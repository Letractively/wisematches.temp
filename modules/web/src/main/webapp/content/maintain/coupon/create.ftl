<#include "/core.ftl">

<div style="padding: 10px; border: 1px solid gray;">
    <form action="/maintain/coupon/create" method="post">
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
                    <label for="amount">Сумма и тип:</label>
                </td>
                <td>
                    <div class="layout-line">
                    <@bg.ui.input "form.amount"/>

                    <@bg.ui.field "form.amountType">
                        <select id="amountType" name="amountType">
                            <option value="PERCENT">Процент от стоимости</option>
                            <option value="FIXED">Фиксированная скидка</option>
                            <option value="PRICE">Фиксированная цена</option>
                        </select>
                    </@bg.ui.field>
                    </div>
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
                    <label for="code">Максимальное количество:</label>
                </td>
                <td>
                <@bg.ui.input path="form.allocatedCount"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="termination">Действует до:</label>
                </td>
                <td>
                <@bg.ui.input path="form.termination"/>
                </td>
            </tr>

            <tr>
                <td colspan="2">
                    <hr>
                </td>
            </tr>

            <tr>
                <td></td>
                <td>
                    <button type="submit">Создать купон</button>
                </td>
            </tr>
        </table>
    </form>
</div>

<script type="text/javascript">
    $(function () {
        $("#termination").datepicker({ "dateFormat": "yy.mm.dd"});
    });
</script>