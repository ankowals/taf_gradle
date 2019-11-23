package pl.test.demo.sample.base;

import com.github.database.rider.core.util.EntityManagerProvider;
import lombok.extern.log4j.Log4j2;
import org.aeonbits.owner.ConfigCache;
import org.awaitility.Duration;
import org.flywaydb.core.Flyway;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.testcontainers.containers.PostgreSQLContainer;
import pl.test.demo.sample.config.EnvironmentConfig;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.*;

@Log4j2
public class BaseTest {

    protected static Map<String,String> dbConnectionProperties;

    private static EnvironmentConfig envConfig =  ConfigCache.getOrCreate(EnvironmentConfig.class);

    static {
        dbConnectionProperties = createOrGetDBConnectionProperties();
        migrateDb(dbConnectionProperties.get("javax.persistence.jdbc.url"),
                dbConnectionProperties.get("javax.persistence.jdbc.user"),
                dbConnectionProperties.get("javax.persistence.jdbc.password"));
    }

    @Rule
    public TestWatcher watchman = new MyTestWatcher();

    @Rule
    public EntityManagerProvider emProvider = EntityManagerProvider.instance(PersitanceUnits.DOGS_DB.getValue(),
            dbConnectionProperties);

    private static void migrateDb(String url, String user, String password) {
        Flyway.configure().locations("filesystem:" + Paths.get("src", "test", "resources", "db", "migration")
                .toAbsolutePath().toString())
                .dataSource(url, user, password)
                    .load()
                        .migrate();
    }

    private static Map<String, String> createOrGetDBConnectionProperties() {
        try {
            // e.g. if started once via `docker-compose up`
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(envConfig.getHost(), envConfig.getPort()), 100);
            socket.close();

            return new HashMap<String, String>() {{
                put("javax.persistence.jdbc.url", "jdbc:postgresql://" + envConfig.getHost() + ":" + envConfig.getPort() + "/");
                put("javax.persistence.jdbc.user",  envConfig.getUser());
                put("javax.persistence.jdbc.password", envConfig.getPass());
            }};
        } catch (Exception ex) {
            PostgreSQLContainer dbContainer = new PostgreSQLContainer();
            dbContainer.start();

            awaitForContainer(dbContainer);

            return new HashMap<String, String>() {{
                put("javax.persistence.jdbc.url", dbContainer.getJdbcUrl());
                put("javax.persistence.jdbc.user",  dbContainer.getUsername());
                put("javax.persistence.jdbc.password", dbContainer.getPassword());
            }};
        }
    }

    private static void awaitForContainer(PostgreSQLContainer dbContainer) {
        await().atMost(5, SECONDS)
                .with().pollDelay(Duration.ZERO)
                    .and().pollInterval(1, SECONDS)
                        .and().pollInSameThread()
                            .until(() -> dbContainer.isRunning());
    }

}