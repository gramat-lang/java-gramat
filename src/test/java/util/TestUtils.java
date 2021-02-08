package util;

import org.beat.Beat;
import org.gramat.Gramat;
import org.gramat.eval.EvalProgram;
import org.gramat.inputs.InputCharSequence;
import org.gramat.logging.PrintStreamLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestUtils {

    private TestUtils() {}

    public static String loadString(String resource) {
        var data = loadBytes(resource);

        return new String(data, StandardCharsets.UTF_8);
    }

    private static byte[] loadBytes(String resource) {
        try (var input = loadInputStream(resource)) {
            return input.readAllBytes();
        }
        catch (IOException e) {
            throw new AssertionError("error reading resource: " + resource, e);
        }
    }

    private static InputStream loadInputStream(String resource) {
        var stream = TestUtils.class.getResourceAsStream(resource);
        if (stream == null) {
            throw new AssertionError("resource not found: " + resource);
        }
        return stream;
    }

    private static boolean exists(String resource) {
        var stream = TestUtils.class.getResourceAsStream(resource);
        if (stream == null) {
            return false;
        }
        try {
            stream.close();
        } catch (IOException e) {
            throw new AssertionError("error closing resource", e);
        }
        return true;
    }

    public static EvalProgram loadProgram(String resource) {
        var logger = new PrintStreamLogger(System.out);
        var gramat = new Gramat(logger);

        if(exists(resource + "c")) {
            var inputStream = loadInputStream(resource);

            return gramat.loadProgram(inputStream);
        }

        var code = loadString(resource);
        var program = gramat.compile(new InputCharSequence(code));

        gramat.saveProgram(program);

        return null;
    }

}
