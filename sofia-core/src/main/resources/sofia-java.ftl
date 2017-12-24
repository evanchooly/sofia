package ${packageName};
import java.util.*;
import java.text.*;
<#list loggingType.imports as import>
import ${import};
</#list>

public class ${className} {
    private static final Logger logger = LoggerFactory.getLogger("${packageName}.${className}");
    private static final Map<String, Localized> IMPLS = new HashMap<>();

    static {
        IMPLS.put("", new LocalizedImpl());
        <#list bundles?keys as bundle>
        IMPLS.put("${bundle}", new LocalizedImpl_${bundle}());
        </#list>
    }

    private static Localized get(Locale... locale) {
        if(locale.length == 0) {
            return IMPLS.get("");
        }
        String name = locale[0].toString();
        while (!name.isEmpty()) {
            Localized localized = IMPLS.get(name);
            if(localized != null) {
                return localized;
            } else {
                name = name.contains("_") ? name.substring(0, name.lastIndexOf('_')) : "";
            }
        }
        return IMPLS.get("");
    }

    <#list methods as method>
    public static String ${method.getMethodName()}(<#list method.parameters as argument>${argument.first} ${argument.second}<#if argument_has_next>, </#if></#list><#if method.parameters?size != 0>, </#if>Locale... locale) {
    <#if method.arguments?size != 0>
        return get(locale).${method.getMethodName()}(<#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>);
    <#else>
        return get(locale).${method.getMethodName()}();
    </#if>
    }

    <#if method.logged>
    public static void ${method.getLoggerName()}(<#list method.parameters as argument>${argument.first} ${argument.second}<#if argument_has_next>, </#if></#list><#if method.parameters?size != 0>, </#if>Locale... locale) {
    <#if "${loggingType.name()}" == "SLF4J">
        if(logger.is${method.logLevel?cap_first}Enabled()) {
    <#elseif "${loggingType.name()}" == "JUL">
        if(logger.isLoggable(Level.${method.logLevel?upper_case})) {
    </#if>
           <#if method.arguments?size != 0>
            logger.${method.logLevel}(${method.getMethodName()}(<#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>, locale));
            <#else>
            logger.${method.logLevel}(${method.getMethodName()}(locale));
            </#if>
        }
    }
    </#if>
    </#list>

    interface Localized {
    <#list methods as method>
        /**
         * Generated from ${method.key}
         */
        default String ${method.getMethodName()}(<#list method.parameters as argument>${argument.first} ${argument.second}<#if argument_has_next>, </#if></#list><#if method.parameters?size != 0>, </#if>Locale... locale) {
        <#assign value = method.value?replace("\"", "\\\"") >
        <#if method.arguments?size != 0>
           return MessageFormat.format("${value}", <#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>);
        <#else>
           return "${value}";
        </#if>
        }

        <#if method.logged>
        /**
         * Generated from ${method.key}
         */
        default void ${method.getLoggerName()}(<#list method.parameters as argument>${argument.first} ${argument.second}<#if argument_has_next>, </#if></#list><#if method.parameters?size != 0>, </#if>Locale... locale) {
        <#if "${loggingType.name()}" == "SLF4J">
           if(${className}.logger.is${method.logLevel?cap_first}Enabled()) {
        <#elseif "${loggingType.name()}" == "JUL">
           if(${className}.logger.isLoggable(Level.${method.logLevel?upper_case})) {
        </#if>
              <#if method.arguments?size != 0>
               ${className}.logger.${method.logLevel}(${method.getMethodName()}(<#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>, locale));
               <#else>
               ${className}.logger.${method.logLevel}(${method.getMethodName()}(locale));
               </#if>
           }
        }

        </#if>
    </#list>
    }

    <#macro impl methods genLogged>
       <#list methods as method>
        public String ${method.getMethodName()}(<#list method.parameters as argument>${argument.first} ${argument.second}<#if argument_has_next>, </#if></#list><#if method.parameters?size != 0>, </#if>Locale... locale) {
        <#assign value = method.value?replace("\"", "\\\"") >
        <#if method.arguments?size != 0>
            return MessageFormat.format("${value}", <#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>);
        <#else>
            return "${value}";
        </#if>
        }

        <#if genLogged && method.logged>
        public void ${method.getLoggerName()}(<#list method.parameters as argument>${argument}<#if argument_has_next>, </#if></#list><#if method.parameters?size != 0>, </#if>Locale... locale) {
        <#if "${loggingType.name()}" == "SLF4J">
            if(${className}.logger.is${method.logLevel?cap_first}Enabled()) {
        <#elseif "${loggingType.name()}" == "JUL">
            if(${className}.logger.isLoggable(Level.${method.logLevel?upper_case})) {
        </#if>
               <#if method.arguments?size != 0>
                ${className}.logger.${method.logLevel}(${method.getMethodName()}(<#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>, locale));
                <#else>
                ${className}.logger.${method.logLevel}(${method.getMethodName()}(locale));
                </#if>
            }
        }
        </#if>
        </#list>
    </#macro>

    private static class LocalizedImpl implements Localized {
    }

    <#list bundles?keys as bundle>
    private static class LocalizedImpl_${bundle} extends LocalizedImpl${findParentExtension(bundle)} {
    <@impl methods=bundles[bundle] genLogged=false />
    }

    </#list>
}
