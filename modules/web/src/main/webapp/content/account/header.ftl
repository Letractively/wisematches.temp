<#-- @ftlvariable name="title" type="java.lang.String" -->
<#-- @ftlvariable name="requestQueryString" type="java.lang.String" -->
<#include "/core.ftl">

<#macro languageUrl code>
    <#if requestQueryString?? && requestQueryString?has_content>
        <#assign requestQueryString=requestQueryString?replace("language=\\w+\\&?", "", "ri")/>
    </#if>

    <#if requestQueryString?? && requestQueryString?has_content>
    ?${requestQueryString}<#if !requestQueryString?ends_with("&")>&</#if>language=${code}
    <#else>
    ?language=${code}
    </#if>
</#macro>

<table style="width: 100%; height: 70px">
    <tr>
        <td id="header-welcome-label" style="text-align: left; vertical-align: bottom">
            <span class="info-header"><span class="info-label"><@message code=title!"title.default"/></span></span>
        </td>
    <#--</tr>-->
    <#--<tr>-->
        <td style="text-align: right; vertical-align: bottom">
            <a rel="alternate" href="<@languageUrl code="ru"/>"><img src="<@wm.ui.static "images/flags/ru.png"/>"
                                                                     alt=""/> Русский</a>
            &nbsp;
            <a rel="alternate" href="<@languageUrl code="en"/>"><img src="<@wm.ui.static "images/flags/en.png"/>"
                                                                     alt=""/> English</a>
        </td>
    </tr>
</table>
