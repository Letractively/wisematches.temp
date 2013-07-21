<#-- @ftlvariable name="title" type="java.lang.String" -->
<#-- @ftlvariable name="titleExtension" type="java.lang.Object" -->

<#-- @ftlvariable name="templateName" type="java.lang.String" -->
<#-- @ftlvariable name="analyticsCode" type="java.lang.String" -->
<#include "/core.ftl">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@message code=title!"title.default"/><#if titleExtension?has_content>${titleExtension}</#if></title>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Content-Language" content="${locale}"/>

    <script type="text/javascript" src="http://code.jquery.com/jquery-2.0.0.min.js"></script>
    <link rel="stylesheet" type="text/css"
          href="http://code.jquery.com/ui/1.10.2/themes/redmond/jquery-ui.min.css"/>
    <script type="text/javascript" src="http://code.jquery.com/ui/1.10.2/jquery-ui.min.js"></script>


    <link rel="stylesheet" type="text/css" href="<@bg.ui.static "css/jquery.dataTables_themeroller-1.9.4.css"/>">
    <script type="text/javascript" src="<@bg.ui.static "js/jquery.dataTables-1.9.4.min.js"/>"></script>

    <link rel="stylesheet" type="text/css" href="<@bg.ui.static "css/jquery.cluetip-1.2.7.css"/>"/>
    <script type="text/javascript" src="<@bg.ui.static "js/jquery.cluetip-1.2.7.min.js"/>"></script>
    <script type="text/javascript" src="<@bg.ui.static "js/jquery.freeow-1.0.2.min.js"/>"></script>
    <script type="text/javascript" src="<@bg.ui.static "js/jquery.blockUI-2.5.4.js"/>"></script>

    <link rel="stylesheet" type="text/css" href="<@bg.ui.static "css/billiongoods-1.0.0.css"/>"/>
    <script type="text/javascript" src="<@bg.ui.static "js/billiongoods-1.0.0.js"/>"></script>

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

    <script type="text/javascript">
        wm.i18n.extend({<#list Language.values() as l>'language.${l.code}': '<@message code="language."+l.code/>',</#list> locale: '${locale}', 'button.close': '<@message code="button.close"/>', 'button.ok': '<@message code="button.ok"/>', 'button.cancel': '<@message code="button.cancel"/>'});
    </script>
</head>
<body>

<div id="billiongoods">
    <div id="mainHeader">
    <#include "header.ftl"/>
    </div>

    <div id="notification-block"></div>

    <div id="mainContent" style="padding: 5px; display: none;">
    <#include "${templateName}"/>
    </div>

    <div id="hainFooter" style="display: none;">
    <#include "footer.ftl"/>
    </div>
</div>
</html>