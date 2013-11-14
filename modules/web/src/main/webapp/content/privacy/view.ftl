<#-- @ftlvariable name="ordersSummary" type="billiongoods.server.services.payment.OrdersSummary" -->

<#include "/core.ftl"/>

<#assign states=[OrderState.ACCEPTED, OrderState.SUSPENDED, OrderState.PROCESSING, OrderState.SHIPPING, OrderState.SHIPPED, OrderState.CLOSED]/>
<table>
<#list states as s>
    <tr>
        <td>
            <a href="/privacy/order?state=${s.name()}">
                <@message code="order.status.${s.name()?lower_case}.label"/>
            </a>
        </td>
        <td style="padding-left: 5px">
            <a href="/privacy/order?state=${s.name()}">
            ${ordersSummary.getOrdersCount(s)}
            </a>
        </td>
    </tr>
</#list>
</table>