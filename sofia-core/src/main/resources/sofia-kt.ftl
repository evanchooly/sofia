package ${packageName};
import java.util.*;
import java.text.*;
${loggingType.imports}

object ${className?capitalize} {
    private val logger = LoggerFactory.getLogger(${className?capitalize}::class.qualifiedName);

    private val IMPLS = HashMap<String, Localized>()

    init {
        IMPLS.put("", LocalizedImpl());
        <#list bundles?keys as bundle>
        IMPLS.put("${bundle}", LocalizedImpl_${bundle}());
        </#list>
    }

    private fun get(vararg locale: Locale): Localized {
        if(locale.size == 0) {
            return IMPLS.get("")!!;
        }
        var name = locale[0].toString();
        while (!name.isEmpty()) {
            val localized = IMPLS.get(name);
            if(localized != null) {
                return localized;
            } else {
                name = if (name.contains("_")) name.substring(0, name.lastIndexOf('_')) else "";
            }
        }
        return IMPLS.get("")!!;
    }

    <#list methods as method>
    fun ${method.getMethodName()}(<#list method.getParameters("kotlin") as parameter>${parameter.second}: ${parameter.first}<#if parameter_has_next>, </#if></#list><#if method.parameters?size != 0>, </#if>vararg locale: Locale): String {
    <#if method.arguments?size != 0>
        return get(*locale).${method.getMethodName()}(<#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>);
    <#else>
        return get(*locale).${method.getMethodName()}();
    </#if>
    }

    <#if method.logged>
    fun ${method.getLoggerName()}(<#list method.getParameters("kotlin") as parameter>${parameter.second}: ${parameter.first}<#if parameter_has_next>, </#if></#list><#if method.parameters?size != 0>, </#if>vararg locale: Locale) {
    <#if "${loggingType.name()}" == "SLF4J">
        if(logger.is${method.logLevel?cap_first}Enabled()) {
    <#elseif "${loggingType.name()}" == "JUL">
        if(logger.isLoggable(Level.${method.logLevel?upper_case})) {
    </#if>
           <#if method.arguments?size != 0>
            logger.${method.logLevel}(${method.getMethodName()}(<#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>, *locale));
            <#else>
            logger.${method.logLevel}(${method.getMethodName()}(*locale));
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
        fun ${method.getMethodName()}(<#list method.getParameters("kotlin") as argument>${argument.second}: ${argument.first}<#if argument_has_next>, </#if></#list><#if method
        .parameters?size != 0>, </#if>vararg locale: Locale): String {
        <#if method.arguments?size != 0>
           return MessageFormat.format("${method.value}", <#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>);
        <#else>
           return "${method.value}";
        </#if>
        }

        <#if method.logged>
        /**
         * Generated from ${method.key}
         */
        fun ${method.getLoggerName()}(<#list method.getParameters("kotlin") as parameter>${parameter.second}: ${parameter.first}<#if parameter_has_next>, </#if></#list><#if method.parameters?size != 0>, </#if>vararg locale: Locale) {
        <#if "${loggingType.name()}" == "SLF4J">
           if(${className?capitalize}.logger.is${method.logLevel?cap_first}Enabled()) {
        <#elseif "${loggingType.name()}" == "JUL">
           if(${className?capitalize}.logger.isLoggable(Level.${method.logLevel?upper_case})) {
        </#if>
              <#if method.arguments?size != 0>
               ${className?capitalize}.logger.${method.logLevel}(${method.getMethodName()}(<#list method.arguments as
               argument>${argument}<#if argument_has_next>, </#if></#list>, *locale));
               <#else>
               ${className?capitalize}.logger.${method.logLevel}(${method.getMethodName()}(*locale));
               </#if>
           }
        }

        </#if>
    </#list>
    }

    <#macro impl methods genLogged>
       <#list methods as method>
        <#if !genLogged>override </#if>fun ${method.getMethodName()}(<#list method.getParameters("kotlin") as parameter>${parameter.second}: ${parameter.first}<#if parameter_has_next>, </#if></#list><#if method .parameters?size != 0>, </#if>vararg locale: Locale): String {
        <#if method.arguments?size != 0>
            return MessageFormat.format("${method.value}", <#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>);
        <#else>
            return "${method.value}";
        </#if>
        }

        <#if genLogged && method.logged>
        fun ${method.getLoggerName()}(<#list method.getParameters("kotlin") as parameter>${parameter.second}: ${parameter.first}<#if parameter_has_next>, </#if></#list><#if method.parameters?size != 0>, </#if>Locale... locale) {
        <#if "${loggingType.name()}" == "SLF4J">
            if(${className?capitalize}.logger.is${method.logLevel?cap_first}Enabled()) {
        <#elseif "${loggingType.name()}" == "JUL">
            if(${className?capitalize}.logger.isLoggable(Level.${method.logLevel?upper_case})) {
        </#if>
               <#if method.arguments?size != 0>
                ${className?capitalize}.logger.${method.logLevel}(${method.getMethodName()}(<#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>, locale));
                <#else>
                ${className?capitalize}.logger.${method.logLevel}(${method.getMethodName()}(locale));
                </#if>
            }
        }
        </#if>
        </#list>
    </#macro>

    private open class LocalizedImpl: Localized {
    }

    <#list bundles?keys as bundle>
    private open class LocalizedImpl_${bundle}: LocalizedImpl${findParentExtension(bundle)}() {
    <@impl methods=bundles[bundle] genLogged=false />
    }

    </#list>
}
