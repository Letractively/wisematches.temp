<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="staticContentId" type="String" -->

<#include "/core.ftl"/>

<div class="title">
    <a href="/">Домашняя страница</a> >
    <a href="/assistance">Центр Поддержки</a>
<#if staticContentModel??> > <@bg.link.assist staticContentId/></#if>
</div>