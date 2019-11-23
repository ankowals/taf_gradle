package pl.test.demo.sample.base;

public enum PersitanceUnits {
    DOGS_DB("dogsDB");

    private String value;

    PersitanceUnits(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}