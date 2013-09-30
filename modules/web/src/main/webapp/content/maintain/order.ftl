<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->

<#include "/core.ftl"/>

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
        <#else>
            Внутренний номер поставщика/Номер доставки почты Китая/Международный номер доставки:
        </#if>
        <@bg.ui.input "form.value"/>

            Коментарий:
        <@bg.ui.field path="form.commentary">
            <textarea rows="2" style="width: 100%"
                      name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
        </@bg.ui.field>

        <#if order.orderState==OrderState.ACCEPTED || order.orderState==OrderState.SUSPENDED>
            <button type="submit" name="state" value="${OrderState.PROCESSING.name()}">
                Перевести в статус "Обработка"
            </button>
        </#if>
        <#if order.orderState==OrderState.PROCESSING  || order.orderState==OrderState.SUSPENDED>
            <button type="submit" name="state" value="${OrderState.SHIPPING.name()}">
                Перевести в статус "Доставка"
            </button>
        </#if>
        <#if order.orderState==OrderState.SHIPPING || order.orderState==OrderState.SUSPENDED>
            <button type="submit" name="state" value="${OrderState.SHIPPED.name()}">
                Перевести в статус "Отправлено"
            </button>
        </#if>

            <div id="extendedOptions">
                <input id="allowExtendedOperations" type="checkbox">
                <button type="submit" name="state" value="${OrderState.SUSPENDED.name()}" disabled="disabled">
                    Перевести в статус "Приостановлено"
                </button>
                <button type="submit" name="state" value="${OrderState.CANCELLED.name()}" disabled="disabled">
                    Перевести в статус "Отменен"
                </button>
            </div>
        </div>
    </form>
</div>

<script type="text/javascript">
    $("#allowExtendedOperations").change(function () {
        $("#extendedOptions").find("button").prop('disabled', !$(this).prop('checked'));
    });
</script>

<#include "/content/warehouse/order/view.ftl"/>