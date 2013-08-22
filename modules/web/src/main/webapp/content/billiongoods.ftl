<#-- @ftlvariable name="title" type="java.lang.String" -->
<#-- @ftlvariable name="titleExtension" type="java.lang.Object" -->

<#-- @ftlvariable name="templateName" type="java.lang.String" -->
<#-- @ftlvariable name="analyticsCode" type="java.lang.String" -->

<#-- @ftlvariable name="hideWarehouse" type="java.lang.Boolean" -->
<#-- @ftlvariable name="hideNavigation" type="java.lang.Boolean" -->
<#include "/core.ftl">

<#assign libsVersion="1.3.0"/>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@message code=title!"title.default"/><#if titleExtension?has_content>${titleExtension}</#if></title>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Content-Language" content="${locale}"/>

    <link rel="stylesheet" type="text/css" href="http://code.jquery.com/ui/1.10.3/themes/humanity/jquery-ui.css"/>
    <script type="text/javascript" src="http://code.jquery.com/jquery-2.0.3.min.js"></script>

    <link rel="stylesheet" type="text/css" href="<@bg.ui.static "css/jquery.cluetip-1.2.7.css"/>"/>
    <script type="text/javascript" src="<@bg.ui.static "js/jquery.cluetip-1.2.7.min.js"/>"></script>
    <script type="text/javascript" src="<@bg.ui.static "js/jquery.freeow-1.0.2.min.js"/>"></script>
    <script type="text/javascript" src="<@bg.ui.static "js/jquery.blockUI-2.5.4.js"/>"></script>
    <script type="text/javascript" src="<@bg.ui.static "js/jquery.timers-1.2.0.js"/>"></script>
    <script type="text/javascript" src="<@bg.ui.static "js/json2-2.1.8.min.js"/>"></script>

    <link rel="stylesheet" type="text/css" href="<@bg.ui.static "css/billiongoods-${libsVersion}.css"/>"/>
    <script type="text/javascript" src="<@bg.ui.static "js/billiongoods-${libsVersion}.js"/>"></script>

<@bg.security.unauthorized "moderator">
    <script type="text/javascript">
        var _gaq = _gaq || [];
        _gaq.push(['_setAccount', '${analyticsCode!""}']);
        _gaq.push(['_trackPageview']);

        (function () {
            var ga = document.createElement('script');
            ga.type = 'text/javascript';
            ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0];
            s.parentNode.insertBefore(ga, s);
        })();
    </script>
</@bg.security.unauthorized>
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
                            <#if !hideWarehouse?? || !hideWarehouse>
                                <div class="whereabouts"><#include "${department.style}/whereabouts.ftl"/></div>
                            </#if>
                            <#include "${templateName}"/>
                            </div>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>

        <tr>
            <td class="layout-container footer-layout">
                <div class="layout-content footer-content">
                <#include "footer.ftl"/>
                </div>
            </td>
        </tr>
    </table>
</div>
</html>