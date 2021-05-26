package org.gramat.expressions;

import org.gramat.errors.ErrorFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExpressionMap {

    public static ExpressionMap of(List<ExpressionRule> rules) {
        var data = new LinkedHashMap<String, Expression>();

        for (var rule : rules) {
            if (data.containsKey(rule.name)) {
                throw ErrorFactory.keyAlreadyExists(rule.name);
            }

            data.put(rule.name, rule.expression);
        }

        return new ExpressionMap(Collections.unmodifiableMap(data));
    }

    public static ExpressionMap of(Map<String, Expression> data) {
        return new ExpressionMap(Collections.unmodifiableMap(new LinkedHashMap<>(data)));
    }

    public final Map<String, Expression> data; // TODO refactor this class

    public ExpressionMap() {
        this.data = new LinkedHashMap<>();
    }

    private ExpressionMap(Map<String, Expression> data) {
        this.data = data;
    }

    public Expression find(String name) {
        var expr = data.get(name);

        if (expr == null) {
            throw ErrorFactory.notFound("expression not found: " + name);
        }

        return expr;
    }

    public List<ExpressionRule> getRules() {
        var rules = new ArrayList<ExpressionRule>();

        data.forEach((key, value) -> rules.add(new ExpressionRule(key, value)));

        return rules;
    }

    public int size() {
        return data.size();
    }

    public void set(String name, Expression expression) {
        data.put(name, expression);
    }
}
