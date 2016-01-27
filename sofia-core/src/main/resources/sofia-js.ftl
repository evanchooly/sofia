var sofiaBundles = [];
<#list bundles?keys as locale>
    <#assign bundle = bundles[locale]>
    sofiaBundles['${locale}'] = {
    <#list bundles?keys as bundle>
        <#list bundles[bundle] as method>
        <#assign value = method.value>
        <#assign label = method.getMethodName()?replace("'", "\\'")>
        <#if label?starts_with("@")>
            <#assign label = label?substring(bundle?index_of('.')+1)>
        </#if>
        '${label}' : '${value}'<#if bundle_has_next>,</#if>
        </#list>
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