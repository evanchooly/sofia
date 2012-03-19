package ${packageName};

import java.text.*;
import java.util.*;

${imports}

public class Localizer {
    private static Map<Locale, ResourceBundle> messages = new HashMap<Locale, ResourceBundle>();
    <#if "${logger}" == "SLF4J">
        // SLF4J
        private static final Logger logger = LoggerFactory.getLogger(Localizer.class);
    </#if>

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
            bundle = ResourceBundle.getBundle("${bundleName}", locale);
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
        if(logger.is${method.logLevel?cap_first}Enabled()) {
            <#if method.arguments?size != 0>
            logger.${method.logLevel}(${method.getMethodName()}(<#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>));
            <#else>
            logger.${method.logLevel}(${method.getMethodName()}());
            </#if>
        }
    }
    </#if>
    </#list>
}