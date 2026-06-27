package io.github.howtis.assetoptimizer.task;

import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

public abstract class MinifyJsTask extends MinifyTask {

    @TaskAction
    public void minify() throws IOException {
        super.minify("**/*.js");
    }
}
