package io.github.howtis.assetoptimizer.webp;

import io.github.howtis.assetoptimizer.task.ConvertWebpTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WebpConverterTest {

    @Test
    void convertsJpegToWebp(@TempDir Path cacheDir) throws IOException {
        WebpPlatform platform = WebpPlatform.detect();
        if (platform == WebpPlatform.UNSUPPORTED) {
            return;
        }
        WebpBinary binary = new WebpBinary(cacheDir);
        Path cwebp = binary.ensureAvailable();

        byte[] jpeg = createMinimalJpeg();
        Path input = cacheDir.resolve("test.jpg");
        Files.write(input, jpeg);
        Path output = cacheDir.resolve("test.webp");

        ConvertWebpTask.convertFile(cwebp, input, output);

        assertTrue(Files.exists(output));
        assertTrue(Files.size(output) > 0);
        assertTrue(isWebp(Files.readAllBytes(output)));
    }

    @Test
    void convertsPngToWebp(@TempDir Path cacheDir) throws IOException {
        WebpPlatform platform = WebpPlatform.detect();
        if (platform == WebpPlatform.UNSUPPORTED) {
            return;
        }
        WebpBinary binary = new WebpBinary(cacheDir);
        Path cwebp = binary.ensureAvailable();

        byte[] png = createMinimalPng();
        Path input = cacheDir.resolve("test.png");
        Files.write(input, png);
        Path output = cacheDir.resolve("test.webp");

        ConvertWebpTask.convertFile(cwebp, input, output);

        assertTrue(Files.exists(output));
        assertTrue(Files.size(output) > 0);
        assertTrue(isWebp(Files.readAllBytes(output)));
    }

    private static byte[] createMinimalJpeg() {
        try {
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
            img.setRGB(0, 0, 0x808080);
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(img, "JPEG", bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create test JPEG", e);
        }
    }

    private static byte[] createMinimalPng() {
        try {
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
            img.setRGB(0, 0, 0x808080);
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(img, "PNG", bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create test PNG", e);
        }
    }

    private static boolean isWebp(byte[] data) {
        return data.length >= 12
            && data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F'
            && data[8] == 'W' && data[9] == 'E' && data[10] == 'B' && data[11] == 'P';
    }
}
