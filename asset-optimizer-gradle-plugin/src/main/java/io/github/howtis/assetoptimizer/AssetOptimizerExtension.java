package io.github.howtis.assetoptimizer;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;

public abstract class AssetOptimizerExtension {

    public abstract ConfigurableFileCollection getSourceDirs();

    public abstract DirectoryProperty getOutputDir();
}
