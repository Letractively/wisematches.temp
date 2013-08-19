<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->

<#include "/core.ftl"/>

<#if order.orderState==OrderState.ACCEPTED || order.orderState==OrderState.PROCESSING || order.orderState==OrderState.SHIPPING>
<div class="order">
    <form action="/maintain/order/promote" method="post">
        <div class="info">
            <strong>Перевести в состояние</strong>

            <br>

            <#if order.orderState==OrderState.ACCEPTED>
                Внутренний номер поставщика:
            <#elseif order.orderState==OrderState.PROCESSING>
                Номер доставки почты Китая:
            <#elseif order.orderState==OrderState.SHIPPING>
                Международный номер доставки (если есть):
            </#if>
            <input name="id" type="hidden" value="${order.id}">

            <@bg.ui.input "form.value">
                <#if order.orderState==OrderState.ACCEPTED>
                    <button type="submit" name="state" value="${OrderState.PROCESSING.name()}">Обработка</button>
                <#elseif order.orderState==OrderState.PROCESSING>
                    <button type="submit" name="state" value="${OrderState.SHIPPING.name()}">Доставка</button>
                <#elseif order.orderState==OrderState.SHIPPING>
                    <button type="submit" name="state" value="${OrderState.SHIPPED.name()}">Доставлено</button>
                </#if>
            </@bg.ui.input>
        </div>
    </form>
</div>
</#if>

<#include "/content/warehouse/order/view.ftl"/>