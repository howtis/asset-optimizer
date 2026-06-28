package io.github.howtis.assetoptimizer.webp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebpPlatformTest {

    @Test
    void detectReturnsNonNullOnCurrentSystem() {
        WebpPlatform platform = WebpPlatform.detect();
        assertNotNull(platform);
        if (platform != WebpPlatform.UNSUPPORTED) {
            assertNotNull(platform.resourcePath());
        }
    }

    @Test
    void unsupportedThrowsOnResourcePath() {
        assertThrows(UnsupportedOperationException.class, WebpPlatform.UNSUPPORTED::resourcePath);
    }

    @Test
    void supportedPlatformsHaveValidPaths() {
        assertPlatform(WebpPlatform.WINDOWS_X64, "win32-x64/cwebp.exe");
        assertPlatform(WebpPlatform.LINUX_X64, "linux-x64/cwebp");
        assertPlatform(WebpPlatform.DARWIN_X64, "darwin-x64/cwebp");
        assertPlatform(WebpPlatform.DARWIN_ARM64, "darwin-arm64/cwebp");
    }

    private static void assertPlatform(WebpPlatform platform, String expectedResourcePath) {
        assertEquals(expectedResourcePath, platform.resourcePath());
    }
}
