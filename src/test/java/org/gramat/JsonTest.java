package org.gramat;

import com.google.gson.Gson;
import org.gramat.automata.Automaton;
import org.gramat.automata.evaluation.Evaluator;
import org.gramat.automata.tapes.Tape;
import org.gramat.tools.CharInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.Resources;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonTest {

    private final Automaton automaton;

    JsonTest() {
        var grammarFile = "/JsonTest/json.gm";
        var input = CharInput.of(Resources.loadString(grammarFile), grammarFile);
        var gramat = new Gramat();

        automaton = gramat.compile(input);
    }

    @ParameterizedTest
    @ValueSource(strings={
            "/JsonTest/sample-01.json",
            "/JsonTest/sample-02.json",
            "/JsonTest/sample-03.json",
            "/JsonTest/sample-04.json",
            "/JsonTest/sample-05.json",
            "/JsonTest/sample-10.json",
            "/JsonTest/sample-11.json",
            "/JsonTest/sample-12.json",
    })
    void test(String jsonFile) {
        var json = Resources.loadString(jsonFile);
        var expected = parse(json);

        var evaluator = new Evaluator();
        var actual = evaluator.eval(automaton.getInitial(), Tape.of(json, jsonFile));

        assertEquals(expected, actual);
    }

    private static JsonMap parse(String json) {
        var gson = new Gson();

        return gson.fromJson(json, JsonMap.class);
    }

    public static class JsonMap extends LinkedHashMap<String, Object> {}

}
