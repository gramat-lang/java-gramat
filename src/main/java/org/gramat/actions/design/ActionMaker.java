package org.gramat.actions.design;

import org.gramat.actions.Action;
import org.gramat.actions.HeapPop;
import org.gramat.actions.HeapPush;
import org.gramat.actions.ListBegin;
import org.gramat.actions.ListEnd;
import org.gramat.actions.MetadataBegin;
import org.gramat.actions.MetadataEnd;
import org.gramat.actions.ObjectBegin;
import org.gramat.actions.ObjectEnd;
import org.gramat.actions.PropertyBegin;
import org.gramat.actions.PropertyEnd;
import org.gramat.actions.TextBegin;
import org.gramat.actions.TextEnd;

public class ActionMaker {
    public static Action make(ActionTemplate template) {
        return make(template.scheme, template.role, template.ordinal, template.argument);
    }

    public static Action make(ActionScheme scheme, ActionRole role, int id, String argument) {
        switch (scheme) {
            case WRAP_RECURSION: return makeWrapRecursion(role, id, argument);
            case CREATE_OBJECT: return makeCreateObject(role, id, argument);
            case CREATE_LIST: return makeCreateList(role, id, argument);
            case CREATE_TEXT: return makeCreateText(role, id, argument);
            case SET_METADATA: return makeSetMetadata(role, id, argument);
            case SET_PROPERTY: return makeSetProperty(role, id, argument);
            default: throw new RuntimeException();
        }
    }

    private static Action makeWrapRecursion(ActionRole role, int id, String argument) {
        switch (role) {
            case BEGIN: return new HeapPush(id, argument);
            case END: return new HeapPop(id, argument);
            default: throw new RuntimeException();
        }
    }

    private static Action makeCreateObject(ActionRole role, int id, String argument) {
        switch (role) {
            case BEGIN: return new ObjectBegin(id);
            case END: return new ObjectEnd(id, argument);
            default: throw new RuntimeException();
        }
    }

    private static Action makeCreateList(ActionRole role, int id, String argument) {
        switch (role) {
            case BEGIN: return new ListBegin(id);
            case END: return new ListEnd(id, argument);
            default: throw new RuntimeException();
        }
    }

    private static Action makeCreateText(ActionRole role, int id, String argument) {
        switch (role) {
            case BEGIN: return new TextBegin(id);
            case END: return new TextEnd(id, argument);
            default: throw new RuntimeException();
        }
    }

    private static Action makeSetMetadata(ActionRole role, int id, String argument) {
        switch (role) {
            case BEGIN: return new MetadataBegin(id);
            case END: return new MetadataEnd(id, argument);
            default: throw new RuntimeException();
        }
    }

    private static Action makeSetProperty(ActionRole role, int id, String argument) {
        switch (role) {
            case BEGIN: return new PropertyBegin(id);
            case END: return new PropertyEnd(id, argument);
            default: throw new RuntimeException();
        }
    }
}
