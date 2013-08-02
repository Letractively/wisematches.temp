<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="staticContentId" type="String" -->

<#include "/core.ftl"/>

<#if category??>
<div class="title">
    <a href="/">Домашняя страница</a> >
    <#list category.genealogy.parents as g>
        <@bg.link.categoryLink g/> >
    </#list>
    <@bg.link.categoryLink category/>
</div>

    <#if category.description??>
    <div class="description">${category.description}</div>
    </#if>
</#if>
