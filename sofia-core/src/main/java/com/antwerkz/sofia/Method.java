package com.antwerkz.sofia;

import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Method {
    private String value;
    private Boolean logged = Boolean.FALSE;
    private int argCount;
    private List<String> arguments = new ArrayList<String>();
    private List<String> parameters = new ArrayList<String>();
    private String key;
    private String logLevel;
    private String name;

    public Method(LoggingType type, String propKey, String value) {
        this.key = propKey;
        name = propKey;
        if (key.startsWith("@")) {
            logged = Boolean.TRUE;
            logLevel = key.substring(1, key.indexOf(".")).toLowerCase();
            name = key.substring(key.indexOf(".")+1);
        }
        this.value = value;
        countArguments(value);
    }

    public Boolean getLogged() {
        return logged;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public String getKey() {
        return key;
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
        if (format == null) {
            return "Object";
        } else if (format instanceof DateFormat) {
            return "java.util.Date";
        } else if (format instanceof NumberFormat) {
            return "Number";
        }
        return "Object";
    }

    public String getMethodName() {
        final String[] parts = name.split("\\.");
        StringBuilder name = new StringBuilder();
        for (String part : parts) {
            name.append(name.length() != 0 ? LocalizerGenerator.capitalize(part) : part);
        }
        return name.toString();
    }

    public String getLoggerName() {
        final String[] parts = name.split("\\.");
        StringBuilder name = new StringBuilder();
        name.append("log");
        for (String part : parts) {
            name.append(Character.toTitleCase(part.charAt(0)))
                .append(part.substring(1));
        }
        return name.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder()
            .append("Method {")
            .append("key='").append(key).append('\'')
            .append(", parameters=").append(parameters)
            .append(", logged=").append(logged)
            .append(", logLevel='").append(logLevel).append('\'')
            .append(", value='").append(value).append('\'')
            .append(", argCount=").append(argCount)
            .append(", arguments=").append(arguments)
            .append('}');
        return sb.toString();
    }
}