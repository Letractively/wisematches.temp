<#-- @ftlvariable name="errorCode" type="java.lang.String" -->
<#-- @ftlvariable name="errorArguments" type="java.lang.Object[]" -->
<#-- @ftlvariable name="errorException" type="java.lang.Exception" -->
<#include "/core.ftl">

<div class="error-layout">
    <img src="<@wm.ui.static "images/errorPage.png"/>" width="83" height="71" alt="" style="float: left;">

    <h1><@message code="error.${errorCode}.label"/></h1>

    <p><@message code="error.${errorCode}.description" args=errorArguments/></p>

    <p><@message code="error.footer.sorry"/></p>

    <p><@message code="error.footer.report"/></p>
</div>