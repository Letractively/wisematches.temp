<#-- @ftlvariable name="context.sender" type="billiongoods.core.Personality" -->
<#-- @ftlvariable name="context.message" type="billiongoods.server.services.message.impl.HibernateMessage" -->
<#import "../../utils.ftl" as notify>

<#if context.message.notification>
<p>${context.message.text}</p>
<#else>
<p>
    You have got new message <#if context.sender??>from user <@notify.player context.sender/></#if>:
</p>
<blockquote>${context.message.text}</blockquote>
</#if>
<br>
<p>Please note that you can have more than one new message</p>