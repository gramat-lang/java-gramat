package org.gramat.automating.engines;

import org.gramat.actions.Action;
import org.gramat.actions.HeapPop;
import org.gramat.actions.MetadataBegin;
import org.gramat.actions.design.ActionMaker;
import org.gramat.actions.design.ActionRole;
import org.gramat.actions.design.ActionScheme;
import org.gramat.actions.design.ActionTemplate;
import org.gramat.tracking.SourceMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ActionEngine {
    public static List<ActionTemplate> joinTemplates(List<ActionTemplate> a, List<ActionTemplate> b) {
        // TODO add collision rules
        var actions = new ArrayList<>(a);
        actions.addAll(b);
        return actions;
    }

    public static List<Action> compileTemplates(List<ActionTemplate> templates, SourceMap sourceMap) {
        var actions = new ArrayList<Action>();

        templates = reduceTemplates(templates);

        templates = sortTemplates(templates);

        for (var template : templates) {
            var action = ActionMaker.make(template);

            actions.add(action);

            sourceMap.addActionLocations(action.id, template.locations);
        }

        return actions;
    }

    private static Set<ActionScheme> collectSchemes(List<ActionTemplate> templates) {
        return templates.stream().map(t -> t.scheme).collect(Collectors.toSet());
    }

    private static Set<ActionRole> collectRoles(List<ActionTemplate> templates) {
        return templates.stream().map(t -> t.role).collect(Collectors.toSet());
    }

    private static Set<String> collectArguments(List<ActionTemplate> templates) {
        return templates.stream().map(t -> t.argument).map(a -> a == null ? "" : a).collect(Collectors.toSet());
    }

    private static List<ActionTemplate> reduceTemplates(List<ActionTemplate> templates) {
        var schemes = collectSchemes(templates);
        var roles = collectRoles(templates);
        var arguments = collectArguments(templates);
        var result = new ArrayList<ActionTemplate>();

        for (var scheme : schemes) {
            for (var role : roles) {
                for (var argument : arguments) {
                    ActionTemplate toAdd = null;

                    for (var template : templates) {
                        if (scheme == template.scheme
                                && role == template.role
                                && Objects.equals(argument, template.argument != null ? template.argument : "")
                                && (toAdd == null || template.ordinal < toAdd.ordinal)) {
                            toAdd = template;
                        }
                    }

                    if (toAdd != null) {
                        result.add(toAdd);
                    }
                }
            }
        }

        return result;
    }

    private static List<ActionTemplate> sortTemplates(List<ActionTemplate> templates) {
        return templates.stream().sorted(Comparator.comparingInt(a -> a.ordinal)).collect(Collectors.toList());
    }
}
