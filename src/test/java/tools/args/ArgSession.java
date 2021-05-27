package tools.args;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArgSession implements Iterable<ArgGroup> {

    private final List<ArgGroup> groups;

    public ArgSession() {
        groups = new ArrayList<>();
    }

    public void addGroup(ArgGroup group) {
        this.groups.add(group);
    }

    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public Iterator<ArgGroup> iterator() {
        return groups.iterator();
    }
}
