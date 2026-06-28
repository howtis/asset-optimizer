package io.github.howtis.assetoptimizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CacheTest {

    @Test
    void returnsFalseWhenOutputDoesNotExist(@TempDir Path dir) throws IOException {
        Path source = dir.resolve("source.txt");
        Files.writeString(source, "hello");
        Path output = dir.resolve("output.txt");

        assertFalse(Cache.shouldSkip(source, output));
    }

    @Test
    void returnsTrueWhenOutputIsNewer(@TempDir Path dir) throws IOException {
        Path source = dir.resolve("source.txt");
        Files.writeString(source, "hello");
        Path output = dir.resolve("output.txt");
        Files.writeString(output, "world");

        // make output newer than source
        Files.setLastModifiedTime(output, FileTime.fromMillis(
                Files.getLastModifiedTime(source).toMillis() + 1000));

        assertTrue(Cache.shouldSkip(source, output));
    }

    @Test
    void returnsFalseWhenOutputIsOlder(@TempDir Path dir) throws IOException {
        Path source = dir.resolve("source.txt");
        Files.writeString(source, "hello");
        Path output = dir.resolve("output.txt");
        Files.writeString(output, "world");

        // make output older than source
        Files.setLastModifiedTime(output, FileTime.fromMillis(
                Files.getLastModifiedTime(source).toMillis() - 1000));

        assertFalse(Cache.shouldSkip(source, output));
    }

    @Test
    void returnsTrueWhenTimestampsAreEqual(@TempDir Path dir) throws IOException {
        Path source = dir.resolve("source.txt");
        Files.writeString(source, "hello");
        Path output = dir.resolve("output.txt");
        Files.writeString(output, "world");

        FileTime sameTime = Files.getLastModifiedTime(source);
        Files.setLastModifiedTime(output, sameTime);

        assertTrue(Cache.shouldSkip(source, output));
    }
}
