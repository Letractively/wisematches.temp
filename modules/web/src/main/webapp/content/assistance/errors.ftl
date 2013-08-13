<#-- @ftlvariable name="errorCode" type="java.lang.String" -->
<#-- @ftlvariable name="errorTemplate" type="java.lang.String" -->
<#-- @ftlvariable name="errorArguments" type="java.lang.Object[]" -->
<#include "/core.ftl">

<#if !errorCode??><#assign errorCode="500"/></#if>

<div class="error-layout">
    <img src="<@bg.ui.static "images/errorPage.png"/>" width="83" height="71" alt="" style="float: left;">

    <h1><@message code="error.${errorCode}.label"/></h1>

<#if errorTemplate??>
    <#include "/content/assistance/errors/${errorTemplate}"/>
<#else>
    <p><@message code="error.${errorCode}.description" args=errorArguments/></p>
</#if>

    <p><@message code="error.footer.sorry"/></p>

    <p><@message code="error.footer.report"/></p>
</div>