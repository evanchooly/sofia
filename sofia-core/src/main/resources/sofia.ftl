package ${packageName};

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.security.*;
import java.util.ResourceBundle.Control;

${imports}

public class ${className?capitalize} {
    private static Map<Locale, ResourceBundle> messages = new HashMap<>();
    <#if "${logger}" == "SLF4J">
        private static final Logger logger = LoggerFactory.getLogger(${className?capitalize}.class);
    <#elseif "${logger}" == "JUL">
        private static Logger logger = Logger.getLogger(${className?capitalize}.class.getName());
    </#if>

    private ${className?capitalize}() {}

    private static ResourceBundle getBundle(Locale... localeList) {
        Locale locale = localeList.length == 0 ? Locale.getDefault() : localeList[0];
        ResourceBundle labels = loadBundle(locale);
        if(labels == null) {
            labels = loadBundle(Locale.ROOT);
        }
        return labels;
    }

    private static ResourceBundle loadBundle(Locale locale) {
        ResourceBundle bundle = messages.get(locale);
        if(bundle == null) {
            bundle = ResourceBundle.getBundle("${bundleName}", locale <#if useControl>, new SofiaControl() </#if>);
            messages.put(locale, bundle);
        }
        return bundle;
    }

    private static String getMessageValue(String key, Locale... locale) {
        return (String) getBundle(locale).getObject(key);
    }

    <#list methods as method>
    public static String ${method.getMethodName()}(<#list method.parameters as argument>${argument}<#if argument_has_next>, </#if></#list><#if method.parameters?size != 0>, </#if>Locale... locale) {
    <#if method.arguments?size != 0>
        return MessageFormat.format(getMessageValue("${method.key}", locale), <#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>);
    <#else>
        return getMessageValue("${method.key}", locale);
    </#if>
    }

    <#if method.logged>
    public static void ${method.getLoggerName()}(<#list method.parameters as argument>${argument}<#if argument_has_next>, </#if></#list><#if method.parameters?size != 0>, </#if>Locale... locale) {
    <#if "${logger}" == "SLF4J">
        if(logger.is${method.logLevel?cap_first}Enabled()) {
    <#elseif "${logger}" == "JUL">
        if(logger.isLoggable(Level.${method.logLevel?upper_case})) {
    </#if>
           <#if method.arguments?size != 0>
            logger.${method.logLevel}(${method.getMethodName()}(<#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>));
            <#else>
            logger.${method.logLevel}(${method.getMethodName()}());
            </#if>
        }
    }
    </#if>
    </#list>

    <#if useControl>
    private static class SofiaControl extends Control {
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, final ClassLoader loader,
        boolean reload) throws IllegalAccessException, InstantiationException, IOException {

        ResourceBundle bundle = null;
        String name = toBundleName(baseName, locale);
        if(name.contains("_")) {
            name = name.replaceFirst("_", ".");
        }
        InputStream stream;
        try {
            final String bundleName = name;
            stream = AccessController.doPrivileged(
                new PrivilegedExceptionAction<InputStream>() {
                    public InputStream run() throws IOException {
                        return loader.getResourceAsStream(bundleName);
                    }
                });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
        if (stream != null) {
            try {
                bundle = new PropertyResourceBundle(stream);
            } finally {
                stream.close();
            }
        }

        return bundle;
      }
    }
    </#if>
}