package com.antwerkz.sofia;

import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Method {
    private String name;
    private String value;
    private int argCount;
    private List<String> arguments = new ArrayList<String>();
    private List<String> parameters = new ArrayList<String>();
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

    public List<String> getParameters() {
        return parameters;
    }

    private void countArguments(String value) {
        MessageFormat messageFormat = new MessageFormat(value);
        Format[] formats = messageFormat.getFormats();
        argCount = messageFormat.getFormats().length;
        for (int i = 0; i < argCount; i++) {
            parameters.add(String.format("%s arg%d", getType(formats[i]), i));
            arguments.add(String.format("arg%d", i));
        }
    }

    private String getType(Format format) {
        if(format == null) {
            return "Object";
        } else if(format instanceof DateFormat) {
            return "java.util.Date";
        } else if(format instanceof NumberFormat) {
            return "Number";
        }
        return "Object";
    }

    private String toMethodName(String key) {
        final String[] parts = key.split("\\.");
        StringBuilder name = new StringBuilder();
        for (String part : parts) {
            name.append(name.length() != 0 ? part.substring(0, 1).toUpperCase() + part.substring(1) : part);
        }
        return name.toString();
    }
}