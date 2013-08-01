<#-- @ftlvariable name="imageResourcesDomain" type="java.lang.String" -->
<#-- @ftlvariable name="staticResourcesDomain" type="java.lang.String" -->
<#setting locale="en">

<#include "/macro/message.ftl"/>

<#import "/spring.ftl" as spring />
<#import "ui/table.ftl" as table />
<#import "ui/panel.ftl" as panel />

<#macro static p>${staticResourcesDomain}/${p}</#macro>

<#macro image article code type>${imageResourcesDomain}/${imageResolver.resolveURI(article, code, type)?replace("\\", "/")}</#macro>

<#macro usd v>US$${v?string("0.00")}</#macro>

<#macro rub v>${v?string("0.00")}<img src="<@static "images/gruble.gif"/>" class="ruble-img"/><span
        class="dot">руб.</span></#macro>

<#macro price v><span class="us"><@usd v/></span>
<span class="rub">(~<@rub priceConverter.convertPrice(v)/>)</span></#macro>

<#macro bind path>
    <@spring.bind path/>

    <#assign status=spring.status>
    <#assign statusValue=spring.stringStatusValue>
</#macro>

<#macro field path id="" class="">
    <@bind path/>

<div <#if id?has_content>id="${id}"</#if>
     class="<#if spring.status.error>field-error<#else>field-ok</#if><#if class?has_content> ${class}</#if>">

    <#nested >

    <#list status.errorMessages as msg>
        <div class="ui-state-error-text error-msg">${msg}</div>
    </#list>
</div>
</#macro>

<#macro input path attributes="" fieldType="text" size=30 value="">
    <@field path=path>

    <input type="${fieldType}" id="${status.expression}" name="${status.expression}" size="${size}"
        <#if fieldType=='checkbox'><#if status.value?has_content && status.value=='true'>checked="checked"</#if>
           value="true"<#else>
           value="<#if fieldType!="password"><#if status.value?has_content>${status.value}<#else><@message code=value/></#if></#if>"</#if> ${attributes}/>
        <#nested>
    </@field>
</#macro>

<#--
<#macro widget title id="" class="" style="" help="" hidden=false>
<div class="ui-widget<#if class?has_content> ${class}</#if> <#if hidden>ui-helper-hidden</#if>"
     <#if style?has_content>style="${style}"</#if>>
    <div class="ui-widget-header ui-corner-all shadow">
        <#if help?has_content>
            <div class="quickInfo ajax">
                <a class="ui-icon ui-icon-info" href="#" onclick="return false"
                   rel="/assistance/tip.ajax?s=${help}"></a>
            </div>
        </#if>
        <@message code=title/>
    </div>
    <div <#if id?has_content>id="${id}"</#if> class="ui-widget-content ui-corner-all shadow">
        <#nested/>
    </div>
</div>
</#macro>

<#macro editor id code value="" view="" classes="">
    <#assign qwe=view/>
    <#if (!view?has_content) && (value?has_content)><#assign qwe=value/></#if>
<div id="${id}" class="ui-editor-item ${classes}">
    <div class="ui-editor-label ${classes}"><@message code="${code}.label"/></div>
    <div label="<@message code="${code}.description"/>"
         class=" ui-editor-view<#if !qwe?has_content> sample</#if> ${classes}
    "><#if qwe?has_content>${qwe}<#else><@message code="${code}.description"/></#if></div>
    <input name="${id}" type="hidden" value="${value}">
</div>
</#macro>

<#macro info>
<div class="help-tooltip ui-icon ui-icon-info" title="<#nested>"></div>
</#macro>-->
