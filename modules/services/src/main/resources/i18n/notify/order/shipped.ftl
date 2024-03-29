<#-- @ftlvariable name="OrderState" type="billiongoods.server.services.payment.OrderState.static" -->
<#-- @ftlvariable name="context" type="billiongoods.server.services.payment.Order" -->

<style type="text/css">
    .order-details {
        border-collapse: collapse;
    }

    .order-details th, .order-details td {
        padding: 5px;
        border: 1px solid #808080;
    }
</style>

<#import "../utils.ftl" as util>

<p>
    Благодарим вас за ваш заказ на BillionGoods.ru. Ваш заказ был отправлен.
</p>

<p>
    Детали вашего заказа:<br>
    Номер заказа: <b>#${context.id}</b><br>
    Дата оформления: ${messageSource.formatDate(context.timeline.created, locale)}<br>
    Дата отправки: ${messageSource.formatDate(context.timeline.shipped, locale)}
</p>

<#include "address.ftl"/>

<p>
    Ваш заказ был отправлен в ${context.parcels?size} <#if context.parcels?size==1>посылке<#else>посылках</#if>.
</p>

<#list context.parcels as parcel>
<p>
<table border="0" class="order-details">
    <tr>
        <td colspan="2" style="border: none; padding: 0" nowrap="nowrap" align="left">
            Посылка #${context.id}:${parcel.id}
        </td>
        <td colspan="3" style="border: none; padding: 0" nowrap="nowrap" align="right">
            <#if parcel.internationalTracking?has_content>номер отслеживания
                <strong>${parcel.internationalTracking}</strong></#if>
        </td>
    </tr>
    <tr>
        <th>Товар</th>
        <th>Код</th>
        <th>Стоимость</th>
        <th>Количество</th>
        <th>Подитог</th>
    </tr>
    <#list context.getItems(parcel) as i>
        <tr>
            <td>${i.product.name}</td>
            <td>${messageSource.getProductCode(i.product)}</td>
            <td>${i.amount?string("0.00")}руб</td>
            <td>${i.quantity?string}</td>
            <td>${(i.amount*i.quantity)?string("0.00")}руб</td>
        </tr>
    </#list>
</table>
</p>
</#list>

<p>
    Обращаем ваше внимание, что посылки начинают отслеживаться только по прошествие нескольких дней.
</p>

<p>
    Вы можете проверить состояние вашего заказа используя <@util.link "/account/signin">свой личный кабинет</@util.link>
    либо воспользовавшись сервисом <@util.link "/warehouse/order/status">отслеживания заказов</@util.link>.
</p>