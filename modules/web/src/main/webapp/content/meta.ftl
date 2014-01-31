<#-- @ftlvariable name="title" type="java.lang.String" -->
<#-- @ftlvariable name="titleExtension" type="java.lang.Object" -->

<#assign libsVersion="4.3.9"/>

<#include "/core.ftl">

<title><@message code=title!"title.default"/><#if titleExtension?has_content>${titleExtension}</#if></title>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta http-equiv="Content-Language" content="${locale}"/>

<link rel="stylesheet" type="text/css" href="http://code.jquery.com/ui/1.10.3/themes/humanity/jquery-ui.css"/>
<script type="text/javascript" src="http://code.jquery.com/jquery-2.0.3.min.js"></script>
<script type="text/javascript" src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js"></script>

<link rel="stylesheet" type="text/css" href="<@bg.ui.static "css/jquery.cluetip-1.2.7.css"/>"/>
<script type="text/javascript" src="<@bg.ui.static "js/jquery.cluetip-1.2.7.min.js"/>"></script>
<script type="text/javascript" src="<@bg.ui.static "js/jquery.freeow-1.0.2.min.js"/>"></script>
<script type="text/javascript" src="<@bg.ui.static "js/jquery.blockUI-2.5.4.js"/>"></script>
<script type="text/javascript" src="<@bg.ui.static "js/jquery.timers-1.2.0.js"/>"></script>
<script type="text/javascript" src="<@bg.ui.static "js/json2-2.1.8.min.js"/>"></script>
<script type="text/javascript" src="<@bg.ui.static "js/jquery.simplemodal.js"/>"></script>

<link rel="stylesheet" type="text/css" href="<@bg.ui.static "css/billiongoods-${libsVersion}.css"/>"/>
<script type="text/javascript" src="<@bg.ui.static "js/billiongoods-${libsVersion}.js"/>"></script>

<#include "metrics.ftl"/>
