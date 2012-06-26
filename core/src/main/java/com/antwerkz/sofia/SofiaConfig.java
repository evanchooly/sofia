package com.antwerkz.sofia;

import java.io.File;

public class SofiaConfig {
  private String packageName = "com.antwerkz.sofia";
  private File properties = new File("src/main/resources/sofia.properties");
  private File outputDirectory;
  private LoggingType type = LoggingType.NONE;
  private boolean useControl = false;

  public File getOutputDirectory() {
    return outputDirectory;
  }

  public String getPackageName() {
    return packageName;
  }

  public File getProperties() {
    return properties;
  }

  public LoggingType getType() {
    return type;
  }

  public boolean isUseControl() {
    return useControl;
  }

  public SofiaConfig setOutputDirectory(File outputDirectory) {
    this.outputDirectory = outputDirectory;
    return this;
  }

  public SofiaConfig setOutputDirectory(String outputDirectory) {
    this.outputDirectory = new File(outputDirectory);
    return this;
  }

  public SofiaConfig setPackageName(String packageName) {
    this.packageName = packageName;
    return this;
  }

  public SofiaConfig setProperties(File properties) {
    this.properties = properties;
    return this;
  }

  public SofiaConfig setProperties(String properties) {
    this.properties = new File(properties);
    return this;
  }

  public SofiaConfig setUseControl(boolean useControl) {
    this.useControl = useControl;
    return this;
  }

  public SofiaConfig setType(LoggingType type) {
    this.type = type;
    return this;
  }
}
