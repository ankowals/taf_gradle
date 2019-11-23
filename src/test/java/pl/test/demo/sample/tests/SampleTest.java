package pl.test.demo.sample.tests;

import com.github.database.rider.core.DBUnitRule;
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
    public void shouldContainAriaInTheDogsList() {
        dbUnitRule.getDataSetExecutor().executeScript( "datasets/shouldContainAriaInTheDogsList/sql/Add_to_DOG.sql");

        Dog dog = (Dog) emProvider.getEm()
                .createQuery("select d from Dog d where d.id = 4")
                .getSingleResult();

        assertThat(dog.getName()).isEqualTo("Aria");
    }

}
