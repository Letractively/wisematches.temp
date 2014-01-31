<#include "/core.ftl"/>

<div class="order-status">
    <form action="/warehouse/order/status" method="post">
        <table>
            <tr>
                <td valign="top">
                    <label for="order">Номер Заказа:</label>
                </td>
                <td valign="top">
                <@bg.ui.input "form.order"/>
                    <div class="sample">Номер заказа, который вы хотели бы отследить.</div>
                </td>
            </tr>

            <tr>
                <td valign="top">
                    <label for="email">Электронная почта:</label>
                </td>
                <td valign="top">
                <@bg.ui.input "form.email"/>
                    <div class="sample">
                        Адрес электронной почты PayPal аккаунта, с которого осуществлялась оплата.
                    </div>
                </td>
            </tr>

            <tr>
                <td>
                </td>
                <td>
                <@bg.ui.field "form"/>

                    <div>
                        <button class="bg-ui-button" type="submit">Проверить</button>
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>
