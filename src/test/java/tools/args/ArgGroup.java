package tools.args;

import java.util.ArrayList;
import java.util.List;

public class ArgGroup {

    private final List<ArgEntry> entries;

    private String resource;
    private Integer lineNumber;
    private ArgMode mode;
    private boolean ignored;

    public ArgGroup(String resource) {
        this.resource = resource;
        this.entries = new ArrayList<>();
    }

    public String getResource() {
        return resource;
    }

    public void addEntry(ArgEntry entry) {
        entries.add(entry);
    }

    public String getValue(String name) {
        var values = getValues(name);

        if (values.isEmpty()) {
            return null;
        }
        else if (values.size() == 1) {
            return values.get(0);
        }
        else {
            throw new RuntimeException(errorMessage(String.format(
                    "Expected 1 value instead of %s for %s", values.size(), name
            )));
        }
    }

    private String errorMessage(String message) {
        return message + " (" + resource + ":" + lineNumber + ")";
    }

    public List<String> getValues(String name) {
        var values = new ArrayList<String>();

        for (var entry : entries) {
            if (name.equals(entry.getName())) {
                values.add(entry.getValue());
            }
        }

        return values;
    }

    public void setMode(ArgMode mode) {
        this.mode = mode;
    }

    public ArgMode getMode() {
        return mode != null ? mode : ArgMode.NORMAL;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        var message = new StringBuilder();
        for (var entry : entries) {
            if (entry.getValue() != null && !entry.getValue().isBlank()) {
                message.append(entry.getValue().trim());
                break;
            }
        }

        return String.format("%s (%s:%s)", message, resource, lineNumber);
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    public boolean isIgnored() {
        return ignored;
    }
}
