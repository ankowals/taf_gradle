package pl.test.demo.sample.base;

import lombok.extern.log4j.Log4j2;
import org.aeonbits.owner.ConfigCache;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.io.IoBuilder;
import org.flywaydb.core.Flyway;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.testcontainers.containers.PostgreSQLContainer;
import pl.test.demo.sample.config.EnvironmentConfig;

import java.nio.file.Paths;
import java.util.*;

@Log4j2
public class BaseTest {

    private static final PostgreSQLContainer DB_CONTAINER;
    protected static Map<String,String> dbConnectionProperties;

    //used to map properties to methods returning different types
    private static EnvironmentConfig environmentConfig =  ConfigCache.getOrCreate(EnvironmentConfig.class);

    static {
        redirectStdStreamsToLogger();
        log.info("--- EXECUTION OF TEST RUN STARTED ---");

        DB_CONTAINER = new PostgreSQLContainer();
        DB_CONTAINER.start();

        migrateDb(DB_CONTAINER.getJdbcUrl(), DB_CONTAINER.getUsername(), DB_CONTAINER.getPassword());

        dbConnectionProperties = new HashMap<String, String>() {{
            put("javax.persistence.jdbc.url", DB_CONTAINER.getJdbcUrl());
            put("javax.persistence.jdbc.user",  DB_CONTAINER.getUsername());
            put("javax.persistence.jdbc.password", DB_CONTAINER.getPassword());
        }};

        log.info("Current environment is " + environmentConfig.currentEnv());
    }

    //either share db connection details like this or via environmentConfig singleton
    public static PostgreSQLContainer getDbContainer() {
        return DB_CONTAINER;
    }

    //dependency injection shall go here

    @Rule
    public TestWatcher watchman = new MyTestWatcher();

    private static void migrateDb(String url, String user, String password) {
        Flyway.configure().locations("filesystem:" + Paths.get("src", "test", "resources", "db", "migration").toAbsolutePath().toString())
                .dataSource(url, user, password)
                .load()
                .migrate();
    }

    private static void redirectStdStreamsToLogger() {
        System.setOut(IoBuilder.forLogger(LogManager.getLogger())
                        .setLevel(Level.DEBUG).buildPrintStream()
        );
        System.setErr(IoBuilder.forLogger(LogManager.getLogger())
                .setLevel(Level.WARN).buildPrintStream()
        );
    }

}
