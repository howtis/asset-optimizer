package io.github.howtis.assetoptimizer.task;

import org.gradle.api.tasks.TaskAction;
import org.gradle.work.InputChanges;

import java.io.IOException;

public abstract class MinifyJsTask extends MinifyTask {

    @TaskAction
    public void minify(InputChanges changes) throws IOException {
        super.minify(changes, "**/*.js");
    }
}
