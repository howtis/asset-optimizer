package io.github.howtis.assetoptimizer.oxipng;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OxipngBinaryTest {

    @Test
    void extractsAndReturnsBinary(@TempDir Path cacheDir) throws IOException {
        OxipngPlatform platform = OxipngPlatform.detect();
        if (platform == OxipngPlatform.UNSUPPORTED) {
            return; // cannot test on unsupported platforms
        }
        OxipngBinary binary = new OxipngBinary(cacheDir);
        Path result = binary.ensureAvailable();
        assertNotNull(result);
        assertTrue(Files.isExecutable(result));
    }

    @Test
    void returnsCachedBinaryWhenAlreadyExtracted(@TempDir Path cacheDir) throws IOException {
        OxipngPlatform platform = OxipngPlatform.detect();
        if (platform == OxipngPlatform.UNSUPPORTED) {
            return; // skip on unsupported platforms
        }
        OxipngBinary binary = new OxipngBinary(cacheDir);
        Path expectedPath = cacheDir.resolve("oxipng-10.1.1").resolve(platform.resourcePath());
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
