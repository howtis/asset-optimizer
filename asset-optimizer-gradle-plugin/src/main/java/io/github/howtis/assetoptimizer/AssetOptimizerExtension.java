package io.github.howtis.assetoptimizer;

import org.gradle.api.file.DirectoryProperty;

public abstract class AssetOptimizerExtension {

    public abstract DirectoryProperty getSourceDir();

    public abstract DirectoryProperty getOutputDir();
}
