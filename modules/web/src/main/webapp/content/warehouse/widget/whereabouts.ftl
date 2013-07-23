<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#include "/core.ftl"/>

<#if category??>
<div class="whereabouts">
    <div class="title">
        <a href="/">Домашня страница</a> >
        <#list category.genealogy.parents as g>
            <@bg.link.categoryLink g/> >
        </#list>
        <@bg.link.categoryLink category/>

        <#if category.description??>
            <div class="description">${category.description} ${category.description} ${category.description} ${category.description} ${category.description}</div>
        </#if>
    </div>
</#if>
