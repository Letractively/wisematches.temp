<#include "/core.ftl"/>

<div class="navigation">
<#--
Good for big catalog. Can't be used for small
<#if category??>
    <#include "widget/category.ftl"/>
<#elseif catalog??>
    <#include "widget/catalog.ftl"/>
</#if>

-->
<#if category??>
    <#include "widget/category.ftl"/>
<#elseif catalog??>
    <#include "widget/catalogMixed.ftl"/>
</#if>

<#include "widget/filtering.ftl"/>
</div>
