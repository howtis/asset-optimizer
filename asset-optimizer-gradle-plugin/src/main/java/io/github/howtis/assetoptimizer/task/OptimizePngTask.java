package io.github.howtis.assetoptimizer.task;

import io.github.howtis.assetoptimizer.Cache;
import io.github.howtis.assetoptimizer.oxipng.OxipngBinary;
import io.github.howtis.assetoptimizer.oxipng.PngOptimizer;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class OptimizePngTask extends DefaultTask {

    @InputFiles
    @SkipWhenEmpty
    public abstract ConfigurableFileCollection getSourceDirs();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    @TaskAction
    void optimize() throws IOException {
        Path cacheDir = getProject().getGradle().getGradleUserHomeDir()
            .toPath().resolve("caches").resolve("asset-optimizer");
        OxipngBinary binary = new OxipngBinary(cacheDir);
        Path oxipngPath = binary.ensureAvailable();
        PngOptimizer optimizer = new PngOptimizer(oxipngPath);

        Path dstDir = getOutputDir().get().getAsFile().toPath();
        Files.createDirectories(dstDir);

        for (java.io.File srcDirFile : getSourceDirs().getFiles()) {
            Path srcDir = srcDirFile.toPath();
            FileTree pngFiles = getProject().fileTree(srcDir.toFile(), files ->
                files.include("**/*.png"));
            pngFiles.forEach(file -> {
                Path relative = srcDir.relativize(file.toPath());
                Path output = dstDir.resolve(relative);
                try {
                    if (Cache.shouldSkip(file.toPath(), output)) {
                        return;
                    }
                    optimizer.optimizeFile(file.toPath(), output);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to optimize " + file, e);
                }
            });
        }
    }
}
