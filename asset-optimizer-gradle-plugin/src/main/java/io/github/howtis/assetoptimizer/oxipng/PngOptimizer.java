package io.github.howtis.assetoptimizer.oxipng;

import io.github.howtis.assetoptimizer.Processes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class PngOptimizer {

    private final Path oxipngBinary;

    public PngOptimizer(Path oxipngBinary) {
        this.oxipngBinary = oxipngBinary;
    }

    public void optimizeFile(Path input, Path output) throws IOException {
        Files.createDirectories(output.getParent());
        Files.copy(input, output);
        Process process = new ProcessBuilder()
                .command(List.of(
                        oxipngBinary.toAbsolutePath().toString(),
                        "-o", "2",    // level 2 (highest) optimization
                        "--strip", "safe",    // remove non-essential metadata
                        output.toAbsolutePath().toString()))
                .redirectErrorStream(true)
                .start();
        Processes.destroyProcess(process, 60, "oxipng");
    }
}
