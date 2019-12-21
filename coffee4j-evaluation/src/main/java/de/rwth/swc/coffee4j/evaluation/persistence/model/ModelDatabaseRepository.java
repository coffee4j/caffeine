package de.rwth.swc.coffee4j.evaluation.persistence.model;

import de.rwth.swc.coffee4j.evaluation.model.ModelIdentifier;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.persistence.jooq.tables.records.ForbiddenCombinationRecord;
import de.rwth.swc.coffee4j.evaluation.persistence.jooq.tables.records.ModelRecord;
import de.rwth.swc.coffee4j.evaluation.persistence.jooq.tables.records.ScenarioRecord;
import de.rwth.swc.coffee4j.evaluation.utils.IOUtils;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static de.rwth.swc.coffee4j.evaluation.persistence.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

/**
 * {@link ModelRepository} that implements connection with a JDBC database.
 * <p>
 * It is initialized with a JOOQ DslContext which should make it mostly independent of the used SQL dialect.
 */
public class ModelDatabaseRepository implements ModelRepository {


    private final DSLContext db;

    /**
     * Constructor.
     *
     * @param db the dsl context to the database
     */
    public ModelDatabaseRepository(DSLContext db) {
        this.db = db;
    }


    @Override
    public TestModel get(ModelIdentifier identifier) {
        ModelRecord modelRecord = db
                .selectFrom(MODEL)
                .where(MODEL.NAME.eq(identifier.getModelName()))
                .fetchOptional()
                .orElseThrow(NoSuchElementException::new);

        Result<ScenarioRecord> scenarioRecords = db
                .selectFrom(SCENARIO)
                .where(SCENARIO.MODEL_ID.eq(modelRecord.getId()))
                .fetch();

        Result<ForbiddenCombinationRecord> forbiddenCombinationRecords = db
                .selectFrom(FORBIDDEN_COMBINATION)
                .where(FORBIDDEN_COMBINATION.MODEL_ID.eq(modelRecord.getId()))
                .fetch();

        return createModel(modelRecord, scenarioRecords, forbiddenCombinationRecords);
    }

    @Override
    public boolean exists(ModelIdentifier identifier) {
        return db.fetchExists(selectOne().from(MODEL).where(MODEL.NAME.eq(identifier.getModelName())));
    }

    @Override
    public Stream<TestModel> getAll() {
        return db.select(MODEL.NAME).from(MODEL).fetchStream()
                .map(r -> new ModelIdentifier(r.getValue(MODEL.NAME)))
                .map(this::get);
    }

    private TestModel createModel(ModelRecord modelRecord,
                                  Result<ScenarioRecord> scenarioRecords,
                                  Result<ForbiddenCombinationRecord> forbiddenCombinationRecords) {

        TestModel.Builder builder = new TestModel.Builder(modelRecord.getName())
                .withParameters(IOUtils.castArrayToInt(modelRecord.getParameters()));

        for (ForbiddenCombinationRecord forbiddenCombinationRecord : forbiddenCombinationRecords) {
            builder.withConstraint(forbiddenCombinationRecord.getName(),
                    IOUtils.castArrayToInt(forbiddenCombinationRecord.getCombination()));
        }

        for (ScenarioRecord scenarioRecord : scenarioRecords) {
            builder.scenario(scenarioRecord.getName())
                    .withStrength(scenarioRecord.getStrength())
                    .withConstraints(IOUtils.castArrayToString(scenarioRecord.getModelConstraints()))
                    .withFaults(IOUtils.castArrayToString(scenarioRecord.getFaults()))
                    .buildScenario();
        }

        return builder.buildModel();
    }

    @Override
    public void write(TestModel model) {
        db.transaction(configuration -> {
            DSL.using(configuration).delete(MODEL)
                    .where(MODEL.NAME.eq(model.getIdentifier().getModelName())).
                    execute();
            Integer id = DSL.using(configuration).insertInto(MODEL, MODEL.NAME, MODEL.PARAMETERS)
                    .values(model.getIdentifier().getModelName(), IOUtils.castArrayToObj(model.getParameters()))
                    .returning(MODEL.ID)
                    .fetchOne().getId();
            for (TestScenario scenario : model.getScenarios().values()) {
                DSL.using(configuration).insertInto(SCENARIO, SCENARIO.MODEL_ID, SCENARIO.NAME,
                        SCENARIO.STRENGTH, SCENARIO.FAULTS, SCENARIO.MODEL_CONSTRAINTS)
                        .values(id, scenario.getIdentifier().getScenarioName(), scenario.getStrength(),
                                scenario.getFaults().toArray(), scenario.getConstraints().toArray()).execute();
            }
            for (Map.Entry<String, int[]> constraint : model.getConstraints().entrySet()) {
                DSL.using(configuration).insertInto(FORBIDDEN_COMBINATION, FORBIDDEN_COMBINATION.MODEL_ID, FORBIDDEN_COMBINATION.NAME,
                        FORBIDDEN_COMBINATION.COMBINATION)
                        .values(id, constraint.getKey(), IOUtils.castArrayToObj(constraint.getValue())).execute();
            }
        });

    }

    @Override
    public void clear() {
        db.delete(MODEL).execute();
    }

    @Override
    public ModelInfo info() {
        return db.select(field(selectCount().from(MODEL)), field(selectCount().from(SCENARIO)))
                .fetchAny().into(ModelInfo.class);
    }

}
