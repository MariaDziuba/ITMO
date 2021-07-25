package info.kgeorgiy.ja.dziuba.i18n;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Class of utilities of files
 */
public class FileUtils {

    static String readFile(final Path filePath) throws IOException {
        return Files.readString(filePath);
    }

    static void writeFile(final String outputFileName, final String data) throws IOException, InvalidPathException {
        final Path outputFilePath = Paths.get(outputFileName);
        final Path parentDir = outputFilePath.getParent();
        Files.createDirectories(Objects.requireNonNull(parentDir));
        Files.writeString(Paths.get(outputFileName), data, StandardCharsets.UTF_8);
    }
}
