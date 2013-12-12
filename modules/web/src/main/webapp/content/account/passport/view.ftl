<#-- @ftlvariable name="timeZones" type="billiongoods.server.services.timezone.TimeZoneEntry[]" -->
<#-- @ftlvariable name="form" type="billiongoods.server.web.servlet.mvc.account.form.PassportForm" -->

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
                            ${form.username!""}
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" nowrap="nowrap">
                                Часовой пояс:
                            </td>
                            <td>
                            <#assign timeZoneEntry=""/>
                            <#list timeZones as tz>
                                <#if tz.id==form.timeZone>
                                    <#assign timeZoneEntry=tz/>
                                </#if>
                            </#list>

                            <#if timeZoneEntry?has_content>
                            ${timeZoneEntry.displayName}
                            <#else>
                                (GMT+00:00) Время по Гринвичу: Дублин, Лондон, Лиссабон, Эдинбург
                            </#if>
                            </td>
                        </tr>
                    </table>
                </div>

                <div class="cnt">
                    <a href="/account/passport/personal"><strong>Изменить персональные данные</strong></a>
                </div>
            </td>

            <td valign="top" nowrap="nowrap">
                <div class="cnt">
                <#if member.email?has_content>
                    <a href="/account/passport/email">Именить адрес электронной почты</a>
                <#else>
                    <a href="/account/passport/email">Добавить адрес электронной почты</a>
                </#if>
                    <br>
                    <span class="sample">Адрес электронной почты для получения статуса заказов</span>
                </div>

                <div class="cnt">
                    <a href="/account/passport/password">Сменить пароль</a>
                    <br>
                    <span class="sample">Храните в тайне ваш пароль на BillionGoods</span>
                </div>

            <#--
                            <div class="cnt">
                                <a href="/account/passport/remove">Удалить аккаунт</a>
                            </div>
            -->
            </td>
        </tr>
    </table>
</div>
