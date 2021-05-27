package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.machine.operations.OperationType;
import org.gramat.errors.ErrorFactory;
import org.gramat.expressions.Alternation;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFactory;
import org.gramat.expressions.ExpressionMap;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.Literal;
import org.gramat.expressions.Option;
import org.gramat.expressions.Reference;
import org.gramat.expressions.Repeat;
import org.gramat.expressions.Sequence;
import org.gramat.expressions.Wildcard;
import org.gramat.expressions.Wrapping;
import org.gramat.tools.IdentifierProvider;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ExpressionExpander {

    public static ExpressionProgram run(ExpressionMap dependencies, String main) {
        return run(dependencies, dependencies.find(main));
    }

    public static ExpressionProgram run(ExpressionMap dependencies, Expression main) {
        return new ExpressionExpander(dependencies).expand(main);
    }

    private final ExpressionMap dependencies;
    private final ExpressionMap newDependencies;
    private final IdentifierProvider referenceIds;
    private final Deque<ReferenceMap> referenceStack;
    private final ExpressionFactory factory;

    private ExpressionExpander(ExpressionMap dependencies) {
        this.dependencies = dependencies;
        this.newDependencies = new ExpressionMap();
        this.referenceIds = IdentifierProvider.create(1);
        this.referenceStack = new ArrayDeque<>();
        this.factory = new ExpressionFactory();
    }

    private ExpressionProgram expand(Expression main) {
        log.debug("Expanding main expression...");

        var recursiveNames = computeRecursiveNames(main);
        var newMain = expand(main, recursiveNames);

        log.debug("Expanding completed: {} rule(s)", 1 + newDependencies.size());

        return new ExpressionProgram(newMain, newDependencies.data);
    }

    private Set<String> computeRecursiveNames(Expression main) {
        var names = new LinkedHashSet<String>();
        var stack = new ArrayDeque<String>();

        computeRecursiveNamesLoop(main, stack, names);

        return names;
    }

    private void computeRecursiveNamesLoop(Expression target, ArrayDeque<String> stack, LinkedHashSet<String> names) {
        if (target instanceof Reference ref && !names.contains(ref.name)) {
            if (stack.contains(ref.name)) {
                names.add(stack.getLast());
            }
            else {
                stack.addLast(ref.name);

                var expr = dependencies.find(ref.name);

                computeRecursiveNamesLoop(expr, stack, names);

                stack.removeLast();
            }
        }

        for (var child : target.getChildren()) {
            computeRecursiveNamesLoop(child, stack, names);
        }
    }

    private Expression expand(Expression target, Set<String> recursiveNames) {
        if (target instanceof Wrapping) {
            return expandWrapping((Wrapping) target, recursiveNames);
        }
        else if (target instanceof Alternation) {
            return expandAlternation((Alternation) target, recursiveNames);
        }
        else if (target instanceof Option) {
            return expandOption((Option) target, recursiveNames);
        }
        else if (target instanceof Reference) {
            return expandReference((Reference) target, recursiveNames);
        }
        else if (target instanceof Repeat) {
            return expandRepeat((Repeat) target, recursiveNames);
        }
        else if (target instanceof Sequence) {
            return expandSequence((Sequence) target, recursiveNames);
        }
        else if (target instanceof Literal || target instanceof Wildcard) {
            return target;
        }
        else {
            throw ErrorFactory.internalError("not implemented expression: " + target);
        }
    }

    private List<Expression> expandAll(List<Expression> items, Set<String> recursiveNames) {
        var newItems = new ArrayList<Expression>();
        var changes = 0;

        for (var item : items) {
            var newItem = expand(item, recursiveNames);

            if (newItem != item) {
                changes++;
            }

            newItems.add(newItem);
        }

        if (changes == 0) {
            return items;
        }
        return newItems;
    }

    private Expression expandWrapping(Wrapping target, Set<String> recursiveNames) {
        return target.derive(expand(target.content, recursiveNames));
    }

    private Expression expandAlternation(Alternation target, Set<String> recursiveNames) {
        return target.derive(expandAll(target.items, recursiveNames));
    }

    private Expression expandOption(Option target, Set<String> recursiveNames) {
        return target.derive(expand(target.content, recursiveNames));
    }

    private Expression expandReference(Reference reference, Set<String> recursiveNames) {
        if (recursiveNames.contains(reference.name)) {
            ReferenceMap refMap = null;
            for (var item : referenceStack) {
                if (item.oldName.equals(reference.name)) {
                    refMap = item;
                    break;
                }
            }

            if (refMap == null) {
                var id = referenceIds.next();
                var newName = String.format("%s-%s", reference.name, id);
                var dependency = dependencies.find(reference.name);
                if (dependency == null) {
                    throw ErrorFactory.notFound(reference.name);
                }

                refMap = new ReferenceMap(reference.name, newName);

                log.debug("Expanding {} from {}...", newName, reference.name);

                referenceStack.addFirst(refMap);
                var newDependency = expand(dependency, recursiveNames);
                referenceStack.removeFirst();

                newDependencies.set(newName, factory.wrapping(newDependency.location, OperationType.TOKEN, newName, newDependency));
            }

            return factory.reference(reference.location, refMap.newName);
        }
        else {
            var content = dependencies.find(reference.name);

            return expand(content, recursiveNames);
        }
    }

    private Expression expandRepeat(Repeat target, Set<String> recursiveNames) {
        var newContent = expand(target.content, recursiveNames);
        var newSeparator = target.separator != null ? expand(target.separator, recursiveNames) : null;
        return target.derive(newContent, newSeparator);
    }

    private Expression expandSequence(Sequence target, Set<String> recursiveNames) {
        return target.derive(expandAll(target.items, recursiveNames));
    }

    private static class ReferenceMap {
        public final String oldName;
        public final String newName;

        public ReferenceMap(String oldName, String newName) {
            this.oldName = oldName;
            this.newName = newName;
        }
    }

}
