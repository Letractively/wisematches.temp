<#-- @ftlvariable name="templateName" type="java.lang.String" -->

<#-- @ftlvariable name="hideWhereabouts" type="java.lang.Boolean" -->
<#-- @ftlvariable name="hideNavigation" type="java.lang.Boolean" -->
<#include "/core.ftl">


<html>
<head>
<#include "meta.ftl"/>
</head>

<body>
<div id="billiongoods" class="department-${department.style} <#if section??>section-${section}</#if>">
    <table width="100%">
        <tr>
            <td class="layout-container header-layout">
                <div class="layout-content header-content">
                <#include "header.ftl"/>
                </div>
            </td>
        </tr>

        <tr>
            <td class="layout-container toolbar-layout">
                <div class="layout-content toolbar-content">
                <#include "toolbar.ftl"/>
                </div>
            </td>
        </tr>

        <tr>
            <td class="layout-container content-layout">
                <table cellspacing="0" cellpadding="0" class="layout-content">
                    <tr>
                    <#if !hideNavigation?? || !hideNavigation>
                        <td width="210px" valign="top">
                            <#include "${department.style}/navigation.ftl"/>
                        </td>
                    </#if>
                        <td valign="top">
                            <div class="content">
                            <#if !hideWhereabouts?? || !hideWhereabouts>
                                <div class="whereabouts"><#include "${department.style}/whereabouts.ftl"/></div>
                            </#if>
                                <div class="${department.style}">
                                <#include "${templateName}"/>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>

        <tr>
            <td class="layout-container footer-layout">
            <#include "footer.ftl"/>
            </td>
        </tr>
    </table>
</div>
</html>