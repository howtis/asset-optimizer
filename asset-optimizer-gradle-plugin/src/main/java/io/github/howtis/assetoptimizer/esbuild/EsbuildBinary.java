package io.github.howtis.assetoptimizer.esbuild;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

public final class EsbuildBinary {

    private static final String ESBUILD_VERSION = "0.25.0";
    private static final String RESOURCE_PREFIX = "/";

    private final Path cacheDir;
    private final Platform platform;

    public EsbuildBinary(Path cacheDir) {
        this.cacheDir = cacheDir.resolve("esbuild-" + ESBUILD_VERSION);
        this.platform = Platform.detect();
    }

    public Path ensureAvailable() throws IOException {
        Path binary = cacheDir.resolve(platform.resourcePath());
        if (Files.isExecutable(binary)) {
            return binary;
        }
        synchronized (EsbuildBinary.class) {
            if (Files.isExecutable(binary)) {
                return binary;
            }
            Files.createDirectories(binary.getParent());
            extractFromClasspath(binary);
        }
        return binary;
    }

    private void extractFromClasspath(Path dest) throws IOException {
        String resourcePath = RESOURCE_PREFIX + platform.resourcePath();
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("esbuild binary not found in classpath: " + resourcePath);
            }
            Files.copy(in, dest);
        }
        makeExecutable(dest);
    }

    private static void makeExecutable(Path file) throws IOException {
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(file);
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            perms.add(PosixFilePermission.GROUP_EXECUTE);
            perms.add(PosixFilePermission.OTHERS_EXECUTE);
            Files.setPosixFilePermissions(file, perms);
        } catch (UnsupportedOperationException ignored) {
            // Windows does not support POSIX permissions
        }
    }
}
