package io.github.howtis.assetoptimizer.oxipng;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OxipngPlatformTest {

    @Test
    void detectReturnsNonNullOnCurrentSystem() {
        OxipngPlatform platform = OxipngPlatform.detect();
        assertNotNull(platform);
        if (platform != OxipngPlatform.UNSUPPORTED) {
            assertNotNull(platform.resourcePath());
        }
    }

    @Test
    void unsupportedThrowsOnResourcePath() {
        assertThrows(UnsupportedOperationException.class, OxipngPlatform.UNSUPPORTED::resourcePath);
    }

    @Test
    void supportedPlatformsHaveValidPaths() {
        assertPlatform(OxipngPlatform.WINDOWS_X64, "win32-x64/oxipng.exe");
        assertPlatform(OxipngPlatform.LINUX_X64, "linux-x64/oxipng");
        assertPlatform(OxipngPlatform.DARWIN_X64, "darwin-x64/oxipng");
        assertPlatform(OxipngPlatform.DARWIN_ARM64, "darwin-arm64/oxipng");
    }

    private static void assertPlatform(OxipngPlatform platform, String expectedResourcePath) {
        assertEquals(expectedResourcePath, platform.resourcePath());
    }
}
