<#-- @ftlvariable name="context" type="billiongoods.server.services.payment.Order" -->
<p>
    Получен новый заказ #${context.id} от ${context.payment.payerName!context.payment.payer}.
</p>

<table>
    <tr>
        <th><strong>Артикул</strong></th>
        <th><strong>Наименование</strong></th>
        <th><strong>Цена</strong></th>
        <th><strong>Количество</strong></th>
        <th><strong>Стоимость</strong></th>
    </tr>
<#list context.items as i>
    <#assign product=i.product/>
    <tr>
        <td>${messageSource.getProductCode(product)}</td>
        <td>${product.name}</td>
        <td>${i.amount?string("0.00")}</td>
        <td>${i.quantity}</td>
        <td>${(i.amount*i.quantity)?string("0.00")}</td>
    </tr>
</#list>
    <tr>
        <td align="right" colspan="3">
            <strong>Стоимость доставки: ${context.shipment.amount?string("0.00")}</strong>
        </td>
    </tr>
<#if (context.discount.amount>0)>
    <tr>
        <td align="right" colspan="3">
            <strong>Скидка по купону: ${context.discount.amount?string("0.00")}</strong>
        </td>
    </tr>
</#if>
    <tr>
        <td align="right" colspan="3">
            <strong>Итоговая стоимость: ${(context.grandTotal)?string("0.00")}</strong>
        </td>
    </tr>
</table>

