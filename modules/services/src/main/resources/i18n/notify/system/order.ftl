<#-- @ftlvariable name="context" type="billiongoods.server.services.payment.Order" -->
<p>
    Получен новый заказ #${context.id} от ${context.payerName!context.payer}.
</p>

<table>
    <tr>
        <th><strong>Артикул</strong></th>
        <th><strong>Наименование</strong></th>
        <th><strong>Количество</strong></th>
        <th><strong>Стоимость</strong></th>
    </tr>
<#list context.orderItems as i>
    <#assign product=i.product/>
    <tr>
        <td>${messageSource.getProductCode(product)}</td>
        <td>${product.name}</td>
        <td>${i.quantity}</td>
        <td>${(i.amount*i.quantity)?string("0.00")}</td>
    </tr>
</#list>
    <tr>
        <td align="right" colspan="2">
            <strong>Стоимость доставки: ${context.shipment.amount?string("0.00")}</strong>
        </td>
    </tr>
<#if (context.discount>0)>
    <tr>
        <td align="right" colspan="2">
            <strong>Скидка по купону: ${context.discount?string("0.00")}</strong>
        </td>
    </tr>
</#if>
    <tr>
        <td align="right" colspan="2">
            <strong>Итоговая
                стоимость: ${(context.amount + context.shipment.amount - context.discount)?string("0.00")}</strong>
        </td>
    </tr>
</table>

