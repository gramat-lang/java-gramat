package tools.args;

public class ArgEntry {

    private final String name;
    private final String value;

    public ArgEntry(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
