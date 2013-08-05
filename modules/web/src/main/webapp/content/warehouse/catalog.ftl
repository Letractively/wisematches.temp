<#-- @ftlvariable name="topSelling" type="billiongoods.server.warehouse.ArticleDescription[]" -->
<#-- @ftlvariable name="newArrival" type="billiongoods.server.warehouse.ArticleDescription[]" -->

<#include "/core.ftl">

<#macro group name url items>
<div class="info-panel">
    <div class="tit">
        <div class="name">${name}</div>

        <div class="more"><a href="${url}">Показать еще</a></div>
    </div>

    <div class="cnt table-content">
        <@bg.ui.articlesViewGrid items {"showCategory":true}/>
    </div>
</div>
</#macro>

<div class="catalog-view">
<#if newArrival?has_content>
    <@group "Последние поступления" "/warehouse/arrivals" newArrival/>
</#if>

<#if topSelling?has_content>
    <@group "Лучшие продажи" "/warehouse/topselling" topSelling/>
</#if>
</div>