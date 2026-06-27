package io.github.howtis.assetoptimizer;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class AssetOptimizerPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        // TODO: Register AssetOptimizerExtension
        // TODO: Hook into processResources to run optimizers
    }
}
