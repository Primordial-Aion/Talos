package engine.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static String readFileAsString(String resourcePath) {
        try {
            Path path = Paths.get(resourcePath);
            if (Files.exists(path)) {
                return Files.readString(path);
            }
            var inputStream = FileUtils.class.getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream != null) {
                return new String(inputStream.readAllBytes());
            }
            throw new RuntimeException("File not found: " + resourcePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + resourcePath, e);
        }
    }

    public static byte[] readFileAsBytes(String resourcePath) {
        try {
            Path path = Paths.get(resourcePath);
            if (Files.exists(path)) {
                return Files.readAllBytes(path);
            }
            var inputStream = FileUtils.class.getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream != null) {
                return inputStream.readAllBytes();
            }
            throw new RuntimeException("File not found: " + resourcePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + resourcePath, e);
        }
    }

    public static String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(lastDot + 1);
        }
        return "";
    }

    public static String getParentPath(String path) {
        int lastSlash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        if (lastSlash > 0) {
            return path.substring(0, lastSlash);
        }
        return "";
    }
}