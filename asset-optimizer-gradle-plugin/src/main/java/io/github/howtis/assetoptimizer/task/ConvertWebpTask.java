package io.github.howtis.assetoptimizer.task;

import io.github.howtis.assetoptimizer.Cache;
import io.github.howtis.assetoptimizer.Processes;

import static io.github.howtis.assetoptimizer.FileSize.humanReadable;
import io.github.howtis.assetoptimizer.webp.WebpBinary;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileType;
import org.gradle.api.tasks.InputFiles;
import org.gradle.work.Incremental;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.ChangeType;
import org.gradle.work.InputChanges;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ConvertWebpTask extends DefaultTask {

    @InputFiles
    @Incremental
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract ConfigurableFileCollection getSourceDirs();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    @TaskAction
    void convert(InputChanges changes) throws IOException {
        Path cacheDir = getProject().getGradle().getGradleUserHomeDir()
                .toPath().resolve("caches").resolve("asset-optimizer");
        Path cwebp = new WebpBinary(cacheDir).ensureAvailable();

        Path dstDir = getOutputDir().get().getAsFile().toPath();
        Files.createDirectories(dstDir);

        Set<Path> sourceRoots = getSourceDirs().getFiles().stream()
            .map(File::toPath)
            .collect(Collectors.toSet());

        changes.getFileChanges(getSourceDirs()).forEach(change -> {
            if (change.getFileType() == FileType.DIRECTORY) {
                return;
            }
            Path input = change.getFile().toPath();
            String fileName = input.getFileName().toString().toLowerCase();
            if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && !fileName.endsWith(".png")) {
                return;
            }
            Path srcDir = sourceRoots.stream()
                .filter(input::startsWith)
                .findFirst().orElse(null);
            if (srcDir == null) {
                return;
            }
            Path relative = srcDir.relativize(input);
            String webpName = relative.toString().replaceFirst("\\.[^.]+$", ".webp");
            Path output = dstDir.resolve(webpName);

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
                long beforeSize = Files.size(input);
                convertFile(cwebp, input, output);
                long afterSize = Files.size(output);
                getLogger().lifecycle(
                    "WebP {} {} -> {} ({}% saved)",
                    relative,
                    humanReadable(beforeSize),
                    humanReadable(afterSize),
                    beforeSize > 0 ? (beforeSize - afterSize) * 100 / beforeSize : 0);
            } catch (IOException e) {
                throw new RuntimeException("Failed to convert " + input + " to WebP", e);
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
