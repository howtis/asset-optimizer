package io.github.howtis.assetoptimizer.task;

import io.github.howtis.assetoptimizer.Processes;
import io.github.howtis.assetoptimizer.webp.WebpBinary;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class ConvertWebpTask extends DefaultTask {

    @InputDirectory
    @SkipWhenEmpty
    public abstract DirectoryProperty getSourceDir();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    @TaskAction
    void convert() throws IOException {
        Path cacheDir = getProject().getGradle().getGradleUserHomeDir()
                .toPath().resolve("caches").resolve("asset-optimizer");
        Path cwebp = new WebpBinary(cacheDir).ensureAvailable();

        Path srcDir = getSourceDir().get().getAsFile().toPath();
        Path dstDir = getOutputDir().get().getAsFile().toPath();
        Files.createDirectories(dstDir);

        FileTree imageFiles = getProject().fileTree(srcDir.toFile(), files ->
            files.include("**/*.jpg", "**/*.jpeg", "**/*.png"));
        imageFiles.forEach(file -> {
            Path relative = srcDir.relativize(file.toPath());
            String webpName = relative.toString().replaceFirst("\\.[^.]+$", ".webp");
            Path output = dstDir.resolve(webpName);
            try {
                convertFile(cwebp, file.toPath(), output);
            } catch (IOException e) {
                throw new RuntimeException("Failed to convert " + file + " to WebP", e);
            }
        });
    }

    public static void convertFile(Path cwebp, Path input, Path output) throws IOException {
        Files.createDirectories(output.getParent());
        Process process = new ProcessBuilder()
                .command(List.of(
                        cwebp.toAbsolutePath().toString(),
                        "-q", "80",
                        input.toAbsolutePath().toString(),
                        "-o", output.toAbsolutePath().toString()))
                .redirectErrorStream(true)
                .start();
        Processes.destroyProcess(process, 30, "cwebp");
    }
}
