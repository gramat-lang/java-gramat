package org.gramat.expressions.engines;

import org.gramat.expressions.Expression;
import org.gramat.expressions.misc.Reference;
import org.gramat.util.ExpressionMap;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Set;

public class RecursionUtils {

    public static Set<String> findRecursiveNames(Expression expr, ExpressionMap rules) {
        var names = new LinkedHashSet<String>();

        findRecursiveNames(expr, rules, names, new ArrayDeque<>());

        return names;
    }

    private static void findRecursiveNames(Expression expr, ExpressionMap rules, Set<String> names, Deque<String> stack) {
        if (expr instanceof Reference) {
            var ref = (Reference) expr;

            if (names.contains(ref.name)) {
                return;
            }

            var refExpr = rules.find(ref.name);

            if (stack.contains(ref.name)) {
                names.add(stack.peek());
            }
            else {
                stack.push(ref.name);

                findRecursiveNames(refExpr, rules, names, stack);

                stack.pop();
            }
        }
        else {
            for (var child : expr.getChildren()) {
                findRecursiveNames(child, rules, names, stack);
            }
        }
    }

    private RecursionUtils() {}

}
