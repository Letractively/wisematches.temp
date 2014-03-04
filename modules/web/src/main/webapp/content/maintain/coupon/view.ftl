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
                Описание:
            </td>
            <td>
            <@bg.ui.coupon coupon/>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <hr>
            </td>
        </tr>
        <tr>
            <td>
                Скидка:
            </td>
            <td>
            ${coupon.amount} <@message code="enum.${coupon.amountType.name()?lower_case}.label"/>
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
                Использование:
            </td>
            <td>
            ${coupon.utilizedCount} / ${coupon.allocatedCount}
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
                Дата последнего использования:
            </td>
            <td>
            <#if coupon.lastUtilization??>
            ${messageSource.formatDateTime(coupon.lastUtilization, locale)}
            <#else>
                не использовался
            </#if>
            </td>
        </tr>

    <#if coupon.active>
        <tr>
            <td></td>
            <td>
                <form action="/maintain/coupon/close" method="post">
                    <button name="code" value="${coupon.code}">Закрыть купон</button>
                </form>
            </td>
        </tr>
    </#if>
    </table>
</div>