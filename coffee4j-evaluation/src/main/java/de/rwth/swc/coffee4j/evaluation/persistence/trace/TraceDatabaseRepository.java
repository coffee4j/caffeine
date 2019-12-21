package de.rwth.swc.coffee4j.evaluation.persistence.trace;

import de.rwth.swc.coffee4j.evaluation.model.ModelIdentifier;
import de.rwth.swc.coffee4j.evaluation.model.ScenarioIdentifier;
import de.rwth.swc.coffee4j.evaluation.trace.ExecutionState;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIdentifier;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIteration;
import de.rwth.swc.coffee4j.evaluation.utils.IOUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.stream.Stream;

import static de.rwth.swc.coffee4j.evaluation.persistence.jooq.Tables.*;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectOne;

/**
 * {@link TraceRepository} that implements connection with a JDBC database.
 * <p>
 * It is initialized with a JOOQ DslContext which should make it mostly independent of the used SQL dialect.
 */
public class TraceDatabaseRepository implements TraceRepository {

    private final DSLContext db;

    /**
     * Constructor.
     *
     * @param db the dsl context to the database
     */
    public TraceDatabaseRepository(DSLContext db) {
        this.db = db;
    }


    @Override
    public Trace get(TraceIdentifier identifier) {
        Record4<Integer, Integer, String, String> idRecord = db.select(TRACE.ID, MODEL.ID, TRACE.SCENARIO_NAME, TRACE.ALGORITHM_NAME)
                .from(TRACE)
                .join(MODEL).onKey()
                .where(getIdentifierCondition(identifier)).fetchAny();
        if (idRecord == null) {
            throw new NoSuchElementException();
        }

        Map<Integer, Record3<Integer, String, Integer>> iteration = db
                .select(TRACE_ITERATION.ITERATION, TRACE_ITERATION.STATE, TRACE_ITERATION.TIME).from(TRACE_ITERATION)
                .where(TRACE_ITERATION.TRACE_ID.eq(idRecord.getValue(TRACE.ID))).fetchMap(TRACE_ITERATION.ITERATION);

        Map<Integer, List<Object[]>> testCases = db
                .select(TEST_CASE.ITERATION, TEST_CASE.COMBINATION)
                .from(TEST_CASE)
                .where(TEST_CASE.TRACE_ID.eq(idRecord.getValue(TRACE.ID)))
                .orderBy(TEST_CASE.ITERATION, TEST_CASE.SORT_INDEX)
                .fetchGroups(TRACE_ITERATION.ITERATION, TEST_CASE.COMBINATION);

        Map<Integer, List<Object[]>> results = db
                .select(FAILURE_INDUCING_COMBINATION.ITERATION, FAILURE_INDUCING_COMBINATION.COMBINATION)
                .from(FAILURE_INDUCING_COMBINATION)
                .where(FAILURE_INDUCING_COMBINATION.TRACE_ID.eq(idRecord.getValue(TRACE.ID)))
                .orderBy(FAILURE_INDUCING_COMBINATION.ITERATION, FAILURE_INDUCING_COMBINATION.SORT_INDEX)
                .fetchGroups(TRACE_ITERATION.ITERATION, FAILURE_INDUCING_COMBINATION.COMBINATION);

        Map<Integer, Result<Record3<Integer, String, Boolean>>> assumptions = db
                .select(ASSUMPTION.ITERATION, ASSUMPTION.ASSUMPTION_KEY, ASSUMPTION.SATISFIED)
                .from(ASSUMPTION)
                .where(ASSUMPTION.TRACE_ID.eq(idRecord.getValue(TRACE.ID)))
                .orderBy(ASSUMPTION.ITERATION).fetchGroups(TRACE_ITERATION.ITERATION);

        return createTrace(identifier, iteration, testCases, results, assumptions);
    }

    private Trace createTrace(TraceIdentifier identifier,
                              Map<Integer, Record3<Integer, String, Integer>> iterations,
                              Map<Integer, List<Object[]>> testCases,
                              Map<Integer, List<Object[]>> results,
                              Map<Integer, Result<Record3<Integer, String, Boolean>>> assumptions) {

        Trace.Builder builder = new Trace.Builder()
                .fromIdentifier(identifier);

        for (int iteration = 0; iteration < iterations.size(); iteration++) {

            ExecutionState state = ExecutionState.valueOf(iterations.get(iteration).getValue(TRACE_ITERATION.STATE));
            int time = iterations.get(iteration).getValue(TRACE_ITERATION.TIME);
            TraceIteration.Builder iterationBuilder = builder.iteration().timeMilli(time);
            Optional.ofNullable(testCases.get(iteration)).stream().flatMap(Collection::stream)
                    .map(IOUtils::castArrayToInt).forEach(iterationBuilder::testCase);
            Optional.ofNullable(results.get(iteration)).stream().flatMap(Collection::stream)
                    .map(IOUtils::castArrayToInt).forEach(iterationBuilder::failureInducingCombination);
            Optional.ofNullable(assumptions.get(iteration)).stream().flatMap(Collection::stream)
                    .forEach(r -> iterationBuilder.assumption(r.value2(), r.value3()));
            iterationBuilder.state(state);
        }
        return builder.build();
    }


