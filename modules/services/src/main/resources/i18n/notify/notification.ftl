<#-- @ftlvariable name="template" type="java.lang.String" -->
<#-- @ftlvariable name="recipient" type="billiongoods.server.services.notify.Recipient.Person" -->

<#import "utils.ftl" as util>

<div class="header">
    Здравствуйте, <#if recipient?has_content && recipient.passport?has_content>${recipient.passport.username!""}</#if>
</div>

<div class="notify-message">
<#include "${template?replace('.', '/')}.ftl">
</div>

<div class="footer">
    Спасибо, что вы выбрали наш магазин
<@util.link "/">BillionGoods.ru</@util.link>
</div>