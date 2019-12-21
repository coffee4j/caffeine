package de.rwth.swc.coffee4j.evaluation.model.ctw;

import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.persistence.antlr.CtwFileBaseVisitor;
import de.rwth.swc.coffee4j.evaluation.persistence.antlr.CtwFileParser;
import de.rwth.swc.coffee4j.evaluation.utils.CombinationUtil;
import de.rwth.swc.coffee4j.evaluation.utils.IntArrayWrapper;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class CtwModelTransformer extends CtwFileBaseVisitor<TestModel> {

    private static final int MAX_STRENGTH = 4;
    private static final int FAULTS_PER_STRENGTH = 2;
    private static final int NUMBER_OF_SCENARIOS_PER_FAULT = 2;
    private static final int MAX_NUMBER_OF_FAULTS_IN_SCENARIO = 3;
    private static final boolean GENERATE_SCENARIOS = true;
    private final NameGenerator scenarioNameGenerator = new NameGenerator("S");
    private final NameGenerator constraintNameGenerator = new NameGenerator("C");
    private final Vocabulary vocabulary;
    private final ConstraintTransformer transformer;
    private final List<String> parameterOrder = new ArrayList<>();
    private final Map<String, Parameter> parameters = new HashMap<>();
    private final Map<String, Constraint> constraints = new HashMap<>();
    private final Map<String, List<String>> forbiddenCombinations = new HashMap<>();
    private final NameGenerator generatedFaultNameGenerator = new NameGenerator("F");
    private TestModel.Builder builder;

    CtwModelTransformer(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
        this.transformer = new NaiveConstraintTransformer(parameters, parameterOrder);
    }

    @Override
    public TestModel visitModel(CtwFileParser.ModelContext ctx) {
        builder = new TestModel.Builder(ctx.modelId.getText());
        ctx.parameters.forEach(this::visitParameter);
        int[] parameterArray = parameterOrder.stream()
                .mapToInt(name -> this.parameters.get(name).getNumberOfValues())
                .toArray();
        builder.withParameters(parameterArray);
        ConstraintVisitor constraintVisitor = new ConstraintVisitor(this.parameters, vocabulary);
        for (CtwFileParser.ConstraintContext constraint : ctx.constraints) {
            String name = constraintNameGenerator.getOrGenerate(constraint.id);
            // invert constraint to describe forbidden combinations
            constraints.put(name, new NotConstraint(constraintVisitor.visit(constraint)));
        }
        Set<IntArrayWrapper> fbCombinations = new HashSet<>();
        for (Map.Entry<String, Constraint> constraintEntry : constraints.entrySet()) {
            String parent = constraintEntry.getKey();
            forbiddenCombinations.put(parent, new ArrayList<>());
            List<int[]> forbidden = transformer.transformConstraint(constraintEntry.getValue());
            for (int i = 0; i < forbidden.size(); i++) {
                int[] combination = forbidden.get(i);
                String name = forbidden.size() == 1 ? constraintEntry.getKey() : parent + "." + i;
                forbiddenCombinations.get(parent).add(name);
                fbCombinations.add(IntArrayWrapper.wrap(combination));
                builder.withConstraint(name, combination);
            }
        }
        ctx.scenarios.forEach(this::visitScenario);
        if (GENERATE_SCENARIOS) {
            createScenarios(parameterArray, fbCombinations);
        }
        return builder.buildModel();
    }

    private void createScenarios(int[] parameterArray, Set<IntArrayWrapper> fbCombinations) {
        Map<String, int[]> generatedFaults = new HashMap<>();
        for (int numFaults = 0; numFaults < FAULTS_PER_STRENGTH; numFaults++) {
            for (int i = 1; i <= MAX_STRENGTH; i++) {
                int[] fault = generateNewFault(fbCombinations, i, parameterArray);
                String name = generatedFaultNameGenerator.getNextName();

                generatedFaults.put(name, fault);
                builder.withConstraint(name, fault);
            }
        }

        List<String> shuffle = new ArrayList<>(generatedFaults.keySet());
        for (int numberOfFaults = 1; numberOfFaults <= MAX_NUMBER_OF_FAULTS_IN_SCENARIO; numberOfFaults++) {
            for (int i = 0; i < NUMBER_OF_SCENARIOS_PER_FAULT; i++) {
                Collections.shuffle(shuffle);
                List<String> chosen = new ArrayList<>(shuffle.subList(0, numberOfFaults));
                int strength = chosen.stream().map(generatedFaults::get)
                        .mapToInt(CombinationUtil::numberOfSetParameters).max().orElseThrow();
                builder.scenario(scenarioNameGenerator.getNextName())
                        .withStrength(strength)
                        .withFaults(chosen)
                        .withConstraints(forbiddenCombinations.values().stream()
                                .flatMap(Collection::stream).collect(Collectors.toList()))
                        .buildScenario();
            }
        }
    }

    private int[] generateNewFault(Set<IntArrayWrapper> fbCombinations, int size, int[] parameters) {
        int[] result = CombinationUtil.emptyCombination(parameters.length);
        do {
            List<Integer> shuffle = IntStream.range(0, parameters.length).boxed().collect(Collectors.toList());
            Collections.shuffle(shuffle);
            for (Integer index : shuffle.subList(0, size)) {
                result[index] = ThreadLocalRandom.current().nextInt(0, parameters[index]);
            }
        } while (fbCombinations.contains(IntArrayWrapper.wrap(result)));
        return result;
    }


    @Override
    public TestModel visitBooleanParameter(CtwFileParser.BooleanParameterContext ctx) {
        addParameter(new BooleanParameter(ctx.id.getText()));
        return null;
    }

    @Override
    public TestModel visitRangeParameter(CtwFileParser.RangeParameterContext ctx) {
        int start = Integer.parseInt(ctx.start.getText());
        int end = Integer.parseInt(ctx.end.getText());
        addParameter(new RangeParameter(ctx.id.getText(), start, end));
        return null;
    }

    @Override
    public TestModel visitEnumParameter(CtwFileParser.EnumParameterContext ctx) {
        addParameter(new EnumParameter(ctx.id.getText(), ctx.element().stream().map(RuleContext::getText)
                .collect(Collectors.toList())));
        return null;
    }

    private void addParameter(Parameter parameter) {
        if (parameters.containsKey(parameter.getName())) {
            throw new InputMismatchException("Trying to declare parameter " + parameter.getName() + " twice.");
        }
        parameterOrder.add(parameter.getName());
        parameters.put(parameter.getName(), parameter);
    }

    @Override
    public TestModel visitScenario(CtwFileParser.ScenarioContext ctx) {
        String name = scenarioNameGenerator.getOrGenerate(ctx.id);
        TestScenario.Builder scenarioBuilder = this.builder.scenario(name)
                .withStrength(Integer.parseInt(ctx.strength.getText()));
        Set<String> faults = new HashSet<>();
        for (Token fault : ctx.faults) {
            List<String> subConstraints = forbiddenCombinations.get(fault.getText());
            if (subConstraints == null) {
                throw new InputMismatchException("Referencing unknown constraint in scenario " + name + ".");
            }
            faults.addAll(subConstraints);
            scenarioBuilder.withFaults(subConstraints);
        }
        if (ctx.allConstraints != null) {
            scenarioBuilder.withConstraints(forbiddenCombinations.values().stream()
                    .flatMap(List::stream)
                    .filter(constraint -> !faults.contains(constraint))
                    .collect(Collectors.toList()));
        } else {
            for (Token constraint : ctx.constraints) {
                List<String> subConstraints = forbiddenCombinations.get(constraint.getText());
                if (subConstraints == null) {
                    throw new InputMismatchException("Referencing unknown constraint in scenario " + name + ".");
                }
                scenarioBuilder.withConstraints(subConstraints);
            }
        }
        scenarioBuilder.buildScenario();
        return null;
    }
}
