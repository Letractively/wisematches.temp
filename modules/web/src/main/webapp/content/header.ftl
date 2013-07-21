<#include "/core.ftl">

This is header

<table id="header" width="100%" cellpadding="0" cellspacing="0" class="ui-widget-content shadow"
       style="background: none; border: 0; padding: 0">
    <tr>
        <td width="170px" valign="top">
            <img src="<@bg.ui.static "images/logo/logo170x70x2.png"/>" width="170px" height="70px"/>
        </td>
        <td style="padding-right: 5px">
        <#if principal??><#include "playground/header.ftl"/><#else><#include "account/header.ftl"/></#if>
        </td>
    </tr>
</table>
