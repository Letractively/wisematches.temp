<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->

<#include "/core.ftl"/>

<#if order.orderState==OrderState.ACCEPTED || order.orderState==OrderState.PROCESSING || order.orderState==OrderState.SHIPPING>
<div class="order">
    <form action="/maintain/order/promote" method="post">
        <input name="id" type="hidden" value="${order.id}">

        <div class="info">
            <#if order.orderState==OrderState.ACCEPTED>
                Внутренний номер поставщика:
            <#elseif order.orderState==OrderState.PROCESSING>
                Номер доставки почты Китая:
            <#elseif order.orderState==OrderState.SHIPPING>
                Международный номер доставки (если есть):
            </#if>

            <@bg.ui.input "form.value"/>

            Коментарий:
            <@bg.ui.field path="form.commentary">
                <textarea rows="2" style="width: 100%"
                          name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
            </@bg.ui.field>

            <#if order.orderState==OrderState.ACCEPTED>
                <button type="submit" name="state" value="${OrderState.PROCESSING.name()}">Перевести в статус
                    "Обработка"
                </button>
            <#elseif order.orderState==OrderState.PROCESSING>
                <button type="submit" name="state" value="${OrderState.SHIPPING.name()}">Перевести в статус "Доставка"
                </button>
            <#elseif order.orderState==OrderState.SHIPPING>
                <button type="submit" name="state" value="${OrderState.SHIPPED.name()}">Перевести в статус
                    "Доставлено"
                </button>
            </#if>
        </div>
    </form>
</div>
</#if>

<#include "/content/warehouse/order/view.ftl"/>