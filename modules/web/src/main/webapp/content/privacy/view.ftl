<#-- @ftlvariable name="ordersSummary" type="billiongoods.server.services.payment.OrdersSummary" -->

<#include "/core.ftl"/>

<#macro smartCountLink union>
    <#if union==OrderStateUnion.ALL>
        <#assign count=ordersSummary.totalCount/>
    <#else>
        <#assign count=ordersSummary.getOrdersCount(union.orderStates)/>
    </#if>
    <#if (count>0)>
    <a href="/privacy/orders?state=${union.code}"><@message code="privacy.orders.${union.code}.label"/> (${count})</a>
    <#else>
    <span class="sample"><@message code="privacy.orders.${union.code}.label"/> (${count})</span>
    </#if>
</#macro>

<table cellpadding="0" cellspacing="0">
    <tr>
        <td valign="top">
        <@bg.ui.widget "Заказы">
            <table cellpadding="5">
                <tr>
                    <td>
                        <@smartCountLink OrderStateUnion.PROCESSING/>
                    </td>
                    <td>
                        <@smartCountLink OrderStateUnion.SUSPENDED/>
                    </td>
                    <td>
                        <@smartCountLink OrderStateUnion.ALL/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <@smartCountLink OrderStateUnion.DELIVERING/>
                    </td>
                    <td colspan="2">
                    </td>
                </tr>
                <tr>
                    <td>
                        <@smartCountLink OrderStateUnion.BILLING/>
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
