package org.gramat.patterns;

import java.util.Objects;

public class PatternToken implements Pattern {

    public final Pattern pattern;
    public final String token;

    PatternToken(Pattern pattern, String token) {
        // TODO validate pattern types to avoid deep nesting
        this.pattern = Objects.requireNonNull(pattern);
        this.token = Objects.requireNonNull(token);
    }

    @Override
    public String toString() {
        return pattern + " / " + token;
    }
}
