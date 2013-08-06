<#-- @ftlvariable name="basket" type="billiongoods.server.services.basket.Basket" -->
<table>
<#list basket.basketItems as i>
    <tr>
        <td>${i.article.name}</td>
        <td>${i.quantity}</td>
        <td>
            <#list i.options as o>
            ${o.attribute.name}=${o.value}
            </#list>
        </td>
    </tr>
</#list>
</table>