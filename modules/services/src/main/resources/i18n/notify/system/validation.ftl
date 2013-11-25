<#-- @ftlvariable name="context" type="billiongoods.server.services.validator.ValidationSummary" -->

<#macro stockInfo info>
${info.stockState.name()}
    <#if info.restockDate??>
    (${messageSource.formatDate(info.restockDate, locale)})
    <#elseif info.leftovers??>
    (остаток ${info.leftovers})
    </#if>
</#macro>

<#macro priceInfo price>
${price.amount?string("0.00")} (<#if price.primordialAmount??>${price.primordialAmount?string("0.00")}<#else>-</#if>)
</#macro>

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
        <td>${context.processedProducts} из ${context.totalCount}</td>
    </tr>
    <tr>
        <td>Обновлено:</td>
        <td>${context.updateProducts}</td>
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
                <td valign="top">
                    <a href="http://www.billiongoods.ru/warehouse/product/${r.productId}">${messageSource.getProductCode(r.productId)}</a>
                </td>
                <#if r.oldPrice.equals(r.newPrice)>
                    <td colspan="3">
                        <@priceInfo r.oldPrice/>
                    </td>
                <#else>
                    <td>
                        <@priceInfo r.oldPrice/>
                    </td>
                    <td>
                        <#if r.newPrice??>
                            <@priceInfo r.newPrice/>
                        <#else>
                            не загрузилась
                        </#if>
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

                <#if r.oldStockInfo.equals(r.newStockInfo)>
                    <td colspan="2">
                        <@stockInfo r.oldStockInfo/>
                    </td>
                <#else>
                    <td>
                        <@stockInfo r.oldStockInfo/>
                    </td>
                    <td>
                        <#if r.newStockInfo??>
                            <@stockInfo r.newStockInfo/>
                        <#else>
                            не загрузилась
                        </#if>
                    </td>
                </#if>
            </tr>
        </#list>
    </table>
</div>
</#if>