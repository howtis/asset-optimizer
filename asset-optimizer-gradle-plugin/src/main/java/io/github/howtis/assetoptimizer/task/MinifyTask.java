package io.github.howtis.assetoptimizer.task;

import io.github.howtis.assetoptimizer.Cache;
import io.github.howtis.assetoptimizer.esbuild.CssMinifier;
import io.github.howtis.assetoptimizer.esbuild.EsbuildBinary;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class MinifyTask extends DefaultTask {

    @InputFiles
    @SkipWhenEmpty
    public abstract ConfigurableFileCollection getSourceDirs();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    protected void minify(String filePath) throws IOException {
        Path cacheDir = getProject().getGradle().getGradleUserHomeDir()
            .toPath().resolve("caches").resolve("asset-optimizer");
        EsbuildBinary binary = new EsbuildBinary(cacheDir);
        Path esbuildPath = binary.ensureAvailable();
        CssMinifier minifier = new CssMinifier(esbuildPath);

        Path dstDir = getOutputDir().get().getAsFile().toPath();
        Files.createDirectories(dstDir);

        for (java.io.File srcDirFile : getSourceDirs().getFiles()) {
            Path srcDir = srcDirFile.toPath();
            FileTree cssFiles = getProject().fileTree(srcDir.toFile(), files ->
                files.include(filePath));
            cssFiles.forEach(file -> {
                Path relative = srcDir.relativize(file.toPath());
                Path output = dstDir.resolve(relative);
                try {
                    if (Cache.shouldSkip(file.toPath(), output)) {
                        return;
                    }
                    Files.createDirectories(output.getParent());
                    minifier.minifyFile(file.toPath(), output);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to minify " + file, e);
                }
            });
        }
    }
}
