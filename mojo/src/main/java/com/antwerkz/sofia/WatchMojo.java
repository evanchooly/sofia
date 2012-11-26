package com.antwerkz.sofia;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@SuppressWarnings("InfiniteLoopStatement")
@Mojo(name = "watch")
public class WatchMojo extends SofiaMojo {
  @Parameter(property="sofia.watchDelay", defaultValue="1000")
  private int delay;
  @Override
  public void execute() throws MojoExecutionException {
    while (true) {
      super.execute();
      try {
        Thread.sleep(delay);
      } catch (InterruptedException e) {
        throw new MojoExecutionException(e.getMessage(), e);
      }
    }
  }
}
