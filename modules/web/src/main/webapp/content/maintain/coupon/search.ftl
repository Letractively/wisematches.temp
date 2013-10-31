<#-- @ftlvariable name="coupons" type="billiongoods.server.services.coupon.Coupon[]" -->

<#include "/core.ftl">

<div style="padding: 10px; border: 1px solid gray;">
    <form action="/maintain/coupon/search" method="get">
        <table cellpadding="3">
            <tr>
                <td>
                    <label for="code">Код:</label>
                </td>
                <td>
                <@bg.ui.input "form.code"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="amount">Продукт/Категория:</label>
                </td>
                <td>
                    <div class="layout-line">
                    <@bg.ui.input "form.reference"/>

                    <@bg.ui.field "form.referenceType">
                        <select id="referenceType" name="referenceType">
                            <option value="PRODUCT">Продукт</option>
                            <option value="CATEGORY">Категория</option>
                        </select>
                    </@bg.ui.field>
                    </div>
                </td>
            </tr>
            <tr>
                <td>

                </td>
                <td>
                    <button type="submit">Искать</button>
                </td>
            </tr>
        </table>
    </form>
</div>

<#if coupons??>
<div style="padding: 10px; margin-top: 10px; border: 1px solid gray;">
    <#if coupons?has_content>
        <table cellpadding="3">
            <#list coupons as c>
                <tr>
                    <td valign="top" nowrap="nowrap">
                        <a href="/maintain/coupon/view?code=${c.code}">${c.code}</a>
                    </td>
                    <td valign="top" nowrap="nowrap">
                    ${c.code}
                    </td>
                    <td>
                        <@bg.ui.coupon c/>
                    </td>
                </tr>
            </#list>
        </table>
    <#else>
        По вашему запросу нет ни одного купона
    </#if>
</div>
</#if>