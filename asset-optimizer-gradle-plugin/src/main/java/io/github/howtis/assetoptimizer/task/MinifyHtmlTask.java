package io.github.howtis.assetoptimizer.task;

import io.github.howtis.assetoptimizer.html.HtmlMinifier;
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

public abstract class MinifyHtmlTask extends DefaultTask {

    @InputDirectory
    @SkipWhenEmpty
    public abstract DirectoryProperty getSourceDir();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    @TaskAction
    void minify() throws IOException {
        HtmlMinifier minifier = new HtmlMinifier();

        Path srcDir = getSourceDir().get().getAsFile().toPath();
        Path dstDir = getOutputDir().get().getAsFile().toPath();
        Files.createDirectories(dstDir);

        FileTree htmlFiles = getProject().fileTree(srcDir.toFile(), files ->
            files.include("**/*.html"));
        htmlFiles.forEach(file -> {
            Path relative = srcDir.relativize(file.toPath());
            Path output = dstDir.resolve(relative);
            try {
                Files.createDirectories(output.getParent());
                minifier.minifyFile(file.toPath(), output);
            } catch (IOException e) {
                throw new RuntimeException("Failed to minify " + file, e);
            }
        });
    }
}
