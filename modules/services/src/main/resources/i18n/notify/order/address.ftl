<#-- @ftlvariable name="context" type="billiongoods.server.services.payment.Order" -->

<p>
    Информация о заказе:
<#assign payment=context.payment/>
<table cellpadding="0">
    <tr>
        <td><strong>E-Mail адрес:</strong></td>
        <td valign="top">${payment.payer}</td>
    </tr>
    <tr>
        <td valign="top"><strong>Адрес доставки:</strong></td>
        <td>
        <#assign address=context.shipment.address/>
        ${address.fullName}
            <br>
        ${address.postcode}, ${address.region}, ${address.city}
            <br>
        ${address.location}
        </td>
    </tr>
    <tr>
        <td><strong>Стоимость доставки:</strong></td>
        <td>${context.shipment.amount?string("0.00")}</td>
    </tr>
<#if context.discount.coupon??>
    <tr>
        <td><strong>Скидка по купону ${context.discount.coupon}:</strong></td>
        <td>${context.discount.amount?string("0.00")}</td>
    </tr>
</#if>
    <tr>
        <td><strong>Общая стоимость заказа:</strong></td>
        <td>${context.grandTotal}руб.</td>
    </tr>
</table>
</p>