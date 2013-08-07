<#-- @ftlvariable name="basket" type="billiongoods.server.services.basket.Basket" -->
<table class="basket">
<#if basket?has_content>
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
<#else>
    <tr>
        <td align="center">
            У вас в корзине нет ни одного товара.
        </td>
    </tr>
</#if>
</table>