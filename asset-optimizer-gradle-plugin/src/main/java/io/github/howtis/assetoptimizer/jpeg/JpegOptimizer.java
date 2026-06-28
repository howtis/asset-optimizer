package io.github.howtis.assetoptimizer.jpeg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class JpegOptimizer {

    public void optimizeFile(Path input, Path output) throws IOException {
        byte[] data = Files.readAllBytes(input);
        byte[] result = optimize(data);
        Files.createDirectories(output.getParent());
        Files.write(output, result);
    }

    byte[] optimize(byte[] jpeg) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(jpeg.length);
        int pos = 0;
        int len = jpeg.length;

        while (pos < len) {
            if (jpeg[pos] != (byte) 0xFF) {
                out.write(jpeg[pos]);
                pos++;
                continue;
            }
            if (pos + 1 >= len) {
                out.write(jpeg[pos]);
                pos++;
                continue;
            }
            int marker = jpeg[pos + 1] & 0xFF;
            if (marker == 0x00 || marker == 0xFF) {
                // stuffed byte or fill byte
                out.write(jpeg[pos]);
                out.write(jpeg[pos + 1]);
                pos += 2;
                continue;
            }
            if (marker == 0xD9) {
                // EOI marker
                out.write(jpeg[pos]);
                out.write(jpeg[pos + 1]);
                pos += 2;
                break;
            }
            if (isStandaloneMarker(marker)) {
                // SOI, RSTn, TEM - no segment length
                out.write(jpeg[pos]);
                out.write(jpeg[pos + 1]);
                pos += 2;
                continue;
            }
            if (marker == 0xDA) {
                // SOS - has segment header then entropy-coded data until next marker
                pos = copySegment(jpeg, pos, out);
                while (pos < len - 1) {
                    if (jpeg[pos] == (byte) 0xFF && jpeg[pos + 1] != (byte) 0x00) {
                        break;
                    }
                    out.write(jpeg[pos]);
                    pos++;
                }
                continue;
            }
            // Segment markers with length
            if (marker == 0xE0) {
                // JFIF (APP0) marker - keep
                pos = copySegment(jpeg, pos, out);
            } else if (isStrippableMarker(marker)) {
                // Remove APPn (except APP0) and COM markers
                pos = skipSegment(jpeg, pos);
            } else {
                // Keep all other markers (DQT, DHT, SOF, etc.)
                pos = copySegment(jpeg, pos, out);
            }
        }
        return out.toByteArray();
    }

    private static boolean isStrippableMarker(int marker) {
        return (marker >= 0xE1 && marker <= 0xEF) // APP1 - APP15
            || marker == 0xFE;                     // COM
    }

    private static boolean isStandaloneMarker(int marker) {
        return marker == 0xD8               // SOI
            || (marker >= 0xD0 && marker <= 0xD7) // RST0-RST7
            || marker == 0x01;              // TEM
    }

    private static int copySegment(byte[] data, int pos, OutputStream out) throws IOException {
        out.write(data[pos]);
        out.write(data[pos + 1]);
        pos += 2;
        if (pos + 1 >= data.length) {
            return pos;
        }
        int length = ((data[pos] & 0xFF) << 8) | (data[pos + 1] & 0xFF);
        int segmentEnd = Math.min(pos + length, data.length);
        out.write(data, pos, segmentEnd - pos);
        return segmentEnd;
    }

    private static int skipSegment(byte[] data, int pos) {
        pos += 2;
        if (pos + 1 >= data.length) {
            return pos;
        }
        int length = ((data[pos] & 0xFF) << 8) | (data[pos + 1] & 0xFF);
        return Math.min(pos + length, data.length);
    }
}
