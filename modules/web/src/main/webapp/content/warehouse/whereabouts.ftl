<#-- @ftlvariable name="section" type="String" -->
<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->

<#include "/core.ftl"/>

<#if category??>
<div class="title">
    <a href="/">Домашняя страница</a> >
    <#list category.genealogy.parents as g>
        <@bg.link.category g/> >
    </#list>
    <@bg.link.category category/>

    <@bg.security.authorized "moderator">
        <div style="display: inline-block; float: right; margin-top: -3px">
            <button type="button" onclick="bg.util.url.redirect('/maintain/category?id=${category.id}')">
                Редактировать
            </button>
        </div>
    </@bg.security.authorized>
</div>

    <#if category.description?has_content && "product"!=section>
    <div class="description">${category.description}</div>
    </#if>
<#elseif section??>
    <@bg.ui.whereabouts false/>
<div class="description"> <@message code="title.warehouse.${section}.description"/></div>
</#if>
