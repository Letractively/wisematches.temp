<#-- @ftlvariable name="products" type="billiongoods.server.warehouse.ProductPreview[]" -->

<#if products??>
    <#list products as p>
        <#if p??>
        ${p}
        </#if>
    </#list>
<#else>
No wish items
</#if>