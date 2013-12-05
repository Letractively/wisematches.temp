<#include "/core.ftl">

<div class="settings">
    <div class="tit">
        Персональные данные
    </div>
    <table>
        <tr>
            <td width="100%" valign="top">
                <div class="cnt">
                    <table>
                        <tr>
                            <td valign="top" width="30%" nowrap="nowrap">
                                Имя и фамилия:
                            </td>
                            <td>
                            ${principal.username!""}
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" nowrap="nowrap">
                                Часовой пояс:
                            </td>
                            <td>
                                (GMT+00:00) Время по Гринвичу: Дублин, Лондон, Лиссабон, Эдинбург
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" nowrap="nowrap">
                                Уведомления:
                            </td>
                            <td>
                                Отправлять на адрес аккаунта
                            </td>
                        </tr>
                    </table>
                </div>

                <div class="cnt">
                    <a href="/account/settings/modify"><strong>Изменить персональные данные</strong></a>
                </div>
            </td>

            <td valign="top" nowrap="nowrap">
                <div class="cnt">
                <#if principal.email?has_content>
                    <a href="/account/settings/email">Именить адрес электронной почты</a>
                <#else>
                    <a href="/account/settings/email">Добавить адрес электронной почты</a>
                </#if>
                    <br>
                    <span class="sample">Адрес электронной почты для получения статуса заказов</span>
                </div>

                <div class="cnt">
                    <a href="/account/settings/password">Сменить пароль</a>
                    <br>
                    <span class="sample">Храните в тайне ваш пароль на BillionGoods</span>
                </div>

                <div class="cnt">
                    <a href="/account/settings/remove">Удалить аккаунт</a>
                </div>
            </td>
        </tr>
    </table>

</div>
