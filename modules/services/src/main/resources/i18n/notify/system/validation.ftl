<#-- @ftlvariable name="context" type="billiongoods.server.services.validator.ValidationSummary" -->

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
        <td>${context.validProducts}</td>
    </tr>
    <tr>
        <td>Ошибок проверки:</td>
        <td>${context.brokenProducts}</td>
    </tr>
</table>

<#if context.productValidations?has_content>
<div>
    Обновленные товары:
    <table>
        <tr>
            <th>Артикул</th>
            <th>Старая цена (до скидки)</th>
            <th>Новая цена (до скидки)</th>
            <th>Изменение (до скидки)</th>
            <th>Старое наличие</th>
            <th>Новое наличие</th>
        </tr>

        <#list context.productValidations as r>
            <tr>
                <td>
                    <a href="http://www.billiongoods.ru/warehouse/product/${r.productId}">${messageSource.getProductCode(r.productId)}</a>
                </td>
                <#if r.errorMessage?has_content>
                    <td colspan="5">${r.errorMessage}</td>
                <#else>
                    <td>
                    ${r.oldPrice.amount?string("0.00")}
                        (<#if r.oldPrice.primordialAmount??>${r.oldPrice.primordialAmount?string("0.00")}<#else>-</#if>)
                    </td>
                    <td>
                    ${r.newPrice.amount?string("0.00")}
                        (<#if r.newPrice.primordialAmount??>${r.newPrice.primordialAmount?string("0.00")}<#else>-</#if>)
                    </td>
                    <td>
                    ${(r.newPrice.amount - r.oldPrice.amount)?string("0.00")}
                        (<#if !r.oldPrice.primordialAmount?? && !r.newPrice.primordialAmount??>
                        -
                    <#elseif !r.oldPrice.primordialAmount??>
                        +${r.newPrice.primordialAmount?string("0.00")}
                    <#elseif !r.newPrice.primordialAmount??>
                    ${r.oldPrice.primordialAmount?string("0.00")}
                    <#else>
                    ${(r.newPrice.primordialAmount - r.oldPrice.primordialAmount)?string("0.00")}
                    </#if>)
                    </td>
                </#if>
            </tr>
        </#list>
    </table>
</div>
</#if>