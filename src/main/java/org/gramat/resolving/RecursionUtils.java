package org.gramat.resolving;

import org.gramat.exceptions.GramatException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.misc.Recursion;
import org.gramat.expressions.misc.Reference;
import org.gramat.util.ExpressionMap;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class RecursionUtils {

    public static boolean isRecursive(Expression expr, String name, ExpressionMap rules) {
        var nameStack = new ArrayDeque<String>();

        nameStack.push(name);

        var result = isRecursive(expr, rules, nameStack);

        nameStack.pop();

        return result;
    }

    public static boolean isRecursive(Expression expr, ExpressionMap rules, Deque<String> nameStack) {
        if (expr instanceof Reference) {
            var refName = ((Reference) expr).name;

            if (nameStack.contains(refName)) {
                // We've walked this path.
                return true;
            }

            var refExpr = rules.get(refName);
            if (refExpr == null) {  // TODO create ExpressionMap
                throw new GramatException("expression not found: " + refName);
            }

            nameStack.push(refName);

            var result = isRecursive(refExpr, rules, nameStack);

            nameStack.pop();

            return result;
        }
        else if (expr instanceof Recursion) {
            // Well... by definition, yes
            return true;
        }

        return anyRecursive(expr.getChildren(), rules, nameStack);
    }

    public static boolean anyRecursive(List<Expression> items, ExpressionMap rules, Deque<String> nameStack) {
        for (var item : items) {
            var result = isRecursive(item, rules, nameStack);

            if (result) {
                return true;
            }
        }

        return false;
    }

    private RecursionUtils() {}

}
