<#-- @ftlvariable name="showCategory" type="java.lang.Boolean" -->
<#-- @ftlvariable name="products" type="billiongoods.server.warehouse.ProductPreview[]" -->
<#-- @ftlvariable name="pageableForm" type="billiongoods.server.web.servlet.mvc.warehouse.form.ProductsPageableForm" -->

<#include "/core.ftl">

<#assign ops={"showCategory": showCategory!false}/>
<@bg.ui.productsTable products pageableForm ops/>