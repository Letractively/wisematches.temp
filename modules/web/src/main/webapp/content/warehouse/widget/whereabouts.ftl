<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#include "/core.ftl"/>

<#if category??>
<div class="whereabouts">
    <div class="title">
        <a href="/">Домашня страница</a> >
        <#list category.genealogy.parents as g>
            <@bg.link.categoryLink g/>
            <#if g_has_next> > </#if>
        </#list>

        <#if category.description??>
            <div class="description">${category.description}</div>
        </#if>
    </div>
</#if>
