package io.github.howtis.assetoptimizer.esbuild;

import java.util.Locale;

enum Platform {
    WINDOWS_X64("win32-x64", "bin/esbuild.exe", "win32-x64/esbuild.exe"),
    LINUX_X64("linux-x64", "bin/esbuild", "linux-x64/esbuild"),
    DARWIN_X64("darwin-x64", "bin/esbuild", "darwin-x64/esbuild"),
    DARWIN_ARM64("darwin-arm64", "bin/esbuild", "darwin-arm64/esbuild"),
    LINUX_ARM64("linux-arm64", "bin/esbuild", "linux-arm64/esbuild"),
    UNSUPPORTED(null, null, null);

    private final String npmName;
    private final String binaryPath;
    private final String resourcePath;

    Platform(String npmName, String binaryPath, String resourcePath) {
        this.npmName = npmName;
        this.binaryPath = binaryPath;
        this.resourcePath = resourcePath;
    }

    String npmName() {
        if (this == UNSUPPORTED) {
            throw new UnsupportedOperationException(
                "esbuild is not available for " + osName() + " " + archName());
        }
        return npmName;
    }

    String binaryPath() {
        return binaryPath;
    }

    String resourcePath() {
        if (this == UNSUPPORTED) {
            throw new UnsupportedOperationException(
                "esbuild is not available for " + osName() + " " + archName());
        }
        return resourcePath;
    }

    static Platform detect() {
        String os = osName().toLowerCase(Locale.ROOT);
        String arch = archName().toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return arch.contains("64") || arch.contains("amd64") ? WINDOWS_X64 : UNSUPPORTED;
        }
        if (os.contains("linux")) {
            if (arch.contains("64") && !arch.contains("arm") && !arch.contains("aarch64")) {
                return LINUX_X64;
            }
            if (arch.contains("arm") || arch.contains("aarch64")) {
                return LINUX_ARM64;
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
