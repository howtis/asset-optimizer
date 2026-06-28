package io.github.howtis.assetoptimizer.task;

import io.github.howtis.assetoptimizer.Cache;

import static io.github.howtis.assetoptimizer.FileSize.humanReadable;
import io.github.howtis.assetoptimizer.esbuild.CssMinifier;
import io.github.howtis.assetoptimizer.esbuild.EsbuildBinary;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputFiles;
import org.gradle.work.Incremental;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.work.ChangeType;
import org.gradle.work.InputChanges;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MinifyTask extends DefaultTask {

    @InputFiles
    @Incremental
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract ConfigurableFileCollection getSourceDirs();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    protected void minify(InputChanges changes, String filePattern) throws IOException {
        Path cacheDir = getProject().getGradle().getGradleUserHomeDir()
            .toPath().resolve("caches").resolve("asset-optimizer");
        EsbuildBinary binary = new EsbuildBinary(cacheDir);
        Path esbuildPath = binary.ensureAvailable();
        CssMinifier minifier = new CssMinifier(esbuildPath);

        Path dstDir = getOutputDir().get().getAsFile().toPath();
        Files.createDirectories(dstDir);

        Set<Path> sourceRoots = getSourceDirs().getFiles().stream()
            .map(File::toPath)
            .collect(Collectors.toSet());

        changes.getFileChanges(getSourceDirs()).forEach(change -> {
            if (change.getFileType() == org.gradle.api.file.FileType.DIRECTORY) {
                return;
            }
            Path input = change.getFile().toPath();
            Path srcDir = sourceRoots.stream()
                .filter(input::startsWith)
                .findFirst().orElse(null);
            if (srcDir == null) {
                return;
            }
            Path relative = srcDir.relativize(input);
            String fileName = input.getFileName().toString();
            if (!filePattern.equals("**/*.css") && !filePattern.equals("**/*.js")) {
                return;
            }
            if (!fileName.endsWith(filePattern.substring(filePattern.lastIndexOf('.')))) {
                return;
            }
            Path output = dstDir.resolve(relative);

            if (change.getChangeType() == ChangeType.REMOVED) {
                try {
                    Files.deleteIfExists(output);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete " + output, e);
                }
                return;
            }

            try {
                if (Cache.shouldSkip(input, output)) {
                    return;
                }
                Files.createDirectories(output.getParent());
                long beforeSize = Files.size(input);
                minifier.minifyFile(input, output);
                long afterSize = Files.size(output);
                getLogger().lifecycle(
                    "{} {} {} -> {} ({}% saved)",
                    filePattern.substring(filePattern.lastIndexOf('.') + 1).toUpperCase(),
                    relative,
                    humanReadable(beforeSize),
                    humanReadable(afterSize),
                    beforeSize > 0 ? (beforeSize - afterSize) * 100 / beforeSize : 0);
            } catch (IOException e) {
                throw new RuntimeException("Failed to minify " + input, e);
            }
        });
    }

}
