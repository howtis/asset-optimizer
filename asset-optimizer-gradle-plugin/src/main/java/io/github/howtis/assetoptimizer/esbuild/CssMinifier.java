package io.github.howtis.assetoptimizer.esbuild;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            destroyProcess(process, 30);
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
        destroyProcess(process, 60);
    }

    private void destroyProcess(Process process, int i) throws IOException {
        try {
            boolean finished = process.waitFor(i, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new IOException("esbuild timed out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
            throw new IOException("esbuild interrupted", e);
        }
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            String stderr;
            try (InputStream err = process.getInputStream()) {
                stderr = new String(err.readAllBytes(), StandardCharsets.UTF_8);
            }
            throw new IOException("esbuild exited with code " + exitCode + ": " + stderr);
        }
    }
}
