package io.github.howtis.assetoptimizer.esbuild;

import io.github.howtis.assetoptimizer.Processes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CssMinifier {

    private final Path esbuildBinary;

    public CssMinifier(Path esbuildBinary) {
        this.esbuildBinary = esbuildBinary;
    }

    public String minify(String css) throws IOException {
        Path tempIn = Files.createTempFile("esbuild-css-in-", ".css");
        Path tempOut = Files.createTempFile("esbuild-css-out-", ".css");
        try {
            Files.writeString(tempIn, css);
            Process process = new ProcessBuilder()
                .command(esbuildBinary.toAbsolutePath().toString(),
                    "--minify", tempIn.toAbsolutePath().toString(),
                    "--outfile=" + tempOut.toAbsolutePath())
                .redirectErrorStream(true)
                .start();
            Processes.destroyProcess(process, 30, "esbuild");
            return Files.readString(tempOut);
        } finally {
            Files.deleteIfExists(tempIn);
            Files.deleteIfExists(tempOut);
        }
    }

    public void minifyFile(Path input, Path output) throws IOException {
        Process process = new ProcessBuilder()
                .command(List.of(
                        esbuildBinary.toAbsolutePath().toString(),
                        "--minify",
                        input.toAbsolutePath().toString(),
                        "--outfile=" + output.toAbsolutePath()))
                .redirectErrorStream(true)
                .start();
        Processes.destroyProcess(process, 60, "esbuild");
    }
}
