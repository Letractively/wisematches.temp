<#include "/core.ftl"/>

<div class="navigation">
<#if category??>
    <#include "widget/category.ftl"/>
<#elseif catalog??>
    <#include "widget/catalogMixed.ftl"/>
</#if>

<#include "widget/filtering.ftl"/>

<#include "widget/recomendations.ftl"/>
</div>
