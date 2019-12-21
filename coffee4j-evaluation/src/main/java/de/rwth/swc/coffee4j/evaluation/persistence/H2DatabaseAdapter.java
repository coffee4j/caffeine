package de.rwth.swc.coffee4j.evaluation.persistence;

import de.rwth.swc.coffee4j.evaluation.persistence.analysis.AnalysisDatabaseRepository;
import de.rwth.swc.coffee4j.evaluation.persistence.analysis.AnalysisRepository;
import de.rwth.swc.coffee4j.evaluation.persistence.model.ModelDatabaseRepository;
import de.rwth.swc.coffee4j.evaluation.persistence.model.ModelRepository;
import de.rwth.swc.coffee4j.evaluation.persistence.trace.TraceDatabaseRepository;
import de.rwth.swc.coffee4j.evaluation.persistence.trace.TraceRepository;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * {@link DatabaseAdapter} that provides the connection to a H2 database over JDBC.
 * <p>
 * This allows for using the database in-memory by calling the empty constructor, or file-based by calling the argument
 * constructor. Internally it delegates to a {@link ModelDatabaseRepository}, a {@link TraceDatabaseRepository} and a
 * {@link AnalysisDatabaseRepository}. These are initialized with a JOOQ DslContext. This allows for potentially reusing
 * these classes with other database types.
 * <p>
 * Database initialization is handled by flyway, which creates and updates the schema if necessary.
 * <p>
 * This class keeps an open JDBC connection and has to be closed after using.
 */
public class H2DatabaseAdapter implements DatabaseAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(H2DatabaseAdapter.class);

    private Connection connection;
    private DSLContext context;
    private ModelRepository modelRepository;
    private TraceRepository traceRepository;
    private AnalysisRepository analysisRepository;

    /**
     * Constructor that creates the adapter connected to an in-memory database called "testdb".
     *
     * @throws IOException when the connection fails
     */
    public H2DatabaseAdapter() throws IOException {
        String connectionPath = "jdbc:h2:mem:testdb";
        init(connectionPath);
    }

    /**
     * Constructor that creates the adapter connected to an file-based database from the given path.
     * <p>
     * This path may not include the file extension. THis means the file at "data/data.mv.db" should be given by the
     * path "data/data".
     *
     * @param path the path to the database. It must not be {@code null}.
     * @throws IOException when the connection fails
     */
    public H2DatabaseAdapter(Path path) throws IOException {
        String connectionPath = "jdbc:h2:file:" + Objects.requireNonNull(path).toAbsolutePath();
        init(connectionPath);
    }

    private void init(String connectionPath) throws IOException {
        LOGGER.debug("Connecting to db {}.", connectionPath);
        try {
            connection = DriverManager.getConnection(connectionPath, "eval", "eval");
        } catch (SQLException e) {
            throw new IOException(e);
        }
        Flyway flyway = Flyway.configure().dataSource(connectionPath, "eval", "eval").load();
        flyway.migrate();
        context = DSL.using(new DefaultConfiguration().set(connection).set(SQLDialect.H2));
        this.modelRepository = new ModelDatabaseRepository(context);
        this.traceRepository = new TraceDatabaseRepository(context);
        this.analysisRepository = new AnalysisDatabaseRepository(context);
    }


    @Override
    public void close() throws IOException {
        context.close();
        try {
            connection.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public ModelRepository getModelRepository() {
        return modelRepository;
    }

    @Override
    public AnalysisRepository getAnalysisRepository() {
        return analysisRepository;
    }

    @Override
    public TraceRepository getTraceRepository() {
        return traceRepository;
    }
}
