<#-- @ftlvariable name="active" type="boolean" -->
<#-- @ftlvariable name="summary" type="billiongoods.server.services.validator.ValidationSummary" -->

<style type="text/css">
    table.info th {
        vertical-align: bottom;
        font-weight: bold;
        border: 1px solid #808080;
        border-collapse: collapse;
    }

    table.info td {
        vertical-align: top;
        border-bottom: 1px dotted #d3d3d3;
        border-collapse: collapse;
    }
</style>

<form action="/maintain/service/validation" method="post">
<#if active>
    Проверка в процессе:
    <button name="action" value="stop" type="submit">Остановить проверку</button>
<#else>
    Проверка не выполняется:
    <button name="action" value="start" type="submit">Запустить проверку</button>
    <#if ((summary.brokenProducts?size)>0)>
        <button name="action" value="broken" type="submit">Проверить ошибочные</button>
    </#if>
    <div style="width: 60px; display: inline-block">&nbsp;</div>
    <button name="action" value="exchange" type="submit">Обновить цены по курсу</button>
</#if>
</form>

<#macro stockInfo info>
${info.stockState.name()} / ${info.shipDays} (${info.count})
    <#if info.arrivalDate??>
    <br>
    (${messageSource.formatDate(info.arrivalDate, locale)})
    </#if>
</#macro>

<#macro priceInfo price>
${price.amount?string("0.00")} (<#if price.primordialAmount??>${price.primordialAmount?string("0.00")}<#else>-</#if>)
</#macro>

<#assign context=summary/>
<table>
    <tr>
        <td>Запущено:</td>
        <td><#if context.startDate??>${context.startDate?datetime?string}<#else>Проверка не проводилась</#if></td>
    </tr>
    <tr>
        <td>Завершено:</td>
        <td><#if context.finishDate??>${context.finishDate?datetime?string}<#else>В процессе
            (итерация ${context.iteration})</#if></td>
    </tr>
    <tr>
        <td>Проверено:</td>
        <td>${context.processedProducts} из ${context.totalCount} (итерация ${context.iteration})</td>
    </tr>
    <tr>
        <td>Ошибок:</td>
        <td>${context.brokenProducts?size}</td>
    </tr>
    <tr>
        <td>Потеряно:</td>
        <td>${context.lostProducts?size}</td>
    </tr>
    <tr>
        <td>Обновлено:</td>
        <td>${context.updatedProducts?size}</td>
    </tr>
    <tr>
        <td>Без изменений:</td>
        <td>${context.processedProducts - context.updatedProducts?size - context.brokenProducts?size - context.lostProducts?size}</td>
    </tr>
</table>

<br><br>

<#if context.lostProducts?has_content>
<div>
    Удаленные продукты:
    <table class="info">
        <tr>
            <th>Артикул</th>
            <th>Наименование</th>
            <th>Banggood</th>
            <th>Текущая цена</th>
            <th>Цена до скидки</th>
        </tr>

        <#list context.lostProducts as b>
            <tr>
                <td nowrap="nowrap">
                    <a href="http://www.billiongoods.ru/maintain/product?id=${b.id}">${messageSource.getProductCode(b.id)}</a>
                </td>
                <td>
                ${b.name}
                </td>
                <td nowrap="nowrap">
                    <a href="${b.supplierInfo.referenceUrl.toString()}">${b.supplierInfo.referenceCode}</a>
                </td>
                <td nowrap="nowrap">
                ${b.price.amount?string("0.00")}
                </td>
                <td nowrap="nowrap">
                    <#if b.price.primordialAmount??>${b.price.primordialAmount?string("0.00")}</#if>
                </td>
            </tr>
        </#list>
    </table>
</div>
</#if>

<br>

<#if context.brokenProducts?has_content>
<div>
    Ошибки при проверки:
    <table class="info">
        <tr>
            <th>Артикул</th>
            <th>наименование</th>
            <th>Banggood</th>
            <th>Текущая цена</th>
            <th>Цена до скидки</th>
            <th>Наличие</th>
        </tr>

        <#list context.brokenProducts as b>
            <tr>
                <td nowrap="nowrap">
                    <a href="http://www.billiongoods.ru/maintain/product?id=${b.id}">${messageSource.getProductCode(b.id)}</a>
                </td>
                <td>
                ${b.name}
                </td>
                <td nowrap="nowrap">
                    <a href="${b.supplierInfo.referenceUrl.toString()}">${b.supplierInfo.referenceCode}</a>
                </td>
                <td nowrap="nowrap">
                ${b.price.amount?string("0.00")}
                </td>
                <td nowrap="nowrap">
                    <#if b.price.primordialAmount??>${b.price.primordialAmount?string("0.00")}</#if>
                </td>
                <td nowrap="nowrap">
                    <@stockInfo b.stockInfo/>
                </td>
            </tr>
        </#list>
    </table>
</div>
</#if>

<br>

<#if context.updatedProducts?has_content>
<div>
    Обновленные товары:
    <table class="info">
        <tr>
            <th rowspan="2">Артикул</th>
            <th colspan="3">Цена (до скидки)</th>
            <th colspan="2">Наличие</th>
        </tr>

        <tr>
            <th>Старая</th>
            <th>Новая</th>
            <th>Изменение</th>
            <th>Старое</th>
            <th>Новое</th>
        </tr>

        <#list context.updatedProducts as r>
            <tr>
                <td valign="top">
                    <a href="http://www.billiongoods.ru/warehouse/product/${r.product.id}">${messageSource.getProductCode(r.product.id)}</a>
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
