package io.github.howtis.assetoptimizer;

import org.junit.jupiter.api.Test;

import static io.github.howtis.assetoptimizer.FileSize.humanReadable;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileSizeTest {

    @Test
    void zeroBytes() {
        assertEquals("0B", humanReadable(0));
    }

    @Test
    void under1KB() {
        assertEquals("512B", humanReadable(512));
        assertEquals("1023B", humanReadable(1023));
    }

    @Test
    void exactly1KB() {
        assertEquals("1KB", humanReadable(1024));
    }

    @Test
    void between1KBAnd1MB() {
        assertEquals("1KB", humanReadable(1536)); // integer division truncates
        assertEquals("1023KB", humanReadable(1024 * 1024 - 1));
    }

    @Test
    void exactly1MB() {
        assertEquals("1.0MB", humanReadable(1024 * 1024));
    }

    @Test
    void above1MB() {
        assertEquals("2.0MB", humanReadable(1024 * 1024 * 2));
    }

    @Test
    void largeValueUsesMB() {
        // values above 1MB always use MB formatting (no GB branch)
        assertEquals("1024.0MB", humanReadable(1024L * 1024 * 1024));
    }
}
