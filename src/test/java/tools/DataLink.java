package tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;

public class DataLink {

    private static final Base64.Encoder encoder = Base64.getUrlEncoder();

    public static String of(String baseName, String value) {
        try {
            var outputDir = Path.of("output").toAbsolutePath();

            Files.createDirectories(outputDir);

            var fileName = String.format("%s-%s.txt", baseName, UUID.randomUUID());
            var path = outputDir.resolve(fileName);

            Files.writeString(path, value, StandardCharsets.UTF_8);

            return String.format("file://%s", path);
        }
        catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private DataLink() {}

}
