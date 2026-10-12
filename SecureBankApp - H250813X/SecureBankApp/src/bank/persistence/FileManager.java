package bank.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * The only class in the project that opens a file stream directly.
 * Every repository goes through here, so file-handling concerns
 * (creating missing files/folders, try-with-resources, IOException
 * handling) live in exactly one place.
 */
public final class FileManager {

    private FileManager() {
        // utility class — no instances
    }

    /** Creates the file (and any missing parent folders) if it doesn't exist yet. */
    public static void ensureFile(String path) {
        try {
            Path p = Paths.get(path);
            if (p.getParent() != null) {
                Files.createDirectories(p.getParent());
            }
            if (!Files.exists(p)) {
                Files.createFile(p);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not create data file: " + path, e);
        }
    }

    /** Reads all non-blank lines from a file, creating it first if missing. */
    public static List<String> readLines(String path) {
        ensureFile(path);
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            List<String> nonBlank = new ArrayList<>();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    nonBlank.add(line);
                }
            }
            return nonBlank;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read data file: " + path, e);
        }
    }

    /** Overwrites the whole file with the given lines. */
    public static void writeLines(String path, List<String> lines) {
        ensureFile(path);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path),
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not write data file: " + path, e);
        }
    }

    /** Appends a single line to the end of the file. */
    public static void appendLine(String path, String line) {
        ensureFile(path);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path),
                StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new IllegalStateException("Could not append to data file: " + path, e);
        }
    }
}
