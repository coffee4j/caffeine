package de.rwth.swc.coffee4j.evaluation.persistence.analysis;

import de.rwth.swc.coffee4j.evaluation.analysis.AnalysisResult;
import de.rwth.swc.coffee4j.evaluation.analysis.KeyInfo;
import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.analysis.model.ModelProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.scenario.ScenarioProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.trace.TraceProperties;
import de.rwth.swc.coffee4j.evaluation.model.ModelIdentifier;
import de.rwth.swc.coffee4j.evaluation.model.ScenarioIdentifier;
import de.rwth.swc.coffee4j.evaluation.persistence.jooq.tables.records.ModelRecord;
import de.rwth.swc.coffee4j.evaluation.persistence.jooq.tables.records.PropertyKeyRecord;
import de.rwth.swc.coffee4j.evaluation.trace.ExecutionState;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIdentifier;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.rwth.swc.coffee4j.evaluation.persistence.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

/**
 * {@link AnalysisRepository} that implements connection with a JDBC database.
 * <p>
 * It is initialized with a JOOQ DslContext which should make it mostly independent of the used SQL dialect.
 */
public class AnalysisDatabaseRepository implements AnalysisRepository {

    private final DSLContext db;

    /**
     * Constructor
     *
     * @param db the dsl context connecting to the database
     */
    public AnalysisDatabaseRepository(DSLContext db) {
        this.db = db;
    }

    @Override
    public AnalysisResult get(TraceIdentifier identifier) {
        Result<Record5<Integer, Integer, Integer, Integer, String>> ids = db
                .select(MODEL_ANALYSIS.ID, SCENARIO_ANALYSIS.ID, TRACE_ANALYSIS.ID,
                        TRACE_ANALYSIS.ITERATION, TRACE_ITERATION.STATE)
                .from(TRACE_ANALYSIS)
                .leftJoin(TRACE).onKey()
                .join(TRACE_ITERATION).onKey()
                .join(MODEL).onKey(TRACE.MODEL_ID)
                .join(MODEL_ANALYSIS).onKey(MODEL_ANALYSIS.MODEL_ID)
                .join(SCENARIO_ANALYSIS).on(TRACE.SCENARIO_NAME.eq(SCENARIO_ANALYSIS.SCENARIO_NAME)
                        .and(TRACE.MODEL_ID.eq(SCENARIO_ANALYSIS.MODEL_ID)))
                .where(findTrace(identifier))
                .orderBy(TRACE_ANALYSIS.ITERATION)
                .fetch();

        if (ids.isEmpty()) {
            throw new NoSuchElementException();
        }

        int modelId = ids.get(0).getValue(MODEL_ANALYSIS.ID);
        int scenarioId = ids.get(0).getValue(SCENARIO_ANALYSIS.ID);
        List<Integer> iterationIds = ids.getValues(TRACE_ANALYSIS.ID);
        List<ExecutionState> iterationStates = ids.getValues(TRACE_ITERATION.STATE).stream().map(ExecutionState::valueOf)
                .collect(Collectors.toList());

        ModelProperties modelProperties = createModelProperties(identifier.getModelIdentifier(),
                db.select().from(MODEL_PROPERTIES)
                        .join(PROPERTY_KEY).onKey()
                        .where(MODEL_PROPERTIES.ANALYSIS_ID.eq(modelId))
                        .fetch());

        ScenarioProperties scenarioProperties = createScenarioProperties(identifier.getScenarioIdentifier(),
                db.select().from(SCENARIO_PROPERTIES)
                        .join(PROPERTY_KEY).onKey()
                        .where(SCENARIO_PROPERTIES.ANALYSIS_ID.eq(scenarioId))
                        .fetch());

        List<TraceProperties.IterationProperties> iterationProperties = new ArrayList<>(iterationIds.size());
        for (int i = 0; i < iterationIds.size(); i++) {
            int iterationId = iterationIds.get(i);

            iterationProperties.add(createIterationTrace(db.select().from(TRACE_PROPERTIES)
                    .join(PROPERTY_KEY).onKey()
                    .where(TRACE_PROPERTIES.ANALYSIS_ID.eq(iterationId)).fetch(), iterationStates.get(i)));
        }

        return new AnalysisResult(modelProperties, scenarioProperties,
                new TraceProperties(identifier, iterationProperties));
    }

    private TraceProperties.IterationProperties createIterationTrace(Result<Record> properties, ExecutionState state) {
        return new TraceProperties.IterationProperties(state, extractProperties(properties, TRACE_PROPERTIES.VALUE));
    }

    private ScenarioProperties createScenarioProperties(ScenarioIdentifier scenarioIdentifier, Result<Record> properties) {
        return new ScenarioProperties(scenarioIdentifier, extractProperties(properties, SCENARIO_PROPERTIES.VALUE));
    }

    private ModelProperties createModelProperties(ModelIdentifier modelIdentifier, Result<Record> properties) {
        return new ModelProperties(modelIdentifier, extractProperties(properties, MODEL_PROPERTIES.VALUE));
    }


