<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#include "/core.ftl"/>

<#assign level=0/>

<#macro item cat root selected expanded>
<div class="item <#if root>root</#if> <#if selected>selected</#if>" style="padding-left: ${level}">
    <#if !root>
        <span class="image<#if expanded> expanded</#if><#if cat.final> empty</#if>"></span></#if>
    <@bg.link.categoryLink cat/>
</div>
</#macro>

<div class="navigation category">
    <div class="container">
        <div class="item selected root">
            Выбранный раздел
        </div>

    <#assign genealogy=category.genealogy/>
    <#list genealogy.parents as p>
        <@item p p_index==0 false true/>
        <#assign level=level + 10/>
    </#list>

    <#assign items=category.children/>
    <#if !items.empty>
        <@item category genealogy.generation==0 true true/>
        <#assign level=level + 10/>
    <#else>
        <#assign items=category.parent.children/>
    </#if>

    <#list items as c>
        <@item c false category==c false/>
    </#list>
    </div>
</div>