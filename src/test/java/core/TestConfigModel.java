package core;

public class TestConfigModel {

    private String name;
    private final transient String defaultValue = "default_value";

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }
}

