<#include "/macro/message.ftl"/>

<#macro permitted role><#nested personalityContext.hasRole(role)/></#macro>

<#macro authorized role><@permitted role;allowed><#if allowed><#nested/></#if></@permitted></#macro>

<#macro unauthorized role><@permitted role;allowed><#if !allowed><#nested/></#if></@permitted></#macro>

