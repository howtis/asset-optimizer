package io.github.howtis.assetoptimizer.jpeg;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JpegOptimizerTest {

    private final JpegOptimizer optimizer = new JpegOptimizer();

    @Test
    void reducesSizeByStrippingExif() throws IOException {
        byte[] input = createJpegWithExif();
        byte[] output = optimizer.optimize(input);
        assertTrue(output.length > 0);
        assertTrue(output.length < input.length, "output should be smaller than input after stripping EXIF");
    }

    @Test
    void reducesSizeByStrippingComments() throws IOException {
        byte[] input = createJpegWithComment();
        byte[] output = optimizer.optimize(input);
        assertTrue(output.length > 0);
        assertTrue(output.length < input.length, "output should be smaller than input after stripping COM");
    }

    @Test
    void outputIsValidJpeg() throws IOException {
        byte[] input = createMinimalJpeg();
        byte[] output = optimizer.optimize(input);
        assertTrue(output.length > 0);
        assertTrue(isJpeg(output));
    }

    @Test
    void preservesJfifHeader() throws IOException {
        byte[] input = createJpegWithJfifApp0();
        byte[] output = optimizer.optimize(input);

        boolean hasApp0 = false;
        for (int i = 0; i < output.length - 1; i++) {
            if (output[i] == (byte) 0xFF && output[i + 1] == (byte) 0xE0) {
                hasApp0 = true;
                break;
            }
        }
        assertTrue(hasApp0, "APP0/JFIF marker should be preserved");
    }

    // Generate a minimal valid JPEG with metadata

    private static byte[] createJpegWithExif() {
        byte[] base = createMinimalJpeg();
        byte[] exif = createAppSegment(0xE1, "Exif\0\0II*\0".getBytes());
        return insertAppSegment(base, exif);
    }

    private static byte[] createJpegWithComment() {
        byte[] base = createMinimalJpeg();
        byte[] com = createComSegment("some comment data".getBytes());
        return insertAppSegment(base, com);
    }

    private static byte[] createJpegWithJfifApp0() {
        byte[] base = createMinimalJpeg();
        byte[] app0 = createAppSegment(0xE0, "JFIF\0".getBytes());
        return insertAppSegment(base, app0);
    }

    /**
     * Creates a minimal but structurally valid JPEG (1x1 pixel, grayscale).
     * Uses ImageIO to generate the image data programmatically.
     */
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

    private static byte[] createAppSegment(int markerCode, byte[] data) {
        int length = data.length + 2;
        byte[] segment = new byte[2 + length];
        segment[0] = (byte) 0xFF;
        segment[1] = (byte) markerCode;
        segment[2] = (byte) (length >> 8);
        segment[3] = (byte) (length & 0xFF);
        System.arraycopy(data, 0, segment, 4, data.length);
        return segment;
    }

    private static byte[] createComSegment(byte[] data) {
        return createAppSegment(0xFE, data);
    }

    /**
     * Insert a marker segment right after SOI (0xFFD8).
     */
    private static byte[] insertAppSegment(byte[] base, byte[] segment) {
        byte[] result = new byte[base.length + segment.length];
        // SOI is first 2 bytes
        System.arraycopy(base, 0, result, 0, 2);
        // insert the segment after SOI
        System.arraycopy(segment, 0, result, 2, segment.length);
        // copy the rest
        System.arraycopy(base, 2, result, 2 + segment.length, base.length - 2);
        return result;
    }

    private static boolean isJpeg(byte[] data) {
        return data.length >= 4
            && data[0] == (byte) 0xFF && data[1] == (byte) 0xD8  // SOI
            && data[data.length - 2] == (byte) 0xFF && data[data.length - 1] == (byte) 0xD9; // EOI
    }
}
