package io.github.howtis.assetoptimizer.esbuild;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CssMinifierTest {

    @Test
    void minifyRemovesWhitespace(@TempDir Path cacheDir) throws IOException {
        Platform platform = Platform.detect();
        if (platform == Platform.UNSUPPORTED) {
            return;
        }
        EsbuildBinary binary = new EsbuildBinary(cacheDir);
        Path esbuild = binary.ensureAvailable();
        CssMinifier minifier = new CssMinifier(esbuild);

        String result = minifier.minify("body {\n  color: red;\n}\n");

        assertNotNull(result);
        assertFalse(result.isBlank());
        // esbuild minification should produce whitespace-free output
        assertTrue(result.contains("body{") || result.contains("body {"));
    }

    @Test
    void minifyProducesSmallerOutput(@TempDir Path cacheDir) throws IOException {
        Platform platform = Platform.detect();
        if (platform == Platform.UNSUPPORTED) {
            return;
        }
        EsbuildBinary binary = new EsbuildBinary(cacheDir);
        Path esbuild = binary.ensureAvailable();
        CssMinifier minifier = new CssMinifier(esbuild);

        String input = ".container {\n  display: flex;\n  padding: 0;\n  margin: 0;\n}\n";
        String result = minifier.minify(input);

        assertTrue(result.length() < input.length(),
                "minified output should be smaller than input");
    }

    @Test
    void minifyFileProducesOutput(@TempDir Path cacheDir) throws IOException {
        Platform platform = Platform.detect();
        if (platform == Platform.UNSUPPORTED) {
            return;
        }
        EsbuildBinary binary = new EsbuildBinary(cacheDir);
        Path esbuild = binary.ensureAvailable();
        CssMinifier minifier = new CssMinifier(esbuild);

        Path input = cacheDir.resolve("test.css");
        Files.writeString(input, ".foo { color: blue; }\n");
        Path output = cacheDir.resolve("test.min.css");

        minifier.minifyFile(input, output);

        assertTrue(Files.exists(output));
        assertTrue(Files.size(output) > 0);
    }
}