    private Condition getIdentifierCondition(TraceIdentifier identifier) {
        return TRACE.MODEL_ID.eq(select(MODEL.ID).where(MODEL.NAME.eq(identifier.getModelIdentifier().getModelName())))
                .and(TRACE.SCENARIO_NAME.eq(identifier.getScenarioIdentifier().getScenarioName()))
                .and(TRACE.ALGORITHM_NAME.eq(identifier.getAlgorithmName()));
    }

    @Override
    public boolean exists(TraceIdentifier identifier) {
        return db.fetchExists(selectOne().from(TRACE).join(MODEL).onKey().where(getIdentifierCondition(identifier)));
    }

    @Override
    public Stream<Trace> getAll() {
        return db.select(MODEL.NAME, TRACE.SCENARIO_NAME, TRACE.ALGORITHM_NAME)
                .from(TRACE)
                .join(MODEL).onKey()
                .fetchStream()
                .map(r -> new TraceIdentifier(r.getValue(MODEL.NAME), r.getValue(TRACE.SCENARIO_NAME), r.getValue(TRACE.ALGORITHM_NAME)))
                .map(this::get);
    }

    @Override
    public void write(Trace trace) {
        ModelIdentifier modelIdentifier = trace.getIdentifier().getModelIdentifier();
        ScenarioIdentifier scenarioIdentifier = trace.getIdentifier().getScenarioIdentifier();
        db.transaction(configuration -> {
            DSLContext dslContext = DSL.using(configuration);
            Record1<Integer> modelIdRecord = dslContext.select(MODEL.ID)
                    .from(MODEL)
                    .where(MODEL.NAME.eq(modelIdentifier.getModelName())).fetchAny();
            if (modelIdRecord == null) {
                throw new NoSuchElementException("Model for trace not in repository");
            }
            int modelId = modelIdRecord.value1();
            dslContext.delete(TRACE)
                    .where(TRACE.MODEL_ID.eq(modelId))
                    .and(TRACE.SCENARIO_NAME.eq(scenarioIdentifier.getScenarioName()))
                    .and(TRACE.ALGORITHM_NAME.eq(trace.getIdentifier().getAlgorithmName()))
                    .execute();
            int traceID = dslContext.insertInto(TRACE, TRACE.MODEL_ID, TRACE.SCENARIO_NAME, TRACE.ALGORITHM_NAME)
                    .values(modelId, scenarioIdentifier.getScenarioName(), trace.getIdentifier().getAlgorithmName())
                    .returning(TRACE.ID).fetchOne().getId();
            for (int index = 0; index < trace.getTraceIterations().size(); index++) {
                TraceIteration iteration = trace.getTraceIterations().get(index);
                dslContext.insertInto(TRACE_ITERATION,
                        TRACE_ITERATION.TRACE_ID, TRACE_ITERATION.ITERATION, TRACE_ITERATION.STATE, TRACE_ITERATION.TIME)
                        .values(traceID, index, iteration.getState().toString(), Math.toIntExact(iteration.getExecutionTime()))
                        .execute();
                var testCaseInsert = dslContext.insertInto(TEST_CASE,
                        TEST_CASE.TRACE_ID, TEST_CASE.ITERATION, TEST_CASE.SORT_INDEX, TEST_CASE.COMBINATION);
                for (int testCaseIndex = 0; testCaseIndex < iteration.getTestCases().size(); testCaseIndex++) {
                    testCaseInsert.values(traceID, index, testCaseIndex,
                            IOUtils.castArrayToObj(iteration.getTestCases().get(testCaseIndex)));
                }
                testCaseInsert.execute();
                var resultInsert = dslContext.insertInto(FAILURE_INDUCING_COMBINATION,
                        FAILURE_INDUCING_COMBINATION.TRACE_ID, FAILURE_INDUCING_COMBINATION.ITERATION,
                        FAILURE_INDUCING_COMBINATION.SORT_INDEX,
                        FAILURE_INDUCING_COMBINATION.COMBINATION);
                for (int resultIndex = 0; resultIndex < iteration.getFailureInducingCombinations().size(); resultIndex++) {
                    resultInsert.values(traceID, index, resultIndex,
                            IOUtils.castArrayToObj(iteration.getFailureInducingCombinations().get(resultIndex)));
                }
                resultInsert.execute();
                var assumptionInsert = dslContext.insertInto(ASSUMPTION, ASSUMPTION.TRACE_ID, ASSUMPTION.ITERATION,
                        ASSUMPTION.ASSUMPTION_KEY, ASSUMPTION.SATISFIED);
                for (Map.Entry<String, Boolean> assumption : iteration.getAssumptions().entrySet()) {
                    assumptionInsert.values(traceID, index, assumption.getKey(), assumption.getValue());
                }
                assumptionInsert.execute();
            }
        });
    }

    @Override
    public void clear() {
        db.delete(TRACE).execute();
    }

    @Override
    public TraceInfo info() {
        return new TraceInfo(db.fetchCount(selectOne().from(TRACE)));
    }
}
