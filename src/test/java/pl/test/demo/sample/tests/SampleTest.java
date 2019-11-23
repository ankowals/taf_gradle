package pl.test.demo.sample.tests;

import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.configuration.DataSetConfig;
import com.github.database.rider.core.util.EntityManagerProvider;
import lombok.extern.log4j.Log4j2;
import org.junit.Rule;
import org.junit.Test;
import pl.test.demo.sample.base.BaseTest;
import pl.test.demo.sample.base.PersitanceUnits;
import pl.test.demo.sample.persistence.Dog;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
public class SampleTest extends BaseTest {

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(emProvider.connection());

    @Test
    @ExpectedDataSet(value = "datasets/shouldContainAriaInTheDogsList/yml/expectedOwner.yml",ignoreCols = "id")
    public void shouldContainAriaInTheDogsList() {
        dbUnitRule.getDataSetExecutor().executeScript( "datasets/shouldContainAriaInTheDogsList/sql/Add_to_DOG.sql");

        dbUnitRule.getDataSetExecutor()
                .createDataSet(new DataSetConfig("datasets/shouldContainAriaInTheDogsList/csv/owner.csv")
                        .strategy(SeedStrategy.INSERT));

        Dog dog = (Dog) emProvider.getEm()
                .createQuery("select d from Dog d where d.id = 4")
                .getSingleResult();

        assertThat(dog.getName()).isEqualTo("Aria");
    }

}
