<#ftl strip_whitespace=true strip_text=true>
<#-- @ftlvariable name="serverDescriptor" type="billiongoods.server.services.ServerDescriptor" -->

<#macro mailto box><a href="mailto:${box}@${serverDescriptor.mailHostName}">${box}
    @${serverDescriptor.mailHostName}</a></#macro>-->

<#macro link href target=""><#local content><#nested></#local><a
        href="${serverDescriptor.webHostName}${href}"<#if target?has_content>
        target="${target}"</#if>><#if content?has_content>${content?string}<#else>${serverDescriptor.webHostName}${href}</#if></a></#macro>
