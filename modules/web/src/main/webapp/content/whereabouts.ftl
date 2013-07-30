<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="staticContentId" type="String" -->

<#include "/core.ftl"/>

<#if !department.undefined>
    <#if department.warehouse>
        <#if category??>
        <div class="whereabouts">
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
        </div>
        </#if>
    <#elseif department.assistance>
    <div class="whereabouts">
        <div class="title">
            <a href="/">Домашняя страница</a> >
            <a href="/assistance">Центр Поддержки</a>
            <#if staticContentModel??> > <@bg.link.assist staticContentId/></#if>
        </div>
    </div>
    </#if>
</#if>