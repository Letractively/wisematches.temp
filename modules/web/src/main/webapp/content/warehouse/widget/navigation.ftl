<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->

<#if category??>
    <#include "navigation/category.ftl"/>
<#else>
    <#include "navigation/catalog.ftl"/>
</#if>