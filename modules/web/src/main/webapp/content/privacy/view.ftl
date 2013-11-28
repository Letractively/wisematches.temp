<#-- @ftlvariable name="ordersSummary" type="billiongoods.server.services.payment.OrdersSummary" -->

<#include "/core.ftl"/>

<#macro smartCountLink msg link count>
    <#if (count>0)>
    <a href="${link}">${msg} (${count})</a>
    <#else>
    <span class="sample">${msg} (${count})</span>
    </#if>
</#macro>

<table cellpadding="0" cellspacing="0">
    <tr>
        <td valign="top">
        <@bg.ui.widget "Заказы">
            <table cellpadding="5">
                <tr>
                    <td>
                        <@smartCountLink "Ожидающие оплату" "/privacy/orders?=" ordersSummary.getOrdersCount(OrderState.NEW) + ordersSummary.getOrdersCount(OrderState.BILLING)/>
                    </td>
                    <td>
                        <@smartCountLink "Приостановленные заказы" "/privacy/orders?=" ordersSummary.getOrdersCount(OrderState.SUSPENDED)/>
                    </td>
                    <td>
                        <@smartCountLink "Все заказы" "/privacy/orders?=" ordersSummary.totalCount/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <@smartCountLink "Ожидающие отправку" "/privacy/orders?=" ordersSummary.getOrdersCount(OrderState.PROCESSING) + ordersSummary.getOrdersCount(OrderState.SHIPPING)/>
                    </td>
                    <td colspan="2">
                    </td>
                </tr>
                <tr>
                    <td>
                        <@smartCountLink "Неподтвержденная доставка" "/privacy/orders?=" ordersSummary.getOrdersCount(OrderState.SHIPPED)/>
                    </td>
                    <td colspan="2">
                    </td>
                </tr>
            </table>
        </@bg.ui.widget>
        </td>

        <td valign="top" width="200px" style="padding-left: 5px">
        <@bg.ui.widget "Уведомления">
            <center>
                Уведомлений нет
            </center>
        </@bg.ui.widget>
        </td>
    </tr>
</table>

<#--

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
</table>-->
