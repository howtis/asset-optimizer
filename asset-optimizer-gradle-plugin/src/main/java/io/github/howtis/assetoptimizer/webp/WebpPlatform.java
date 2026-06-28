package io.github.howtis.assetoptimizer.webp;

import java.util.Locale;

enum WebpPlatform {
    WINDOWS_X64("win32-x64/cwebp.exe"),
    LINUX_X64("linux-x64/cwebp"),
    DARWIN_X64("darwin-x64/cwebp"),
    DARWIN_ARM64("darwin-arm64/cwebp"),
    UNSUPPORTED(null);

    private final String resourcePath;

    WebpPlatform(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    String resourcePath() {
        if (this == UNSUPPORTED) {
            throw new UnsupportedOperationException(
                "cwebp is not available for " + osName() + " " + archName());
        }
        return resourcePath;
    }

    static WebpPlatform detect() {
        String os = osName().toLowerCase(Locale.ROOT);
        String arch = archName().toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return arch.contains("64") || arch.contains("amd64") ? WINDOWS_X64 : UNSUPPORTED;
        }
        if (os.contains("linux")) {
            if (arch.contains("64") && !arch.contains("arm") && !arch.contains("aarch64")) {
                return LINUX_X64;
            }
            return UNSUPPORTED;
        }
        if (os.contains("mac")) {
            if (arch.contains("arm") || arch.contains("aarch64")) {
                return DARWIN_ARM64;
            }
            return DARWIN_X64;
        }
        return UNSUPPORTED;
    }

    private static String osName() {
        return System.getProperty("os.name", "");
    }

    private static String archName() {
        return System.getProperty("os.arch", "");
    }
}
