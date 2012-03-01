package org.miloframework.sofia;

import java.util.ArrayList;
import java.util.List;

public class Method {
    private String name;
    private String value;
    private int argCount;
    List<String> arguments = new ArrayList<String>();
    private String key;

    public Method(String key, String value) {
        this.key = key;
        this.name = toMethodName(key);
        this.value = value;
        countArguments(value);
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getArgCount() {
        return argCount;
    }

    public List<String> getArguments() {
        return arguments;
    }

    private void countArguments(String value) {
        int index = 0;
        while ((index = value.indexOf("{}", index)+1) != 0) {
            arguments.add("arg" + argCount);
            argCount++;
        }
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
}