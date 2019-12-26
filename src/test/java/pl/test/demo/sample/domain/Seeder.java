package pl.test.demo.sample.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Log4j2
@Getter
@NoArgsConstructor
@SuppressWarnings("unchecked")
public class Seeder {

    private Map<Class<?>, List<Object>> map = new LinkedHashMap<>();
    private Class<?> klazz;

    public Seeder table(Class<?> klazz) {
        this.klazz = klazz;
        return this;
    }

    public <T> Seeder addRow(T value) {
        if (!map.containsKey(klazz)) {
            map.put(klazz, new LinkedList<>());
        }
        if (klazz.isInstance(value)) {
            map.get(klazz).add(value);
        }
        return this;
    }

    public <T> Seeder clearRow(T value) {
        map.get(klazz).remove(value);
        return this;
    }

    public <T> Seeder clearRowAtIndex(int index) {
        map.get(klazz).remove(index);
        return this;
    }

    public <T> Seeder clearAllRows() {
        map.get(klazz).clear();
        return this;
    }

    public <T> Seeder clearRows(Collection<?> value) {
        map.get(klazz).removeAll(value);
        return this;
    }

    public <T> Seeder addRows(List<T> list) {
        if (!map.containsKey(klazz)) {
            map.put(klazz, list.stream()
                    .filter(klazz::isInstance)
                    .map(klazz::cast)
                    .collect(toList()));
        } else {
            map.get(klazz).addAll(list.stream()
                    .filter(klazz::isInstance)
                    .map(klazz::cast)
                    .collect(toList()));
        }
        return this;
    }

    public <T> List<T> getRows(Class<?> klazz) {
        return (List<T>) map.get(klazz);
    }

    public void insertAll() {

    }

    public void deleteAll() {

    }

}
