<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->

<#include "/core.ftl"/>

<#assign level=0/>

<#macro item cat root selected>
<div class="item<#if root> root</#if><#if selected> selected</#if>">
    <span class="image <#if !cat.final> expanded<#else> empty</#if>"></span>
    <@bg.link.category cat/>
</div>
</#macro>

<#macro ct cat>
<div class="item<#if !cat.parent??> root</#if><#if category?? && cat=category> selected</#if>">
    <div class="ct-name">
        <span class="image <#if !cat.final> expanded<#else> empty</#if>"></span>
        <@bg.link.category cat/>
    </div>

    <#assign level=level+1/>
    <#if cat.children?has_content>
        <div class="ct-items">
            <#list cat.children as cat>
                <#--<@item cat=cat root=false selected=(category?? && category=cat)/>-->
                <@ct cat/>
        </#list>
        </div>
    </#if>
    <#assign level=level-1/>
</div>
</#macro>

<div class="catalog">
    <div class="ct-tit">
        Каталог товаров
    </div>

    <div class="ct-plain">
    <#list catalog.rootCategories as cat>
<#--<@item cat=cat root=true selected=(category?? && category=cat)/>-->
<@ct cat/>
</#list>
    </div>
</div>
