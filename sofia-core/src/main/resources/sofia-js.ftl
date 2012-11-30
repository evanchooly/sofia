sofia = {
    <#list methods as method>
    ${method.getMethodName()}: function(<#list method.arguments as argument>${argument}<#if argument_has_next>, </#if></#list>) {
        return "${properties[method.key]}";
    }<#if method_has_next>,</#if>
    </#list>
};