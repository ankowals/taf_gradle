package pl.test.demo.sample.tests;

import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.configuration.DataSetConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.test.demo.sample.base.BaseTest;
import pl.test.demo.sample.persistence.Dog;

import java.util.List;

import static com.github.database.rider.core.util.EntityManagerProvider.em;
import static com.github.database.rider.core.util.EntityManagerProvider.instance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.extractProperty;

@SuppressWarnings("unchecked")
@Log4j2
public class SampleTest2 extends BaseTest {

    @Nested
    @DisplayName("shouldContainDogs")
    class shouldContainTest extends BaseTest {

        private String datasetName = "shouldContainGermanShepherds";

        @Test
        @DisplayName("shouldContainTwoDogs")
        void shouldContainDogs() {
            executor().createDataSet(new DataSetConfig("datasets/" + datasetName + "/csv/dog.csv")
                            .strategy(SeedStrategy.CLEAN_INSERT));

            List<Dog> dogs = em()
                    .createQuery("select d from Dog d")
                    .getResultList();

            assertThat(dogs)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(2);
        }

        @Test
        @DisplayName("shouldContainAtLeastOneGermanShepherd")
        void shouldContainGermanShepherds() {
            executor().createDataSet(new DataSetConfig("datasets/" + datasetName + "/csv/dog.csv")
                            .strategy(SeedStrategy.INSERT));

            List<Dog> dogs = em()
                    .createQuery("select d from Dog d")
                    .getResultList();

            assertThat(extractProperty("breed").ofType(String.class).from(dogs)).contains("'German Shepherd'");
        }
    }
}
