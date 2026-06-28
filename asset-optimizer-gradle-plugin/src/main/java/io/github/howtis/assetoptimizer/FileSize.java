package io.github.howtis.assetoptimizer;

public final class FileSize {

    private FileSize() {}

    public static String humanReadable(long bytes) {
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + "KB";
        return String.format("%.1fMB", bytes / (1024.0 * 1024.0));
    }
}
