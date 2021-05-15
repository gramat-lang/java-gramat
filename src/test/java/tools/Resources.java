package tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Resources {

    public static String loadString(String resource) {
        var stream = Resources.class.getResourceAsStream(resource);
        if (stream == null) {
            throw new AssertionError("Resource not found: " + resource);
        }

        try {
            try {
                var data = stream.readAllBytes();

                return new String(data, StandardCharsets.UTF_8);
            }
            finally {
                stream.close();
            }
        }
        catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}
