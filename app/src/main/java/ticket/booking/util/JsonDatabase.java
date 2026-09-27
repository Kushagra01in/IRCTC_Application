package ticket.booking.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/** Small file store that keeps runtime data out of source files and writes atomically. */
public final class JsonDatabase {
    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static final Path DATA_DIRECTORY = java.nio.file.Paths.get(System.getProperty("ticket.booking.data.dir", "app/data"));
    private JsonDatabase() { }
    public static synchronized <T> T load(String fileName, Path legacyFile, TypeReference<T> type) throws IOException {
        Path file = databaseFile(fileName);
        if (!Files.exists(file)) {
            if (legacyFile != null && Files.exists(legacyFile)) Files.copy(legacyFile, file);
            else Files.write(file, "[]".getBytes("UTF-8"), StandardOpenOption.CREATE_NEW);
        }
        return MAPPER.readValue(file.toFile(), type);
    }
    public static synchronized void save(String fileName, Object value) throws IOException {
        Path file = databaseFile(fileName);
        Path temporaryFile = Files.createTempFile(DATA_DIRECTORY, fileName, ".tmp");
        try {
            MAPPER.writeValue(temporaryFile.toFile(), value);
            try { Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException ignored) { Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temporaryFile); }
    }
    private static Path databaseFile(String fileName) throws IOException { Files.createDirectories(DATA_DIRECTORY); return DATA_DIRECTORY.resolve(fileName); }
}
