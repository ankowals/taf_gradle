package pl.test.demo.sample.base;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.dataset.DataSetExecutorImpl;
import com.github.database.rider.junit5.DBUnitExtension;
import lombok.extern.log4j.Log4j2;
import org.aeonbits.owner.ConfigCache;
import org.awaitility.Duration;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import pl.test.demo.sample.config.EnvironmentConfig;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;

import static com.github.database.rider.core.util.EntityManagerProvider.instance;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.*;

@Log4j2
@ExtendWith(TestLoggerExtension.class)
@ExtendWith(DBUnitExtension.class)
public class BaseTest {

    private static Map<String, String> connectionProperties = new HashMap<>();
    private static EnvironmentConfig envConfig =  ConfigCache.getOrCreate(EnvironmentConfig.class);

    static {
        String url = createOrGetUrl();

        connectionProperties.put("javax.persistence.jdbc.url", url);
        connectionProperties.put("javax.persistence.jdbc.user",  envConfig.getUser());
        connectionProperties.put("javax.persistence.jdbc.password", envConfig.getPass());

        migrateDb(url, envConfig.getUser(), envConfig.getPass());
    }

    protected Map<String, String> getConnectionProperties() {
        return connectionProperties;
    }
    protected ConnectionHolder getConnection(){
        return () -> instance(PersitanceUnits.DOGS_DB.getValue(), getConnectionProperties()).connection();
    }
    protected DataSetExecutorImpl executor() { return DataSetExecutorImpl.instance(getConnection()); }

    private static void migrateDb(String url, String user, String password) {
        Flyway.configure().locations("filesystem:" + Paths.get("src", "test", "resources", "db", "migration")
                .toAbsolutePath().toString())
                .dataSource(url, user, password)
                    .load()
                        .migrate();
    }

    private static String createOrGetUrl() {
        try {
            // e.g. if started once via `docker-compose up`
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(envConfig.getHost(), envConfig.getPort()), 100);
            socket.close();

            return "jdbc:postgresql://" + envConfig.getHost() + ":" + envConfig.getPort() + "/";

        } catch (Exception ex) {
            PostgreSQLContainer dbContainer = new PostgreSQLContainer().withDatabaseName("DOGSDB")
                    .withUsername(envConfig.getUser()).withPassword(envConfig.getPass());
            dbContainer.start();
            awaitForContainer(dbContainer);

            return dbContainer.getJdbcUrl();
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