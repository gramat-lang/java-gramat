package org.gramat.machine.links;

import org.gramat.actions.Action;
import org.gramat.actions.Actions;
import org.gramat.actions.ActionsW;
import org.gramat.machine.nodes.Node;
import org.gramat.patterns.Pattern;

import java.util.Objects;

public class LinkPattern implements Link {

    private final Node source;
    private final Node target;
    private final ActionsW beforeActions;
    private final ActionsW afterActions;
    private final Pattern pattern;

    LinkPattern(Node source, Node target, Pattern pattern) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
        this.pattern = Objects.requireNonNull(pattern);
        this.beforeActions = Actions.createW();
        this.afterActions = Actions.createW();
    }

    public Node getSource() { return source; }
    public Node getTarget() { return target; }

    public Actions getBeforeActions() { return beforeActions; }
    public Actions getAfterActions() { return afterActions; }

    public boolean isEmpty() {
        return false;
    }

    public boolean hasPattern() {
        return true;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void addBeforeActions(Action action) {
        beforeActions.append(action);
    }

    public void addBeforeActions(Actions actions) {
        this.beforeActions.append(actions);
    }

    public void addAfterActions(Actions actions) {
        this.afterActions.append(actions);
    }

    public void addAfterActions(Action action) {
        afterActions.append(action);
    }
}