    private Map<PropertyKey, Number> extractProperties(Result<Record> properties, Field<Double> valueField) {
        Map<PropertyKey, Number> propertyMap = new HashMap<>();
        for (Record property : properties) {
            PropertyKeyRecord keyRecord = property.into(PROPERTY_KEY);
            double value = property.getValue(valueField);
            propertyMap.put(getPropertyKey(keyRecord), value);
        }
        return propertyMap;
    }


    @Override
    public boolean exists(ModelIdentifier identifier) {
        return db.fetchExists(selectOne().from(MODEL_ANALYSIS)
                .join(MODEL).onKey().where(findModel(identifier)));
    }

    @Override
    public boolean exists(ScenarioIdentifier identifier) {
        return db.fetchExists(selectOne().from(SCENARIO_ANALYSIS)
                .join(MODEL).onKey()
                .where(findModel(identifier.getModelIdentifier())
                        .and(SCENARIO_ANALYSIS.SCENARIO_NAME.eq(identifier.getScenarioName()))));
    }

    @Override
    public boolean exists(TraceIdentifier identifier) {
        return db.fetchExists(selectOne().from(TRACE_ANALYSIS)
                .join(TRACE).onKey()
                .join(MODEL).onKey()
                .where(findTrace(identifier)));
    }

    @Override
    public Stream<AnalysisResult> getAll() {

        return db.select(MODEL.NAME, TRACE.SCENARIO_NAME, TRACE.ALGORITHM_NAME)
                .from(TRACE_ANALYSIS)
                .join(TRACE).onKey()
                .join(MODEL).onKey()
                .where(TRACE_ANALYSIS.ITERATION.eq(0))
                .fetchStream()
                .map(r -> new TraceIdentifier(r.getValue(MODEL.NAME),
                        r.getValue(TRACE.SCENARIO_NAME),
                        r.getValue(TRACE.ALGORITHM_NAME)))
                .map(this::get);
    }


    @Override
    public void write(ModelProperties properties) {
        db.transaction(configuration -> {
            DSLContext context = DSL.using(configuration);
            insertKeys(context, properties.getProperties().keySet());
            int modelId = getModelId(context, properties.getIdentifier());
            context.delete(MODEL_ANALYSIS).where(MODEL_ANALYSIS.ID.eq(modelId));
            int analysisId = context.insertInto(MODEL_ANALYSIS, MODEL_ANALYSIS.MODEL_ID)
                    .values(modelId).returning(MODEL_ANALYSIS.ID).fetchOne().getId();
            for (Map.Entry<PropertyKey, Number> entry : properties.getProperties().entrySet()) {
                context.mergeInto(MODEL_PROPERTIES,
                        MODEL_PROPERTIES.ANALYSIS_ID,
                        MODEL_PROPERTIES.PROP_KEY,
                        MODEL_PROPERTIES.VALUE)
                        .select(select(val(analysisId), PROPERTY_KEY.ID, val(entry.getValue().doubleValue()))
                                .from(PROPERTY_KEY)
                                .where(findPropertyKey(entry.getKey())))
                        .execute();
            }
        });
    }

    private int getModelId(DSLContext context, ModelIdentifier identifier) {
        ModelRecord modelRecord = context.selectFrom(MODEL).where(findModel(identifier)).fetchAny();
        if (modelRecord == null) {
            throw new NoSuchElementException("Model " + identifier + " not found in database.");
        }
        return modelRecord.getId();
    }

    @Override
    public void write(ScenarioProperties properties) {
        db.transaction(configuration -> {
            DSLContext context = DSL.using(configuration);
            insertKeys(context, properties.getProperties().keySet());
            int modelId = getModelId(context, properties.getIdentifier().getModelIdentifier());
            context.delete(SCENARIO_ANALYSIS).where(SCENARIO_ANALYSIS.MODEL_ID.eq(modelId))
                    .and(SCENARIO_ANALYSIS.SCENARIO_NAME.eq(properties.getIdentifier().getScenarioName()));
            int analysisId = context.insertInto(SCENARIO_ANALYSIS, SCENARIO_ANALYSIS.MODEL_ID, SCENARIO_ANALYSIS.SCENARIO_NAME)
                    .values(modelId, properties.getIdentifier().getScenarioName()).returning(SCENARIO_ANALYSIS.ID)
                    .fetchOne().getId();
            for (Map.Entry<PropertyKey, Number> entry : properties.getProperties().entrySet()) {
                context.insertInto(SCENARIO_PROPERTIES,
                        SCENARIO_PROPERTIES.ANALYSIS_ID,
                        SCENARIO_PROPERTIES.PROP_KEY,
                        SCENARIO_PROPERTIES.VALUE)
                        .select(select(val(analysisId), PROPERTY_KEY.ID, val(entry.getValue().doubleValue()))
                                .from(PROPERTY_KEY)
                                .where(findPropertyKey(entry.getKey())))
                        .execute();
            }
        });
    }

