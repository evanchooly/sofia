package org.miloframework.localizer;

import java.util.ArrayList;
import java.util.List;

public class Method {
    private String name;
    private String value;
    private int argCount;
    List<String> arguments = new ArrayList<String>();
    private String argumentString;
    private String body;
    private String signature;
    private StringBuilder argNames;
    private String key;

    public Method(String key, String value) {
        this.key = key;
        this.name = toMethodName(key);
        this.value = value;
        buildArguments(value);
        buildBody();
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

    private void buildArguments(String value) {
        int index = 0;
        StringBuilder sigArgs = new StringBuilder();
        argNames = new StringBuilder();
        while ((index = value.indexOf("{}", index)) != -1) {
            arguments.add("arg" + argCount);
            if (argNames.length() != 0) {
                argNames.append(", ");
                sigArgs.append(", ");
            }
            sigArgs.append("Object arg" + argCount);
            argNames.append("arg" + argCount);
            argCount++;
            index++;
        }
        argumentString = argNames.toString();
        signature = String.format("public String %s(%s)", name, sigArgs);
    }

    private void buildBody() {
        if (arguments.isEmpty()) {
            body = String.format("\"%s\"", value);
        } else {
            body = String.format("MessageFormat.format(\"%s\", %s)", value, argumentString);
        }
    }

    public String getSignature() {
        return signature;
    }
}
