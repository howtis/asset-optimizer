package io.github.howtis.assetoptimizer.webp;

import io.github.howtis.assetoptimizer.Processes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class WebpBinary {

    private static final String WEBP_VERSION = "1.5.0";

    private final Path cacheDir;
    private final WebpPlatform platform;

    public WebpBinary(Path cacheDir) {
        this.cacheDir = cacheDir.resolve("webp-" + WEBP_VERSION);
        this.platform = WebpPlatform.detect();
    }

    public Path ensureAvailable() throws IOException {
        Path binary = cacheDir.resolve(platform.resourcePath());
        if (Files.isExecutable(binary)) {
            return binary;
        }
        synchronized (WebpBinary.class) {
            if (Files.isExecutable(binary)) {
                return binary;
            }
            Files.createDirectories(binary.getParent());
            Processes.extractBinary(getClass(), "/" + platform.resourcePath(), binary, "cwebp");
        }
        return binary;
    }
}
