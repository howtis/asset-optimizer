package io.github.howtis.assetoptimizer;

import io.github.howtis.assetoptimizer.task.MinifyCssTask;
import io.github.howtis.assetoptimizer.task.MinifyJsTask;
import io.github.howtis.assetoptimizer.task.OptimizePngTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public class AssetOptimizerPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        TaskProvider<MinifyCssTask> minifyCss = project.getTasks().register(
            "minifyCss", MinifyCssTask.class, task -> {
                task.getSourceDir().set(
                    project.getLayout().getProjectDirectory().dir("src/main/resources/static"));
                task.getOutputDir().set(
                    project.getLayout().getBuildDirectory().dir("asset-optimizer/css"));
            });

        TaskProvider<MinifyJsTask> minifyJs = project.getTasks().register(
            "minifyJs", MinifyJsTask.class, task -> {
                task.getSourceDir().set(
                    project.getLayout().getProjectDirectory().dir("src/main/resources/static"));
                task.getOutputDir().set(
                    project.getLayout().getBuildDirectory().dir("asset-optimizer/js"));
            });

        TaskProvider<OptimizePngTask> optimizePng = project.getTasks().register(
            "optimizePng", OptimizePngTask.class, task -> {
                task.getSourceDir().set(
                    project.getLayout().getProjectDirectory().dir("src/main/resources/static"));
                task.getOutputDir().set(
                    project.getLayout().getBuildDirectory().dir("asset-optimizer/png"));
            });

        project.getTasks().matching(task -> task.getName().equals("processResources"))
            .all(processResources -> {
                processResources.dependsOn(minifyCss);
                processResources.dependsOn(minifyJs);
                processResources.dependsOn(optimizePng);
            });
    }
}
