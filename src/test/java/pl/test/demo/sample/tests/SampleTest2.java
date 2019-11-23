package pl.test.demo.sample.tests;

import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.configuration.DataSetConfig;
import com.github.database.rider.core.util.EntityManagerProvider;
import lombok.extern.log4j.Log4j2;
import org.junit.Rule;
import org.junit.Test;
import pl.test.demo.sample.base.BaseTest;
import pl.test.demo.sample.base.PersitanceUnits;
import pl.test.demo.sample.persistence.Dog;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.extractProperty;

@SuppressWarnings("unchecked")
@Log4j2
public class SampleTest2 extends BaseTest {

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(emProvider.connection());

    @Test
    public void shouldContainDogs() {
        dbUnitRule.getDataSetExecutor()
                .createDataSet(new DataSetConfig("datasets/shouldContainAriaInTheDogsList/csv/dog.csv")
                        .strategy(SeedStrategy.CLEAN_INSERT));

        List<Dog> dogs = emProvider.getEm()
                .createQuery("select d from Dog d")
                .getResultList();

        assertThat(dogs)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2);
    }

    @Test
    public void shouldContainGermanShepherds() {
        dbUnitRule.getDataSetExecutor()
                .createDataSet(new DataSetConfig("datasets/shouldContainGermanShepherds/csv/dog.csv")
                        .strategy(SeedStrategy.INSERT));

        List<Dog> dogs = emProvider.getEm()
                .createQuery("select d from Dog d")
                .getResultList();

        assertThat(extractProperty("breed").ofType(String.class).from(dogs)).contains("'German Shepherd'");
    }

}
