<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="articles" type="billiongoods.server.warehouse.ArticleDescription[]" -->

<#include "/core.ftl">

<div class="articles preview">
<#list articles as a>
<@bg.link.article a>
${a.name}
</@bg.link.article>
    </#list>
</div>
