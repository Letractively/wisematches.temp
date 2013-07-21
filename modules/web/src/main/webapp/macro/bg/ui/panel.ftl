<#macro round id="" class="" style="">
    <@abstract id="${id}" class="ui-corner-all ${class}" style="${style}">
        <#nested>
    </@abstract>
</#macro >

<#macro roundTop id="" class="" style="">
    <@abstract id="${id}" class="ui-corner-top ${class}" style="${style}">
        <#nested>
    </@abstract>
</#macro >

<#macro roundBottom id="" class="" style="">
    <@abstract id="${id}" class="ui-corner-bottom ${class}" style="${style}">
        <#nested>
    </@abstract>
</#macro >

<#macro abstract class style id="">
<div <#if id?has_content>id="${id}"</#if> class="ui-widget-content ${class!}"
     <#if style?has_content>style="${style}"</#if>>
    <#nested>
</div>
</#macro>