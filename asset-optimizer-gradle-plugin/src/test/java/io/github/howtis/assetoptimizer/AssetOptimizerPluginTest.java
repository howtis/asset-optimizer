package io.github.howtis.assetoptimizer;

import io.github.howtis.assetoptimizer.task.MinifyCssTask;
import io.github.howtis.assetoptimizer.task.MinifyJsTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

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
        assertNotNull(task.getSourceDir().getOrNull());
        assertNotNull(task.getOutputDir().getOrNull());
    }

    @Test
    void minifyJsTaskHasSourceAndOutputConfigured() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.howtis.asset-optimizer");

        MinifyJsTask task = (MinifyJsTask) project.getTasks().getByName("minifyJs");
        assertNotNull(task.getSourceDir().getOrNull());
        assertNotNull(task.getOutputDir().getOrNull());
    }
}
