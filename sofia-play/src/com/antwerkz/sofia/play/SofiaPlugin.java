package com.antwerkz.sofia.play;

import java.io.File;
import java.io.IOException;

import com.antwerkz.sofia.LocalizerGenerator;
import com.antwerkz.sofia.SofiaConfig;
import play.PlayPlugin;

public class SofiaPlugin extends PlayPlugin {
    private File properties = new File("conf/messages");
    private File targetDir = new File("app");

  @Override
    public void onApplicationStart() {
        generate();
    }

    @Override
    public void detectChange() {
        generate();
    }

    private void generate() {
      try {
        new LocalizerGenerator(new SofiaConfig()
          .setPackageName("utils")
          .setProperties(properties)
          .setOutputDirectory(targetDir)
          .setUseControl(true)).write();
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
}