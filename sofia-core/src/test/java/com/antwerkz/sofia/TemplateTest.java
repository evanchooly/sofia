package com.antwerkz.sofia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TemplateTest {
  @Test
  public void control() {
    File properties = new File("src/test/resources/template.properties");
    SofiaConfig sofiaConfig = new SofiaConfig()
      .setPackageName("utils")
      .setBundleName("template")
      .setProperties(properties)
      .setOutputDirectory(new File("target"))
      .setUseControl(false);
    try {
      new LocalizerGenerator(sofiaConfig).write();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    Assert.assertFalse(readFile("target/utils/Template.java").contains("SofiaControl"));

    sofiaConfig.setUseControl(true);
    try {
      new LocalizerGenerator(sofiaConfig).write();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    Assert.assertTrue(readFile("target/utils/Template.java").contains("SofiaControl"));
  }

  private String readFile(String s) {
    StringBuilder builder = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(s))) {
      while (reader.ready()) {
        builder.append(reader.readLine())
          .append("\n");
      }
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    return builder.toString();
  }
}
