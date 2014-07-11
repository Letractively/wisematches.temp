<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->

<#include "/core.ftl"/>

<div class="tit">
    <div style="display: inline-block">
        Заказ #${order.id} от ${messageSource.formatDate(order.created, locale)}
    </div>
    <div style="display: inline-block; float: right">
    <@bg.tracking.international order.internationalTracking/>
    </div>
</div>