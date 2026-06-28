package io.github.howtis.assetoptimizer;

import io.github.howtis.assetoptimizer.task.ConvertWebpTask;
import io.github.howtis.assetoptimizer.task.MinifyCssTask;
import io.github.howtis.assetoptimizer.task.MinifyHtmlTask;
import io.github.howtis.assetoptimizer.task.MinifyJsTask;
import io.github.howtis.assetoptimizer.task.OptimizeJpegTask;
import io.github.howtis.assetoptimizer.task.OptimizePngTask;
import io.github.howtis.assetoptimizer.task.OptimizeSvgTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AssetOptimizerPluginTest {

    @Test
    void pluginAppliesSuccessfully() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        assertNotNull(project.getPluginManager().findPlugin("io.github.howtis.asset-optimizer"));
    }

    @Test
    void registersMinifyCssTask() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        MinifyCssTask task = (MinifyCssTask) project.getTasks().getByName("minifyCss");
        assertNotNull(task);
    }

    @Test
    void registersMinifyJsTask() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        MinifyJsTask task = (MinifyJsTask) project.getTasks().getByName("minifyJs");
        assertNotNull(task);
    }

    @Test
    void minifyCssTaskHasSourceAndOutputConfigured() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        MinifyCssTask task = (MinifyCssTask) project.getTasks().getByName("minifyCss");
        assertNotNull(task.getSourceDirs());
        assertNotNull(task.getOutputDir().getOrNull());
    }

    @Test
    void minifyJsTaskHasSourceAndOutputConfigured() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        MinifyJsTask task = (MinifyJsTask) project.getTasks().getByName("minifyJs");
        assertNotNull(task.getSourceDirs());
        assertNotNull(task.getOutputDir().getOrNull());
    }

    @Test
    void registersOptimizePngTask() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        OptimizePngTask task = (OptimizePngTask) project.getTasks().getByName("optimizePng");
        assertNotNull(task);
    }

    @Test
    void registersMinifyHtmlTask() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        MinifyHtmlTask task = (MinifyHtmlTask) project.getTasks().getByName("minifyHtml");
        assertNotNull(task);
    }

    @Test
    void minifyHtmlTaskHasSourceAndOutputConfigured() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        MinifyHtmlTask task = (MinifyHtmlTask) project.getTasks().getByName("minifyHtml");
        assertNotNull(task.getSourceDirs());
        assertNotNull(task.getOutputDir().getOrNull());
    }

    @Test
    void registersOptimizeSvgTask() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        OptimizeSvgTask task = (OptimizeSvgTask) project.getTasks().getByName("optimizeSvg");
        assertNotNull(task);
    }

    @Test
    void registersOptimizeJpegTask() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        OptimizeJpegTask task = (OptimizeJpegTask) project.getTasks().getByName("optimizeJpeg");
        assertNotNull(task);
    }

    @Test
    void optimizeJpegTaskHasSourceAndOutputConfigured() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        OptimizeJpegTask task = (OptimizeJpegTask) project.getTasks().getByName("optimizeJpeg");
        assertNotNull(task.getSourceDirs());
        assertNotNull(task.getOutputDir().getOrNull());
    }

    @Test
    void optimizeSvgTaskHasSourceAndOutputConfigured() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        OptimizeSvgTask task = (OptimizeSvgTask) project.getTasks().getByName("optimizeSvg");
        assertNotNull(task.getSourceDirs());
        assertNotNull(task.getOutputDir().getOrNull());
    }

    @Test
    void optimizePngTaskHasSourceAndOutputConfigured() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        OptimizePngTask task = (OptimizePngTask) project.getTasks().getByName("optimizePng");
        assertNotNull(task.getSourceDirs());
        assertNotNull(task.getOutputDir().getOrNull());
    }

    @Test
    void registersConvertWebpTask() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        ConvertWebpTask task = (ConvertWebpTask) project.getTasks().getByName("convertWebp");
        assertNotNull(task);
    }

    @Test
    void convertWebpTaskHasSourceAndOutputConfigured() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        ConvertWebpTask task = (ConvertWebpTask) project.getTasks().getByName("convertWebp");
        assertNotNull(task.getSourceDirs());
        assertNotNull(task.getOutputDir().getOrNull());
    }

    @Test
    void registersExtension() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        AssetOptimizerExtension extension = project.getExtensions()
            .getByType(AssetOptimizerExtension.class);
        assertNotNull(extension);
    }

    @Test
    void extensionHasDefaultSourceDirs() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        AssetOptimizerExtension extension = project.getExtensions()
            .getByType(AssetOptimizerExtension.class);
        assertFalse(extension.getSourceDirs().isEmpty());
    }

    @Test
    void extensionHasDefaultOutputDir() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        AssetOptimizerExtension extension = project.getExtensions()
            .getByType(AssetOptimizerExtension.class);
        assertEquals("asset-optimizer",
            extension.getOutputDir().get().getAsFile().getName());
    }

    @Test
    void customSourceDirsPropagatesToTasks() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        AssetOptimizerExtension extension = project.getExtensions()
            .getByType(AssetOptimizerExtension.class);
        extension.getSourceDirs().setFrom(
            project.getLayout().getProjectDirectory().dir("custom/static"));

        MinifyCssTask task = (MinifyCssTask) project.getTasks().getByName("minifyCss");
        assertNotNull(task.getSourceDirs());
    }

    @Test
    void customOutputDirPropagatesToTasks() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        AssetOptimizerExtension extension = project.getExtensions()
            .getByType(AssetOptimizerExtension.class);
        extension.getOutputDir().set(
            project.getLayout().getBuildDirectory().dir("custom-output"));

        OptimizePngTask task = (OptimizePngTask) project.getTasks().getByName("optimizePng");
        assertNotNull(task.getOutputDir().getOrNull());
    }
}
