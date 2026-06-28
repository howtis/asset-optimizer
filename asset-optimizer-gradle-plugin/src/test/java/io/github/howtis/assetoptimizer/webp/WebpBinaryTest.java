package io.github.howtis.assetoptimizer.webp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WebpBinaryTest {

    @Test
    void extractsAndReturnsBinary(@TempDir Path cacheDir) throws IOException {
        WebpPlatform platform = WebpPlatform.detect();
        if (platform == WebpPlatform.UNSUPPORTED) {
            return;
        }
        WebpBinary binary = new WebpBinary(cacheDir);
        Path result = binary.ensureAvailable();
        assertNotNull(result);
        assertTrue(Files.isExecutable(result));
    }

    @Test
    void returnsCachedBinaryWhenAlreadyExtracted(@TempDir Path cacheDir) throws IOException {
        WebpPlatform platform = WebpPlatform.detect();
        if (platform == WebpPlatform.UNSUPPORTED) {
            return;
        }
        WebpBinary binary = new WebpBinary(cacheDir);
        Path expectedPath = cacheDir.resolve("webp-1.5.0").resolve(platform.resourcePath());
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
