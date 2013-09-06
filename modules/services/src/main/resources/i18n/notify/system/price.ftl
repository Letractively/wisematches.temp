<#-- @ftlvariable name="context" type="billiongoods.server.services.price.ValidationSummary" -->

<table>
    <tr>
        <td>Запущено:</td>
        <td><#if context.startDate??>${context.startDate?datetime?string}<#else>Проверка не проводилась</#if></td>
    </tr>
    <tr>
        <td>Завершено:</td>
        <td><#if context.finishDate??>${context.finishDate?datetime?string}<#else>В процессе</#if></td>
    </tr>
    <tr>
        <td>Проверено:</td>
        <td>${context.validatedProducts}</td>
    </tr>
    <tr>
        <td>Обновлено:</td>
        <td>${context.priceRenewals?size}</td>
    </tr>
    <tr>
        <td>Ошибок проверки:</td>
        <td>${context.priceBreakdowns?size}</td>
    </tr>
</table>

<#if context.priceRenewals?has_content>
<div>
    Обновленные товары:
    <table>
        <tr>
            <th>Артикул</th>
            <th>Старая цена</th>
            <th>Новая цена</th>
            <th>Изменение</th>
            <th>Старая до скидки</th>
            <th>Новая до скидки</th>
            <th>Изменение</th>
        </tr>

        <#list context.priceRenewals as r>
            <tr>
                <td>
                    <a href="http://www.billiongoods.ru/warehouse/product/${r.productId}">${messageSource.getProductCode(r.productId)}</a>
                </td>
                <td>
                ${r.oldPrice.amount?string("0.00")}
                </td>
                <td>
                ${r.newPrice.amount?string("0.00")}
                </td>
                <td>
                ${(r.newPrice.amount - r.oldPrice.amount)?string("0.00")}
                </td>
                <td>
                    <#if r.oldPrice.primordialAmount??>
                    ${r.oldPrice.primordialAmount?string("0.00")}
                    <#else>
                        -
                    </#if>
                </td>
                <td>
                    <#if r.newPrice.primordialAmount??>
                    ${r.newPrice.primordialAmount?string("0.00")}
                    <#else>
                        -
                    </#if>
                </td>
                <td>
                    <#if !r.oldPrice.primordialAmount?? && !r.newPrice.primordialAmount??>
                        -
                    <#elseif !r.oldPrice.primordialAmount??>
                        +${r.newPrice.primordialAmount?string("0.00")}
                    <#elseif !r.newPrice.primordialAmount??>
                    ${r.oldPrice.primordialAmount?string("0.00")}
                    <#else>
                    ${(r.newPrice.primordialAmount - r.oldPrice.primordialAmount)?string("0.00")}
                    </#if>
                </td>
            </tr>
        </#list>
    </table>
</div>
</#if>

<#if context.priceBreakdowns?has_content>
<div>
    Ошибки проверки:
    <table>
        <#list context.priceBreakdowns as b>
            <tr>
                <td nowrap="nowrap">
                    <a href="http://www.billiongoods.ru/warehouse/product/${b.productId}">${messageSource.getProductCode(b.productId)}</a>
                </td>
                <td width="100%">
                ${b.exception.message!""}
                </td>
            </tr>
        </#list>
    </table>
</div>
</#if>