    @Override
    public void write(TraceProperties traceProperties) {
        db.transaction(configuration -> {
            DSLContext context = DSL.using(configuration);
            int traceId = context.select(TRACE.ID).from(TRACE)
                    .join(MODEL).onKey()
                    .where(findTrace(traceProperties.getIdentifier()))
                    .fetchOptional().orElseThrow(NoSuchElementException::new).value1();

            context.delete(TRACE_ANALYSIS).where(TRACE_ANALYSIS.TRACE_ID.eq(traceId));

            for (int i = 0; i < traceProperties.getPropertiesForIteration().size(); i++) {
                TraceProperties.IterationProperties properties = traceProperties.getPropertiesForIteration().get(i);
                int analysisId = context.insertInto(TRACE_ANALYSIS, TRACE_ANALYSIS.TRACE_ID, TRACE_ANALYSIS.ITERATION)
                        .values(traceId, i)
                        .returning(TRACE_ANALYSIS.ID)
                        .fetchOne().getId();
                insertKeys(context, properties.getProperties().keySet());
                for (Map.Entry<PropertyKey, Number> entry : properties.getProperties().entrySet()) {
                    context.insertInto(TRACE_PROPERTIES,
                            TRACE_PROPERTIES.ANALYSIS_ID,
                            TRACE_PROPERTIES.PROP_KEY,
                            TRACE_PROPERTIES.VALUE)
                            .select(select(val(analysisId), PROPERTY_KEY.ID, val(entry.getValue().doubleValue()))
                                    .from(PROPERTY_KEY)
                                    .where(findPropertyKey(entry.getKey())))
                            .execute();
                }

            }
        });
    }

    @Override
    public void clear() {
        db.transaction(configuration -> {
            DSL.using(configuration).delete(MODEL_ANALYSIS).execute();
            DSL.using(configuration).delete(SCENARIO_ANALYSIS).execute();
            DSL.using(configuration).delete(TRACE_ANALYSIS).execute();
        });
    }

    private Condition findModel(ModelIdentifier identifier) {
        return MODEL.NAME.eq(identifier.getModelName());
    }

    private Condition findTrace(TraceIdentifier identifier) {
        return findModel(identifier.getModelIdentifier())
                .and(TRACE.SCENARIO_NAME.eq(identifier.getScenarioIdentifier().getScenarioName()))
                .and(TRACE.ALGORITHM_NAME.eq(identifier.getAlgorithmName()));
    }

    private Condition findPropertyKey(PropertyKey key) {
        return PROPERTY_KEY.PROP_KEY.eq(key.getKey());
    }

    private void insertKeys(DSLContext context, Collection<PropertyKey> keys) {
        for (PropertyKey key : keys) {
            context.mergeInto(PROPERTY_KEY,
                    PROPERTY_KEY.PROP_KEY, PROPERTY_KEY.DATA_TYPE, PROPERTY_KEY.MIN, PROPERTY_KEY.MAX)
                    .key(PROPERTY_KEY.PROP_KEY)
                    .values(key.getKey(), key.getDomainClass().getName(),
                            key.getMinValue(),
                            key.getMaxValue())
                    .execute();
        }
    }

    @Override
    public KeyInfo getKeyInfo() {
        Map<String, List<PropertyKey>> keys = db.select(val("Model"),
                PROPERTY_KEY.PROP_KEY,
                PROPERTY_KEY.DATA_TYPE,
                PROPERTY_KEY.MIN,
                PROPERTY_KEY.MAX)
                .distinctOn(PROPERTY_KEY.PROP_KEY)
                .from(MODEL_PROPERTIES)
                .join(PROPERTY_KEY).onKey()
                .unionAll(
                        select(val("Scenario"), PROPERTY_KEY.PROP_KEY,
                                PROPERTY_KEY.DATA_TYPE,
                                PROPERTY_KEY.MIN,
                                PROPERTY_KEY.MAX)
                                .distinctOn(PROPERTY_KEY.PROP_KEY)
                                .from(SCENARIO_PROPERTIES)
                                .join(PROPERTY_KEY).onKey()
                ).unionAll(
                        select(val("Trace"), PROPERTY_KEY.PROP_KEY,
                                PROPERTY_KEY.DATA_TYPE,
                                PROPERTY_KEY.MIN,
                                PROPERTY_KEY.MAX)
                                .distinctOn(PROPERTY_KEY.PROP_KEY)
                                .from(TRACE_PROPERTIES)
                                .join(PROPERTY_KEY).onKey()
                ).fetch().intoGroups(r -> (String) r.getValue(0), r -> getPropertyKey(r.into(PROPERTY_KEY)));

        return new KeyInfo(keys.get("Model"), keys.get("Scenario"), keys.get("Trace"));
    }

    private PropertyKey getPropertyKey(PropertyKeyRecord keyRecord) {
        try {
            return new PropertyKey(keyRecord.getPropKey(), Class.forName(keyRecord.getDataType()),
                    keyRecord.getMin(), keyRecord.getMax());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException();
        }
    }
}
