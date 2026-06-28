package io.github.howtis.assetoptimizer.task;

import io.github.howtis.assetoptimizer.svg.SvgOptimizer;
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

public abstract class OptimizeSvgTask extends DefaultTask {

    @InputDirectory
    @SkipWhenEmpty
    public abstract DirectoryProperty getSourceDir();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    @TaskAction
    void optimize() throws IOException {
        SvgOptimizer optimizer = new SvgOptimizer();

        Path srcDir = getSourceDir().get().getAsFile().toPath();
        Path dstDir = getOutputDir().get().getAsFile().toPath();
        Files.createDirectories(dstDir);

        FileTree svgFiles = getProject().fileTree(srcDir.toFile(), files ->
            files.include("**/*.svg"));
        svgFiles.forEach(file -> {
            Path relative = srcDir.relativize(file.toPath());
            Path output = dstDir.resolve(relative);
            try {
                Files.createDirectories(output.getParent());
                optimizer.optimizeFile(file.toPath(), output);
            } catch (IOException e) {
                throw new RuntimeException("Failed to optimize " + file, e);
            }
        });
    }
}
