<#-- @ftlvariable name="active" type="boolean" -->
<#-- @ftlvariable name="summary" type="billiongoods.server.services.price.ValidationSummary" -->

<form action="/maintain/service/validatePrices" method="post">
<#if active>
    Проверка в процессе:
    <button name="action" value="stop" type="submit">Остановить проверку</button>
<#else>
    Проверка не выполняется:
    <button name="action" value="start" type="submit">Запустить проверку</button>
</#if>
</form>

<div style="padding-top: 10px">
    Последняя проверка:
    <table>
        <tr>
            <td>Запущено:</td>
            <td><#if summary.startDate??>${summary.startDate?datetime?string}<#else>Проверка не проводилась</#if></td>
        </tr>
        <tr>
            <td>Завершено:</td>
            <td><#if summary.finishDate??>${summary.finishDate?datetime?string}<#else>В процессе</#if></td>
        </tr>
        <tr>
            <td>Проверено:</td>
            <td>${summary.validatedProducts}</td>
        </tr>
        <tr>
            <td>Обновлено:</td>
            <td>${summary.priceRenewals?size}</td>
        </tr>
        <tr>
            <td>Ошибок проверки:</td>
            <td>${summary.priceBreakdowns?size}</td>
        </tr>
    </table>

<#if summary.priceRenewals?has_content>
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

            <#list summary.priceRenewals as r>
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

<#if summary.priceBreakdowns?has_content>
    <div>
        Ошибки проверки:
        <table>
            <#list summary.priceBreakdowns as b>
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
</div>