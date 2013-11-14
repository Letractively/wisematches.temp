<#-- @ftlvariable name="orders" type="billiongoods.server.services.payment.Order[]" -->

<#include "/core.ftl"/>

<table>
    <tr>
        <th>Номер</th>
        <th>Общая стоимость</th>
        <th>Номер отслеживания</th>
        <th>Состояние</th>
    </tr>
<#list orders as o>
    <tr>
        <td>#${o.id}</td>
        <td>${o.amount + o.shipment.amount - o.discount}</td>
        <td><#if o.tracking && o.internationalTracking?has_content>${o.internationalTracking}</#if></td>
        <td>
            <@message code="order.status.${o.orderState.code}.label"/>
        </td>
    </tr>
</#list>
</table>