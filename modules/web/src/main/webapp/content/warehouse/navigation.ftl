<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->

<#if category??>
    <#include "widget/category.ftl"/>
<#elseif catalog??>
    <#include "widget/catalog.ftl"/>
</#if>
