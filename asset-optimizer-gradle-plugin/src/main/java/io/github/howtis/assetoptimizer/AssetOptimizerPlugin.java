package io.github.howtis.assetoptimizer;

import io.github.howtis.assetoptimizer.task.MinifyCssTask;
import io.github.howtis.assetoptimizer.task.MinifyHtmlTask;
import io.github.howtis.assetoptimizer.task.MinifyJsTask;
import io.github.howtis.assetoptimizer.task.OptimizeJpegTask;
import io.github.howtis.assetoptimizer.task.OptimizePngTask;
import io.github.howtis.assetoptimizer.task.OptimizeSvgTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public class AssetOptimizerPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        AssetOptimizerExtension extension = project.getExtensions().create(
            "assetOptimizer", AssetOptimizerExtension.class);

        extension.getSourceDir().convention(
            project.getLayout().getProjectDirectory().dir("src/main/resources/static"));
        extension.getOutputDir().convention(
            project.getLayout().getBuildDirectory().dir("asset-optimizer"));

        TaskProvider<MinifyCssTask> minifyCss = project.getTasks().register(
            "minifyCss", MinifyCssTask.class, task -> {
                task.getSourceDir().convention(extension.getSourceDir());
                task.getOutputDir().set(
                    extension.getOutputDir().map(d -> d.dir("css")));
            });

        TaskProvider<MinifyJsTask> minifyJs = project.getTasks().register(
            "minifyJs", MinifyJsTask.class, task -> {
                task.getSourceDir().convention(extension.getSourceDir());
                task.getOutputDir().set(
                    extension.getOutputDir().map(d -> d.dir("js")));
            });

        TaskProvider<MinifyHtmlTask> minifyHtml = project.getTasks().register(
            "minifyHtml", MinifyHtmlTask.class, task -> {
                task.getSourceDir().convention(extension.getSourceDir());
                task.getOutputDir().set(
                    extension.getOutputDir().map(d -> d.dir("html")));
            });

        TaskProvider<OptimizePngTask> optimizePng = project.getTasks().register(
            "optimizePng", OptimizePngTask.class, task -> {
                task.getSourceDir().convention(extension.getSourceDir());
                task.getOutputDir().set(
                    extension.getOutputDir().map(d -> d.dir("png")));
            });

        TaskProvider<OptimizeSvgTask> optimizeSvg = project.getTasks().register(
            "optimizeSvg", OptimizeSvgTask.class, task -> {
                task.getSourceDir().convention(extension.getSourceDir());
                task.getOutputDir().set(
                    extension.getOutputDir().map(d -> d.dir("svg")));
            });

        TaskProvider<OptimizeJpegTask> optimizeJpeg = project.getTasks().register(
            "optimizeJpeg", OptimizeJpegTask.class, task -> {
                task.getSourceDir().convention(extension.getSourceDir());
                task.getOutputDir().set(
                    extension.getOutputDir().map(d -> d.dir("jpeg")));
            });

        project.getTasks().matching(task -> task.getName().equals("processResources"))
            .all(processResources -> {
                processResources.dependsOn(minifyCss);
                processResources.dependsOn(minifyHtml);
                processResources.dependsOn(minifyJs);
                processResources.dependsOn(optimizeJpeg);
                processResources.dependsOn(optimizePng);
                processResources.dependsOn(optimizeSvg);
            });
    }
}
