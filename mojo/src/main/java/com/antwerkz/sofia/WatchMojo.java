package com.antwerkz.sofia;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "watch")
public class WatchMojo extends SofiaMojo {
  @Override
  public void execute() throws MojoExecutionException {
    try {
      WatchService watcher = FileSystems.getDefault().newWatchService();
      Path inputPath = getInputFile().toPath();
      Path dir = inputPath.getParent();
      dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
      getLog().info("Watching " + inputPath + " for changes");
      generate();
      boolean valid = true;
      while (valid) {
        WatchKey key = watcher.take();
        for (WatchEvent<?> event : key.pollEvents()) {
          if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
            continue;
          }
          WatchEvent<Path> ev = (WatchEvent<Path>) event;
          if(dir.resolve(ev.context()).equals(inputPath)) {
            generate();
          }
        }
        valid = key.reset();
      }
    } catch (IOException | InterruptedException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
