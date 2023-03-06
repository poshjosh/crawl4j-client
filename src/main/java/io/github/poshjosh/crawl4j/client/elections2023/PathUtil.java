package io.github.poshjosh.crawl4j.client.elections2023;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Pattern;

public final class PathUtil {

    private static final Logger logger = LoggerFactory.getLogger(PathUtil.class);

    private static final Path OWN_STORAGE_PARENT = Paths.get(System.getProperty("user.home"), "Desktop", "2023-elections");
    private static final String OWN_STORAGE_FOLDER_RESULTS = OWN_STORAGE_PARENT.resolve("results").toString();
    public static final String OWN_STORAGE_FOLDER_DATA = OWN_STORAGE_PARENT.resolve(Paths.get("crawl-data", "root")).toString();

    private PathUtil() { }

    private static final Pattern PATH_PATTERN = Pattern.compile("[^a-zA-Z_0-9-./]");

    public static boolean save(String url, byte [] data) {
        Path path = null;
        try {
            path = PathUtil.createPathForUrl(url);
            Files.write(path, data,
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("Saved: {}", url);
            return true;
        } catch (IOException iox) {
            logger.error("Failed to write file: {}, reason: {}", path == null ? url : path, iox.toString());
            return false;
        }
    }

    public static Path createPathForUrl(String url) throws IOException {
        String filename = resolvePathForUrl(url);
        Path path = Paths.get(OWN_STORAGE_FOLDER_RESULTS, "2023-elections", filename);
        createDirIfNotExisting(path.getParent());
        return path;
    }

    /**
     * Convert a URL string to a file path.
     * <p/>
     * <code>https://abc$_def/ghi.jpg</code> results to <code>abc_def/ghi.jpg</code>
     * @param url The url to convert
     * @return The file path resolved from the provided URL
     */
    static String resolvePathForUrl(String url) {
        final int n = url.indexOf("//");
        return PATH_PATTERN.matcher(url.substring(n + 1)).replaceAll("");
    }

    public static void createDirIfNotExisting(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            logger.info("Created directories: {}", path);
        }
    }
}
