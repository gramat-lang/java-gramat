package tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Resources {

    public interface StreamReader<T> {
        T read(InputStream stream) throws IOException;
    }

    public static <T> T loadStream(String resource, StreamReader<T> fn) {
        var stream = Resources.class.getResourceAsStream(resource);
        if (stream == null) {
            throw new AssertionError("Resource not found: " + resource);
        }

        try {
            try (stream) {
                return fn.read(stream);
            }
        }
        catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public static String loadString(String resource) {
        return loadStream(resource, stream -> {
            var data = stream.readAllBytes();

            return new String(data, StandardCharsets.UTF_8);
        });
    }

    public static String[] loadLines(String resource) {
        return loadString(resource).split("\r?\n");
    }

}
