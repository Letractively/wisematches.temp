<#-- @ftlvariable name="template" type="java.lang.String" -->
<#-- @ftlvariable name="recipient" type="billiongoods.server.services.notify.Recipient.Person" -->

<#import "utils.ftl" as util>

<style type="text/css">
    .order-details {
        border-collapse: collapse;
    }

    .order-details th, .order-details td {
        padding: 5px;
        border: 1px solid #808080;
    }

    table td, table th {
        vertical-align: top;
    }
</style>

<div class="header">
    Здравствуйте, <#if recipient?has_content && recipient.passport?has_content>${recipient.passport.username!""}</#if>
</div>

<div class="notify-message">
<#include "${template?replace('.', '/')}.ftl">
</div>

<div class="footer">
    <p>
        Спасибо, что вы выбрали наш магазин <@util.link "/">BillionGoods.ru</@util.link>
    </p>

    <p>
        Если у вас возникли какие-либо вопросы по вашему заказу, вы можете связаться с нами по электронной
        почте ответив на это письмо либо отправив новое на андес <@util.mailto "support"/>.
    </p>
</div>