<#-- @ftlvariable name="context.date" type="java.util.Date" -->
<#-- @ftlvariable name="context.checked" type="java.lang.Integer" -->
<#-- @ftlvariable name="context.renewals" type="billiongoods.server.services.price.PriceRenewal[]" -->

<div>Проверено {context.checked} элементов.</div>

<#if context.renewals?has_content>
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

        <#list context.renewals as r>
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
<#else>
<div>Обновлений нет.</div>
</#if>