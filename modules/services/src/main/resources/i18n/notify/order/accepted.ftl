<#-- @ftlvariable name="OrderState" type="billiongoods.server.services.payment.OrderState.static" -->
<#-- @ftlvariable name="context" type="billiongoods.server.services.payment.Order" -->

<#import "../utils.ftl" as util>

<p>
    Благодарим вас за ваш заказ на BillionGoods.ru. Как только ваш заказ будет отправлен, мы вышлем вам письмо с номером
    отслеживания.
</p>

<p>
    Детали вашего заказа:<br>
    Ваш заказ <b>#${context.id}</b> был оформлен ${messageSource.formatDate(context.timeline.created, locale)}<br>
    Ожидаемая дата отправки вашего заказа ${messageSource.getExceptedDeliveryDate(context.items, locale)}
</p>

<#include "address.ftl"/>

<p>
    Информация о товарах:
<table border="0" class="order-details">
    <tr>
        <th>Товар</th>
        <th>Код</th>
        <th>Стоимость</th>
        <th>Количество</th>
        <th>Подитог</th>
    </tr>
<#list context.items as i>
    <tr>
        <td>${i.product.name}</td>
        <td>${messageSource.getProductCode(i.product)}</td>
        <td>${i.amount?string("0.00")}руб</td>
        <td>${i.quantity?string}</td>
        <td>${(i.amount*i.quantity)?string("0.00")}руб</td>
    </tr>
</#list>
    <tr>
        <td colspan="4" align="center">Стоимость доставки:</td>
        <td>${context.shipment.amount?string("0.00")}</td>
    </tr>
<#if context.discount.coupon??>
    <tr>
        <td colspan="4" align="center">Скидка по купону ${context.discount.coupon}:</td>
        <td>${context.discount.amount?string("0.00")}</td>
    </tr>
</#if>
    <tr>
        <td colspan="4" align="center"><strong>Итоговая стоимость заказа:</strong></td>
        <td>${context.grandTotal?string("0.00")}</td>
    </tr>
</table>
</p>

<p>
    Вы можете проверить состояние вашего заказа используя <@util.link "/account/signin">свой личный кабинет</@util.link>
    либо воспользовавшись сервисом <@util.link "/warehouse/order/status">отслеживания заказов</@util.link>.
</p>