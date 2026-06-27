package io.github.howtis.assetoptimizer.esbuild;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlatformTest {

    @Test
    void detectReturnsNonNullOnCurrentSystem() {
        Platform platform = Platform.detect();
        assertNotNull(platform);
        if (platform != Platform.UNSUPPORTED) {
            assertNotNull(platform.npmName());
            assertNotNull(platform.binaryPath());
        }
    }

    @Test
    void unsupportedThrowsOnNpmName() {
        assertThrows(UnsupportedOperationException.class, Platform.UNSUPPORTED::npmName);
    }

    @Test
    void unsupportedThrowsOnResourcePath() {
        assertThrows(UnsupportedOperationException.class, Platform.UNSUPPORTED::resourcePath);
    }

    @Test
    void supportedPlatformsHaveValidPaths() {
        assertPlatform(Platform.WINDOWS_X64, "win32-x64", "bin/esbuild.exe", "win32-x64/esbuild.exe");
        assertPlatform(Platform.LINUX_X64, "linux-x64", "bin/esbuild", "linux-x64/esbuild");
        assertPlatform(Platform.DARWIN_X64, "darwin-x64", "bin/esbuild", "darwin-x64/esbuild");
        assertPlatform(Platform.DARWIN_ARM64, "darwin-arm64", "bin/esbuild", "darwin-arm64/esbuild");
        assertPlatform(Platform.LINUX_ARM64, "linux-arm64", "bin/esbuild", "linux-arm64/esbuild");
    }

    private static void assertPlatform(Platform platform, String npmName, String binaryPath, String resourcePath) {
        assertEquals(npmName, platform.npmName());
        assertEquals(binaryPath, platform.binaryPath());
        assertEquals(resourcePath, platform.resourcePath());
    }
}
