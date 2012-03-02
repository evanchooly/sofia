package com.antwerkz.sofia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

class LocalizerGenerator {
    private List<Method> methods = new ArrayList<Method>();
    private String pkgName;
    private File baseFile;
    private String baseName;
    private String ext;
    private Map<String, String> messages;
    private Set<String> keySet;

    public LocalizerGenerator(String pkgName, File file) throws IOException {
        this.pkgName = pkgName;
        baseFile = file;
        final String fileName = file.getName();
        baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.') + 1) : "";
        messages = new HashMap<String, String>();
        Map<String, String> map = loadPropertiesFile(file);
        keySet = map.keySet();
        for (Entry<String, String> entry : map.entrySet()) {
            methods.add(buildMethod(entry.getKey(), entry.getValue()));
        }
        record(null, map);
        record(extractLocale(file), map);
        loadOtherLocales(map, file);
    }

    private void record(String locale, Map<String, String> map) {
        for (Entry<String, String> entry : map.entrySet()) {
            messages.put(String.format("%s%s", locale == null ? "" : locale + ".", entry.getKey()), entry.getValue());
        }
    }

    private void loadOtherLocales(Map<String, String> map, File baseFile) throws IOException {
        final File root = baseFile.getParentFile();
        final String fileName = baseFile.getName();
        final File[] files = root.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.equals(fileName) && name.startsWith(baseName) && name.endsWith(ext);
            }
        });
        for (File file : files) {
            record(extractLocale(file), loadPropertiesFile(file));
        }
    }

    private Map<String, String> loadPropertiesFile(File file) throws IOException {
        Properties props = new Properties();
        Map<String, String> map = new TreeMap<String, String>();
        props.load(new FileInputStream(file));
        for (Entry<Object, Object> entry : props.entrySet()) {
            map.put((String) entry.getKey(), (String) entry.getValue());
        }
        if(keySet != null && !map.keySet().equals(keySet)) {
            System.out.printf("WARNING: %s has a different set of properties than %s.\n", file, baseFile);
        }
        return map;
    }

    private String extractLocale(File file) {
        String stripped = file.getName().substring(baseName.length() + 1);
        stripped = stripped.substring(0, stripped.length() - ext.length());
        if(stripped.endsWith(".")) {
            stripped = stripped.substring(0, stripped.length() - 1);
        }
        return stripped.isEmpty() ? null : stripped;
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
        map.put("messages", messages);
        return map;
    }

    public List<Method> getMethods() {
        return methods;
    }
}
