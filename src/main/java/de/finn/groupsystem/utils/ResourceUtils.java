package de.finn.groupsystem.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceUtils {

    public static void copyResourceIfMissing(String resourcePath, Path targetPath) throws IOException {
        if (Files.notExists(targetPath)) {
            try (InputStream in = ResourceUtils.class.getResourceAsStream(resourcePath)) {
                if (in == null) {
                    throw new IOException("Resource not found: " + resourcePath);
                }
                Files.createDirectories(targetPath.getParent());
                Files.copy(in, targetPath);
            }
        }
    }
}