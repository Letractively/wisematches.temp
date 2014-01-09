<#-- @ftlvariable name="coupon" type="billiongoods.server.services.coupon.Coupon" -->

<#include "/core.ftl">

<div style="padding: 10px; border: 1px solid gray;" xmlns="http://www.w3.org/1999/html">
    <strong>Описание купона:</strong>
    <hr>

    <table cellpadding="3">
        <tr>
            <td>
                Код:
            </td>
            <td>
            ${coupon.code}
            </td>
        </tr>
        <tr>
            <td>
                Состояние:
            </td>
            <td>
            <#if coupon.active>Активен<#else>Не активен</#if>
            </td>
        </tr>
        <tr>
            <td>
                Сумма:
            </td>
            <td>
            ${coupon.amount}
            </td>
        </tr>
        <tr>
            <td>
                Вид скидки:
            </td>
            <td>
            ${coupon.amountType}
            </td>
        </tr>
        <tr>
            <td>
                Действует на:
            </td>
            <td>
            <#if coupon.referenceType.product>
                Продукт <a
                    href="/warehouse/product/${coupon.reference}">${messageSource.getProductCode(coupon.reference)}</a>
            <#elseif coupon.referenceType.category>
                <#assign category=catalog.getCategory(coupon.reference)/>
                Категорию <@bg.link.category category>#${category.id} ${category.name}</@bg.link.category>
            <#elseif coupon.referenceType.everything>
                Все товары
            <#else>
                Незвестный тип ${coupon.referenceType}
            </#if>
            </td>
        </tr>
        <tr>
            <td>
                Действует до:
            </td>
            <td>
            <#if coupon.termination??>${messageSource.formatDate(coupon.termination, locale)}
                (${messageSource.formatRemainedTime(coupon.termination, locale)})<#else>Бессрочный</#if>
            </td>
        </tr>
        <tr>
            <td>
                Выделенное количество:
            </td>
            <td>
            ${coupon.allocatedCount}
            </td>
        </tr>
        <tr>
            <td>
                Уже использован раз:
            </td>
            <td>
            ${coupon.utilizedCount}
            </td>
        </tr>
        <tr>
            <td>
                Дата последнего использования:
            </td>
            <td>
            <#if lastUtilization??>
            ${messageSource.formatDateTime(coupon.lastUtilization, locale)}}
            <#else>
                не использовался
            </#if>
            </td>
        </tr>
    </table>
</div>