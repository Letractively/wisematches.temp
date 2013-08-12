<#-- @ftlvariable name="code" type="java.lang.String" -->
<#-- @ftlvariable name="errorArguments" type="java.lang.Object[]" -->
<#-- @ftlvariable name="errorException" type="java.lang.Exception" -->
<#include "/core.ftl">

<#if !code??><#assign code="500"/></#if>

<div class="error-layout">
    <img src="<@bg.ui.static "images/errorPage.png"/>" width="83" height="71" alt="" style="float: left;">

    <h1><@message code="error.${code}.label"/></h1>

    <p><@message code="error.${code}.description" args=errorArguments/></p>

    <p><@message code="error.footer.sorry"/></p>

    <p><@message code="error.footer.report"/></p>
</div>