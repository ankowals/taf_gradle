package pl.test.demo.sample.tests;

import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.configuration.DataSetConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import pl.test.demo.sample.base.BaseTest;
import pl.test.demo.sample.persistence.Dog;

import static com.github.database.rider.core.util.EntityManagerProvider.em;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
public class SampleTest extends BaseTest {

    @Test
    @DisplayName("shouldContainAriaInTheDogsList")
    @ExpectedDataSet(value = "datasets/shouldContainAriaInTheDogsList/yml/expectedOwner.yml",ignoreCols = "id")
    void shouldContainAriaInTheDogsList(TestInfo info) {
        executor().executeScript("datasets/" + info.getTestMethod().get().getName() + "/sql/Add_to_DOG.sql");
        executor().createDataSet(new DataSetConfig("datasets/" + info.getTestMethod().get().getName() + "/csv/owner.csv")
                        .strategy(SeedStrategy.INSERT));

        Dog dog = (Dog) em()
                .createQuery("select d from Dog d where d.id = 4")
                .getSingleResult();

        assertThat(dog.getName()).isEqualTo("Aria");
    }

}
