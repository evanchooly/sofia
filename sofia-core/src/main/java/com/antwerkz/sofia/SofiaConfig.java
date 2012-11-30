package com.antwerkz.sofia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

public class SofiaConfig {
  private String bundleName = "sofia";
  private String className;
  private String packageName = "com.antwerkz.sofia";
  private File outputDirectory;
  private LoggingType type = LoggingType.NONE;
  private boolean useControl = false;
  private Map<String, String> properties;
  private boolean generateJavascript;
  private File javascriptOutputFile;

  public String getBundleName() {
    return bundleName;
  }

  public String getClassName() {
    return className;
  }

  public File getOutputDirectory() {
    return outputDirectory;
  }

  public String getPackageName() {
    return packageName;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public LoggingType getType() {
    return type;
  }

  public boolean isUseControl() {
    return useControl;
  }

  public SofiaConfig setBundleName(String bundleName) {
    this.bundleName = bundleName;
    return this;
  }

  public SofiaConfig setClassName(String className) {
    this.className = className;
    return this;
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

  public SofiaConfig setProperties(File propertiesFile) {
    try {
      this.properties = loadProperties(new FileInputStream(propertiesFile));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    return this;
  }

  public SofiaConfig setProperties(Map<String, String> properties) {
    this.properties = properties;
    return this;
  }

  public SofiaConfig setProperties(InputStream stream) {
    this.properties = loadProperties(stream);
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

  private Map<String, String> loadProperties(InputStream inputStream) {
    try (InputStream stream = inputStream) {
      properties = new TreeMap<>();
      Properties props = new Properties();
      props.load(stream);
      for (Entry<Object, Object> entry : props.entrySet()) {
        properties.put((String) entry.getKey(), (String) entry.getValue());
      }
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    return properties;
  }

  public boolean isGenerateJavascript() {
    return generateJavascript;
  }

  public SofiaConfig setGenerateJavascript(boolean generateJavascript) {
    this.generateJavascript = generateJavascript;
    return this;
  }

  public File getJavascriptOutputFile() {
    return javascriptOutputFile;
  }

  public SofiaConfig setJavascriptOutputFile(File file) {
    this.javascriptOutputFile = file;
    return this;
  }
}
