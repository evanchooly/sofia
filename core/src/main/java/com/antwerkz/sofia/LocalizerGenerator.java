package com.antwerkz.sofia;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class LocalizerGenerator {
  private List<Method> methods = new ArrayList<>();
  private String pkgName;
  private File outputDirectory;
  private String bundleName;
  private SofiaConfig config;

  public LocalizerGenerator(SofiaConfig sofiaConfig) throws IOException {
    config = sofiaConfig;
    this.pkgName = sofiaConfig.getPackageName();
    this.outputDirectory = sofiaConfig.getOutputDirectory();
    bundleName = sofiaConfig.getBundleName();
    if (bundleName.contains(".")) {
      bundleName = bundleName.substring(0, bundleName.indexOf("."));
    }
    Map<String, List<Method>> loggers = new HashMap<>();
    for (String level : config.getType().getLoggingLevels()) {
      loggers.put(level, new ArrayList<Method>());
    }
    Map<String, String> map = sofiaConfig.getProperties();
    for (Entry<String, String> entry : map.entrySet()) {
      Method method = new Method(config.getType(), entry.getKey(), entry.getValue());
      methods.add(method);
      if (method.getLogged()) {
        List<Method> level = loggers.get(method.getLogLevel());
        if (level == null) {
          throw new IllegalArgumentException(
            String.format("Invalid logging level '%s' for logging type '%s'", method.getLogLevel(),
              config.getType()));
        }
        level.add(method);
      }
    }
  }

  public static String capitalize(String text) {
    return Character.toTitleCase(text.charAt(0)) + text.substring(1);
  }

  public String toString() {
    Configuration cfg = new Configuration();
    try {
      Template template = new Template("sofia",
        new InputStreamReader(getClass().getResourceAsStream("/sofia.ftl")), cfg);
      Writer out = new StringWriter();
      template.process(buildDataMap(), out);
      out.flush();
      return out.toString();
    } catch (IOException | TemplateException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private Map<String, Object> buildDataMap() {
    final HashMap<String, Object> map = new HashMap<>();
    map.put("packageName", config.getPackageName());
    map.put("className", bundleName);
    map.put("methods", methods);
    map.put("bundleName", bundleName);
    map.put("imports", config.getType().getImports());
    map.put("logger", config.getType().name());
    map.put("useControl", config.isUseControl());
    return map;
  }

  public void write() {
    File file = new File(outputDirectory, String.format("%s/%s.java", pkgName.replace('.', '/'),
      capitalize(bundleName)));
    file.getParentFile().mkdirs();
    try (PrintWriter stream = new PrintWriter(file, "UTF-8")) {
      stream.println(this);
      stream.flush();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}