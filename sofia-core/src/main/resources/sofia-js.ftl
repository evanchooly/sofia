var sofiaBundles = [];
<#list bundles?keys as locale>
    <#assign bundle = bundles[locale]>
    sofiaBundles['${locale}'] = {
    <#list bundle?keys as key>
        <#assign value = bundle[key]?replace("'", "\\'")>
        <#assign label = key>
        <#if key?starts_with("@")>
            <#assign label = key?substring(key?index_of('.')+1)>
        </#if>
        '${label}' : '${value}'<#if key_has_next>,</#if>
    </#list>
    };
</#list>

var sofiaLang = navigator.language || navigator.userLanguage;
sofia = {
    format: function(value, arguments) {
        var formatted = value;
        if (arguments) {
            for (var arg in arguments) {
                formatted = formatted.replace("{" + arg + "}", arguments[arg]);
            }
        }
        return formatted;
    },
    <#list methods as method>
    ${method.getMethodName()}: function(<#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>) {
        return format(sofiaBundles[sofiaLang]['${method.key}']);
    }<#if method_has_next>,</#if>
    </#list>
};