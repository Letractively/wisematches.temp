<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->

<#include "/core.ftl"/>

<div class="tit">
    <div style="display: inline-block">
        Заказ #${order.id} от ${messageSource.formatDate(order.timeline.created, locale)}
    </div>
    <div style="display: inline-block; float: right">
    <#list order.parcels as p>
        <@bg.tracking.international p.internationalTracking/><#if p_has_next>,</#if>
    </#list>
    </div>
</div>