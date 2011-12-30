package org.miloframework.localizer;

import java.io.IOException;
import java.io.InputStreamReader;
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

class LocalizerGenerator {
    private List<Method> methods = new ArrayList<Method>();
    private String pkgName;

    public LocalizerGenerator(String pkgName, Map<String, String> map) {
        this.pkgName = pkgName;
        for (Entry<String, String> entry : map.entrySet()) {
            methods.add(buildMethod(entry.getKey(), entry.getValue()));
        }
    }

    private Method buildMethod(String key, String value) {
        return new Method(key, value);
    }

    private String toMethodName(String key) {
        final String[] parts = key.split("\\.");
        StringBuilder name = new StringBuilder();
        for (String part : parts) {
            if(name.length() != 0) {
                name.append(part.substring(0, 1).toUpperCase() + part.substring(1));
            } else {
                name.append(part);
            }
        }
        return name.toString();
    }


    public String toString()  {
        Configuration cfg = new Configuration();
        try {
            Template template = new Template("localizer",
                new InputStreamReader(getClass().getResourceAsStream("/localizer.fm")), cfg);
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
        return map;
    }

    public List<Method> getMethods() {
        return methods;
    }
}
