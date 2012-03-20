package com.antwerkz.sofia;

import java.io.File;
import java.io.FileInputStream;
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
import java.util.Properties;
import java.util.TreeMap;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class LocalizerGenerator {
    private List<Method> methods = new ArrayList<Method>();
    private String pkgName;
    private File outputDirectory;
    private String bundleName;
    private SofiaConfig config;
    private Map<String, List<Method>> loggers = new HashMap<String, List<Method>>();

    public LocalizerGenerator(SofiaConfig sofiaConfig) throws IOException {
        config = sofiaConfig;
        this.pkgName = sofiaConfig.getPackageName();
        this.outputDirectory = sofiaConfig.getOutputDirectory();
        bundleName = sofiaConfig.getProperties().getName();
        if (bundleName.contains(".")) {
            bundleName = bundleName.substring(0, bundleName.indexOf("."));
        }
        for (String level : config.getType().getLoggingLevels()) {
            loggers.put(level, new ArrayList<Method>());
        }
        Map<String, String> map = loadPropertiesFile(sofiaConfig.getProperties());
        for (Entry<String, String> entry : map.entrySet()) {
            Method method = new Method(config.getType(), entry.getKey(), entry.getValue());
            methods.add(method);
            if(method.getLogged()) {
                List<Method> level = loggers.get(method.getLogLevel());
                if(level == null) {
                    throw new IllegalArgumentException(
                        String.format("Invalid logging level '%s' for logging type '%s'", method.getLogLevel(),
                            config.getType()));
                }
                level.add(method);
            }
        }
    }

    private Map<String, String> loadPropertiesFile(File file) throws IOException {
        Properties props = new Properties();
        Map<String, String> map = new TreeMap<String, String>();
        props.load(new FileInputStream(file));
        for (Entry<Object, Object> entry : props.entrySet()) {
            map.put((String) entry.getKey(), (String) entry.getValue());
        }
        return map;
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
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (TemplateException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Map<String, Object> buildDataMap() {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("packageName", config.getPackageName());
        map.put("methods", methods);
        map.put("bundleName", bundleName);
        map.put("imports", config.getType().getImports());
        map.put("logger", config.getType().name());
        return map;
    }

    public void write() {
        PrintWriter stream = null;
        File file = new File(outputDirectory, String.format("%s/Localizer.java", pkgName.replace('.', '/')));
        file.getParentFile().mkdirs();
        try {
            stream = new PrintWriter(file, "UTF-8");
            stream.println(this);
            stream.flush();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
