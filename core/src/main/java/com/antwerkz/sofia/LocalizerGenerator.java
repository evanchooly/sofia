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

    public LocalizerGenerator(String pkgName, File file, File outputDirectory) throws IOException {
        this.pkgName = pkgName;
        this.outputDirectory = outputDirectory;
        bundleName = file.getName();
        if (bundleName.contains(".")) {
            bundleName = bundleName.substring(0, bundleName.indexOf("."));
        }

        Map<String, String> map = loadPropertiesFile(file);
        for (Entry<String, String> entry : map.entrySet()) {
            methods.add(buildMethod(entry.getKey(), entry.getValue()));
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

    private Method buildMethod(String key, String value) {
        return new Method(key, value);
    }

    public String toString() {
        Configuration cfg = new Configuration();
        try {
            Template template = new Template("sofia",
                new InputStreamReader(getClass().getResourceAsStream("/sofia.fm")), cfg);
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
        map.put("packageName", pkgName);
        map.put("methods", methods);
        map.put("bundleName", bundleName);
        return map;
    }

    public void write() {
        PrintWriter stream = null;
        File file = new File(outputDirectory, String.format("%s/Localizer.java", pkgName.replace('.', '/')));
        file.getParentFile().mkdirs();
        try {
            stream = new PrintWriter(
                file,
                "UTF-8");
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
