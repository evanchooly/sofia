sofia = {
    <#list methods as method>
    ${method.getMethodName()}() {
        return "${properties[method.key]}"
    }<#if method_has_next>,</#if>
    </#list>
};