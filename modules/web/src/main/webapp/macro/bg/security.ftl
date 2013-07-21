<#include "/macro/message.ftl"/>

<#macro observed nbsp=false>
    <#if !restriction??><#nested><#elseif nbsp>&nbsp;</#if>
</#macro>

<#macro authorize granted><#if springSecurityContext.hasRole(granted)><#nested/></#if></#macro>