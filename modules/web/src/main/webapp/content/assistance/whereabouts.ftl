<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="section" type="String" -->

<#include "/core.ftl"/>

<@bg.ui.whereabouts>
<a href="/assistance">Центр Поддержки</a>
    <#if section??> > <@bg.link.assist section/></#if>
</@bg.ui.whereabouts>