package com.antwerkz.sofia.play;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.antwerkz.sofia.LocalizerGenerator;
import com.antwerkz.sofia.SofiaConfig;
import play.PlayPlugin;

public class SofiaPlugin extends PlayPlugin {
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
        .setBundleName("messages")
        .setProperties(findProperties())
        .setOutputDirectory(targetDir)
        .setUseControl(true))
        .write();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private InputStream findProperties() throws FileNotFoundException {
    InputStream stream = getClass().getClassLoader().getResourceAsStream("/messages");
    if(stream == null) {
      stream = new FileInputStream("conf/messages");
    }
    return stream;
  }

}