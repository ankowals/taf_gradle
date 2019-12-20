package pl.test.demo.sample.tests;

import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.configuration.DataSetConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import pl.test.demo.sample.base.BaseTest;
import pl.test.demo.sample.domain.Seeder;
import pl.test.demo.sample.persistence.Dog;

import java.util.LinkedList;
import java.util.List;

import static com.github.database.rider.core.util.EntityManagerProvider.em;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
public class SampleTest extends BaseTest {

    @Test
    @DisplayName("shouldContainAriaInTheDogsList")
    @ExpectedDataSet(value = "datasets/shouldContainAriaInTheDogsList/yml/expectedOwner.yml", ignoreCols = "id")
    void shouldContainAriaInTheDogsList(TestInfo info) {
        executor().executeScript("datasets/" + info.getTestMethod().get().getName() + "/sql/Add_to_DOG.sql");
        executor().createDataSet(new DataSetConfig("datasets/" + info.getTestMethod().get().getName() + "/csv/owner.csv").cleanAfter(true)
                        .strategy(SeedStrategy.INSERT));

        Dog dog = (Dog) em()
                .createQuery("select d from Dog d where d.id = 4")
                .getSingleResult();

        assertThat(dog.getName()).isEqualTo("Aria");
    }

    @Test
    @DisplayName("seederCheck")
    void shouldSeedDataAsReqestedByTheSeeder() {
        Dog dog1 = new Dog();
        dog1.setName("Aria");

        Dog dog2 = new Dog();
        dog2.setName("Lena");

        Dog dog3 = new Dog();
        dog3.setName("Draco");

        Dog dog4 = new Dog();
        dog4.setName("Sara");

        Dog dog5 = new Dog();
        dog5.setName("Ursej");

        List<Dog> dogs = new LinkedList<>();
        dogs.add(dog4);
        dogs.add(dog5);

        Seeder seeder = new Seeder()
                .table(Dog.class)
                .addRow(dog1)
                .addRow(dog2)
                .addRow(dog3)
                .addRows(dogs);

        List<Dog> result = seeder.getRows(Dog.class);

        assertThat(seeder.getRows(Dog.class).size()).isEqualTo(5);
        assertThat(result.get(1).getName()).isEqualTo("Lena");
        assertThat(result.get(4).getName()).isEqualTo("Ursej");
    }

}
