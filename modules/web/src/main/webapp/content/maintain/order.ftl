<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->

<#include "/core.ftl"/>

<#if order.orderState!=OrderState.CLOSED>
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
            <#elseif order.orderState==OrderState.SHIPPED>
                Дата вручения (yyyy.MM.dd):
            <#else>
                Внутренний номер поставщика/Номер доставки почты Китая/Международный номер доставки:
            </#if>
            <@bg.ui.input path="form.value" fieldType="text"/>

            Коментарий:
            <@bg.ui.field path="form.commentary">
                <textarea rows="2" style="width: 100%"
                          name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
            </@bg.ui.field>

            <#if order.orderState==OrderState.ACCEPTED || order.orderState==OrderState.SUSPENDED>
                <button type="submit" name="state" value="${OrderState.PROCESSING.name()}">
                    Перевести в статус "Обработка" (PROCESSING)
                </button>
            </#if>
            <#if order.orderState==OrderState.PROCESSING || order.orderState==OrderState.SUSPENDED>
                <button type="submit" name="state" value="${OrderState.SHIPPING.name()}">
                    Перевести в статус "Доставка" (SHIPPING)
                </button>
            </#if>
            <#if order.orderState==OrderState.PROCESSING || order.orderState==OrderState.SHIPPING || order.orderState==OrderState.SUSPENDED>
                <button type="submit" name="state" value="${OrderState.SHIPPED.name()}">
                    Перевести в статус "Отправлено" (SHIPPED)
                </button>
            </#if>
            <#if order.orderState==OrderState.SHIPPED>
                <button type="submit" name="state" value="${OrderState.CLOSED.name()}">
                    Перевести в статус "Завершено" (CLOSED)
                </button>
            </#if>

            <#if order.orderState==OrderState.PROCESSING || order.orderState == OrderState.ACCEPTED>
                <div id="extendedOptions">
                    <input id="allowExtendedOperations" type="checkbox">
                    <button type="submit" name="state" value="${OrderState.SUSPENDED.name()}" disabled="disabled">
                        Перевести в статус "Приостановлено" (SUSPENDED)
                    </button>
                    <button type="submit" name="state" value="${OrderState.CANCELLED.name()}" disabled="disabled">
                        Перевести в статус "Отменен" (CANCELLED)
                    </button>
                </div>
            </#if>
        </div>
    </form>
</div>

<script type="text/javascript">
    $("#allowExtendedOperations").change(function () {
        $("#extendedOptions").find("button").prop('disabled', !$(this).prop('checked'));
    });

        <#if order.orderState==OrderState.SHIPPED>
        $("#value").datepicker({ "dateFormat": "yy.mm.dd"});
        </#if>
</script>
</#if>


<#include "/content/warehouse/order/view.ftl"/>