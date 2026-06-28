package io.github.howtis.assetoptimizer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Static utilities for native binary lifecycle: extraction, POSIX permissions, and process cleanup.
 */
public final class Processes {

    private Processes() {
    }

    public static void extractBinary(Class<?> clazz, String resourcePath, Path dest, String toolName) throws IOException {
        try (InputStream in = clazz.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException(toolName + " binary not found in classpath: " + resourcePath);
            }
            Files.copy(in, dest);
        }
        makeExecutable(dest);
    }

    public static void makeExecutable(Path file) throws IOException {
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

    public static void destroyProcess(Process process, int timeoutSeconds, String toolName) throws IOException {
        try {
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new IOException(toolName + " timed out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
            throw new IOException(toolName + " interrupted", e);
        }
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            String stderr;
            try (InputStream err = process.getInputStream()) {
                stderr = new String(err.readAllBytes(), StandardCharsets.UTF_8);
            }
            throw new IOException(toolName + " exited with code " + exitCode + ": " + stderr);
        }
    }
}
