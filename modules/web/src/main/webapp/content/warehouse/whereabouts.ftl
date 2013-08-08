<#-- @ftlvariable name="section" type="String" -->
<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->

<#include "/core.ftl"/>

<#if category??>
    <@bg.ui.whereabouts>
        <#list category.genealogy.parents as g>
            <@bg.link.categoryLink g/> >
        </#list>
        <@bg.link.categoryLink category/>

        <@bg.security.authorize "admin">
        <div style="display: inline-block; float: right">
            <a href="/maintain/category?id=${category.id}">Редактировать</a>
        </div>
        </@bg.security.authorize>
    </@bg.ui.whereabouts>

    <#if category.description?has_content>
    <div class="description">${category.description}</div>
    </#if>
<#elseif section??>
    <@bg.ui.whereabouts>
        <@message code="title.warehouse.${section}"/>
    </@bg.ui.whereabouts>

<div class="description"> <@message code="title.warehouse.${section}.description"/></div>
</#if>
