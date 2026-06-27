package io.github.howtis.assetoptimizer.esbuild;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EsbuildBinaryTest {

    @Test
    void extractsAndReturnsBinary(@TempDir Path cacheDir) throws IOException {
        Platform platform = Platform.detect();
        if (platform == Platform.UNSUPPORTED) {
            return; // cannot test on unsupported platforms
        }
        EsbuildBinary binary = new EsbuildBinary(cacheDir);
        Path result = binary.ensureAvailable();
        assertNotNull(result);
        assertTrue(java.nio.file.Files.isExecutable(result));
    }

    @Test
    void returnsCachedBinaryWhenAlreadyExtracted(@TempDir Path cacheDir) throws IOException {
        Platform platform = Platform.detect();
        if (platform == Platform.UNSUPPORTED) {
            return; // skip on unsupported platforms
        }
        EsbuildBinary binary = new EsbuildBinary(cacheDir);
        Path expectedPath = cacheDir.resolve("esbuild-0.25.0").resolve(platform.resourcePath());
        Files.createDirectories(expectedPath.getParent());
        Files.createFile(expectedPath);
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(expectedPath);
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            Files.setPosixFilePermissions(expectedPath, perms);
        } catch (UnsupportedOperationException ignored) {
            // Windows does not support POSIX permissions
        }

        Path result = binary.ensureAvailable();
        assertEquals(expectedPath, result);
    }
}
