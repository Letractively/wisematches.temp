<#-- @ftlvariable name="context.date" type="java.util.Date" -->
<#-- @ftlvariable name="context.summary" type="billiongoods.server.services.price.ValidationSummary" -->

<table>
    <tr>
        <td>Запущено:</td>
        <td>${context.summary.startDate?string}</td>
    </tr>
    <tr>
        <td>Завершено:</td>
        <td>${context.summary.finishDate?string}</td>
    </tr>
    <tr>
        <td>Проверено:</td>
        <td>${context.summary.validatedArticles}</td>
    </tr>
    <tr>
        <td>Обновлено:</td>
        <td>${context.summary.priceRenewals?size}</td>
    </tr>
    <tr>
        <td>Ошибок проверки:</td>
        <td>${context.summary.priceBreakdowns?size}</td>
    </tr>
</table>

<#if context.summary.priceRenewals?has_content>
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

        <#list context.summary.priceRenewals as r>
            <tr>
                <td>
                    <a href="http://www.billiongoods.ru/warehouse/article/${r.articleId}">${messageSource.getArticleCode(r.articleId)}</a>
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

<#if context.summary.priceBreakdowns?has_content>
<div>
    Ошибки проверки:
    <table>
        <#list context.summary.priceBreakdowns as b>
            <tr>
                <td nowrap="nowrap">
                    <a href="http://www.billiongoods.ru/warehouse/article/${b.articleId}">${messageSource.getArticleCode(b.articleId)}</a>
                </td>
                <td width="100%">
                ${b.exception.message!""}
                </td>
            </tr>
        </#list>
    </table>
</div>
</#if>