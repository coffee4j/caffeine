package de.rwth.swc.coffee4j.evaluation.model;


import de.rwth.swc.coffee4j.evaluation.utils.CombinationUtil;
import de.rwth.swc.coffee4j.evaluation.utils.IntArrayWrapper;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that can generate random {@link TestModel} instances according to a {@link RandomModelConfiguration}.
 * <p>
 * It currently only supports the generation of models without constraints.
 */
public class RandomModelGenerator {

    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final RandomModelConfiguration configuration;


    /**
     * Constructor.
     *
     * @param configuration the configuration. This may not be {@code null}.
     */
    public RandomModelGenerator(RandomModelConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

    /**
     * Generate a given number of random models.
     *
     * @param numberOfModels the number of models to generate. Must be positive.
     * @return a collection of random models
     */
    public Collection<TestModel> generateSystemModels(int numberOfModels) {
        if (numberOfModels < 0) {
            throw new IllegalArgumentException("Number of models must be positive.");
        }
        List<TestModel> models = new ArrayList<>(numberOfModels);
        for (int i = 0; i < numberOfModels; i++) {
            String name = "Random_" + UUID.randomUUID().toString();
            models.add(generateSystemModel(name));
        }
        return models;
    }

    private TestModel generateSystemModel(String name) {
        int[] parameters = generateParameters();
        Map<String, int[]> constraints = generateConstraints(parameters);
        TestModel.Builder builder = new TestModel.Builder(name)
                .withParameters(parameters)
                .withConstraints(constraints);
        generateScenarios(builder, constraints);

        return builder.buildModel();
    }

    private void generateScenarios(TestModel.Builder builder, Map<String, int[]> constraints) {

        for (int i = 0; i < configuration.getNumberOfScenarios(); i++) {
            int numberOfActiveFaults = random.nextInt(configuration.getMinNumberOfForbiddenCombinationsInScenario(),
                    configuration.getMaxNumberOfForbiddenCombinationsInScenario());
            List<String> possibleFaults = new ArrayList<>(constraints.keySet());
            List<String> faults = getRandomUniquesFromRange(numberOfActiveFaults, 0, constraints.size())
                    .stream()
                    .map(possibleFaults::get)
                    .collect(Collectors.toList());
            builder.scenario("s" + i)
                    .withStrength(getMinimumStrength(faults.stream().map(constraints::get).collect(Collectors.toList())))
                    .withFaults(faults)
                    .buildScenario();
        }
    }

    private int getMinimumStrength(List<int[]> faults) {
        return faults.stream().mapToInt(CombinationUtil::numberOfSetParameters)
                .max().orElseThrow();
    }

    private Map<String, int[]> generateConstraints(int[] parameters) {
        Map<String, int[]> constraints = new HashMap<>();
        Set<IntArrayWrapper> causes = new HashSet<>(configuration.getNumberOfForbiddenCombinations());

        while (causes.size() < configuration.getNumberOfForbiddenCombinations()) {
            int numberOfInvolvedParameters = random.nextInt(
                    configuration.getMinNumberOfParametersInForbiddenCombination(),
                    configuration.getMaxNumberOfParametersInForbiddenCombination());
            int[] cause = CombinationUtil.emptyCombination(parameters.length);
            for (Integer index : getRandomUniquesFromRange(Math.min(cause.length, numberOfInvolvedParameters), 0, cause.length)) {
                cause[index] = random.nextInt(0, parameters[index]);
            }
            IntArrayWrapper wrapper = IntArrayWrapper.wrap(cause);
            if (!causes.contains(wrapper)) {
                causes.add(wrapper);
                constraints.put("c" + causes.size(), cause);
            }
        }

        return constraints;
    }

    private int[] generateParameters() {
        int noParameters = random.nextInt(configuration.getMinNumberOfParameters(),
                configuration.getMaxNumberOfParameters());
        int[] parameters = new int[noParameters];
        for (int i = 0; i < noParameters; i++) {
            parameters[i] = random.nextInt(configuration.getMinNumberOfParameterValues(),
                    configuration.getMaxNumberOfParameterValues());
        }
        return parameters;
    }

    private List<Integer> getRandomUniquesFromRange(int number, int min, int max) {
        if (number > (max - min)) {
            throw new IllegalArgumentException();
        }
        List<Integer> shuffle = IntStream.range(min, max).boxed().collect(Collectors.toList());
        Collections.shuffle(shuffle);
        return shuffle.subList(0, number);

    }
}
