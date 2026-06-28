package io.github.howtis.assetoptimizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Cache {

    private Cache() {
    }

    public static boolean shouldSkip(Path source, Path output) throws IOException {
        if (!Files.exists(output)) {
            return false;
        }
        return Files.getLastModifiedTime(output).compareTo(
            Files.getLastModifiedTime(source)) >= 0;
    }
}
