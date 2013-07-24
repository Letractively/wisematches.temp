<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->

<#if category??>
    <#include "widget/category.ftl"/>
<#else>
    <#include "widget/catalog.ftl"/>
</#if>