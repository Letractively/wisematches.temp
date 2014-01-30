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
        <td><#if context.startDate??>${context.startDate?datetime?string}<#else>Проверка не
            проводилась</#if></td>
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
        <td>${context.updatedProducts?size}</td>
    </tr>
    <tr>
        <td>Без изменений:</td>
        <td>${context.processedProducts - context.updatedProducts?size - context.brokenProducts?size}</td>
    </tr>
    <tr>
        <td>Ошибок проверки:</td>
        <td>${context.brokenProducts?size}</td>
    </tr>
</table>

<br>

<#if context.updatedProducts?has_content>
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

        <#list context.updatedProducts as v>
            <tr>
                <td valign="top">
                    <a href="http://www.billiongoods.ru/warehouse/product/${v.product.id}">${messageSource.getProductCode(v.product.id)}</a>
                </td>
                <#if v.oldPrice.equals(v.newPrice)>
                    <td colspan="3">
                        <@priceInfo v.oldPrice/>
                    </td>
                <#else>
                    <td>
                        <@priceInfo v.oldPrice/>
                    </td>
                    <td>
                        <#if v.newPrice??>
                            <@priceInfo v.newPrice/>
                        <#else>
                            не загрузилась
                        </#if>
                    </td>
                    <td>
                    ${(v.newPrice.amount - v.oldPrice.amount)?string("0.00")}
                        (<#if !v.oldPrice.primordialAmount?? && !v.newPrice.primordialAmount??>
                        -
                    <#elseif !v.oldPrice.primordialAmount??>
                        +${v.newPrice.primordialAmount?string("0.00")}
                    <#elseif !v.newPrice.primordialAmount??>
                    ${v.oldPrice.primordialAmount?string("0.00")}
                    <#else>
                    ${(v.newPrice.primordialAmount - v.oldPrice.primordialAmount)?string("0.00")}
                    </#if>)
                    </td>
                </#if>

                <#if v.oldStockInfo.equals(v.newStockInfo)>
                    <td colspan="2">
                        <@stockInfo v.oldStockInfo/>
                    </td>
                <#else>
                    <td>
                        <@stockInfo v.oldStockInfo/>
                    </td>
                    <td>
                        <#if v.newStockInfo??>
                            <@stockInfo v.newStockInfo/>
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

<br>

<#if context.brokenProducts?has_content>
<div>
    Ошибки при проверки:
    <table>
        <tr>
            <th>Артикул</th>
            <th>Banggood</th>
            <th>Текущая цена</th>
            <th>Цена до скидки</th>
            <th>Наличие</th>
        </tr>

        <#list context.brokenProducts as b>
            <tr>
                <td>
                    <a href="http://www.billiongoods.ru/maintain/product?id=${b.id}">${messageSource.getProductCode(b.id)}</a>
                </td>
                <td>
                    <a href="${b.supplierInfo.referenceUrl.toString()}">${b.supplierInfo.referenceCode}</a>
                </td>
                <td>
                ${b.price.amount?string("0.00")}
                </td>
                <td>
                    <#if b.price.primordialAmount??>${b.price.primordialAmount?string("0.00")}</#if>
                </td>
                <td>
                    <@stockInfo b.stockInfo/>
                </td>
            </tr>
        </#list>
    </table>
</div>
</#if>