package com.antwerkz.sofia;

import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Pattern pattern = Pattern.compile("\\{\\d\\}");
        Matcher matcher = pattern.matcher(value);
        int count = matcher.groupCount();
        Format[] formats = new MessageFormat(value).getFormats();
        argCount = formats.length;
        for (int i = 0; i < formats.length; i++) {
            arguments.add("arg" + i);
        }
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