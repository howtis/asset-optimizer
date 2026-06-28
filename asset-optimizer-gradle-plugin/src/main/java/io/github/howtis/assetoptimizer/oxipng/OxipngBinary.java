package io.github.howtis.assetoptimizer.oxipng;

import io.github.howtis.assetoptimizer.Processes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class OxipngBinary {

    private static final String OXIPNG_VERSION = "10.1.1";

    private final Path cacheDir;
    private final OxipngPlatform platform;

    public OxipngBinary(Path cacheDir) {
        this.cacheDir = cacheDir.resolve("oxipng-" + OXIPNG_VERSION);
        this.platform = OxipngPlatform.detect();
    }

    public Path ensureAvailable() throws IOException {
        Path binary = cacheDir.resolve(platform.resourcePath());
        if (Files.isExecutable(binary)) {
            return binary;
        }
        synchronized (OxipngBinary.class) {
            if (Files.isExecutable(binary)) {
                return binary;
            }
            Files.createDirectories(binary.getParent());
            Processes.extractBinary(getClass(), "/" + platform.resourcePath(), binary, "oxipng");
        }
        return binary;
    }
}
