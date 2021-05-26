package org.gramat.patterns;

import java.util.ArrayList;
import java.util.List;

public class PatternFactory {

    private static final List<Pattern> PATTERNS = new ArrayList<>();

    public static PatternChar character(char value) {
        for (var pattern : PATTERNS) {
            if (pattern instanceof PatternChar pc && pc.value == value) {
                return pc;
            }
        }

        var pattern = new PatternChar(value);

        PATTERNS.add(pattern);

        return pattern;
    }

    public static PatternRange range(char begin, char end) {
        for (var pattern : PATTERNS) {
            if (pattern instanceof PatternRange pr && pr.begin == begin && pr.end == end) {
                return pr;
            }
        }

        var pattern = new PatternRange(begin, end);

        PATTERNS.add(pattern);

        return pattern;
    }

    public static PatternToken token(Pattern pattern, String token) {
        for (var s : PATTERNS) {
            if (s instanceof PatternToken pt && pt.pattern == pattern && pt.token.equals(token)) {
                return pt;
            }
        }

        var pt = new PatternToken(pattern, token);

        PATTERNS.add(pt);

        return pt;
    }

    public static PatternReference reference(String name) {
        for (var s : PATTERNS) {
            if (s instanceof PatternReference sr && sr.name.equals(name)) {
                return sr;
            }
        }

        var sr = new PatternReference(name);

        PATTERNS.add(sr);

        return sr;
    }

    private PatternFactory() {}
}
