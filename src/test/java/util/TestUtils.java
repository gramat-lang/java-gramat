package util;

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

}
