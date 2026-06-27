package io.github.howtis.assetoptimizer;

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
}
