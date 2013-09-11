<#include "/core.ftl">

<table style="height: 40px">
    <tr>
        <td align="left">
            <div class="header-logo">
                <a href="/"><img alt="logo" src="<@bg.ui.static "images/logo.png"/>"/></a>
            <#--
                            <a href="/" style="text-decoration: none !important;">
                                <span class="first-letter">B</span><span>illion</span><span
                                    class="first-letter">G</span><span>oods</span><span
                                    class="domain"><sup>.ru</sup></span>
                            </a>
            -->
            </div>
        </td>

        <td align="right" valign="middle">
        <#--
                    <div class="table-cell header-welcome">
                        Добро Пожаловать!
                        <a class="highlight" href="/account/login">Войти</a>
                        или
                        <a class="highlight" href="/account/register">Зарегистрироваться</a>
                    </div>
        -->

            <div class="table-cell header-links">
            <@bg.security.authorized "moderator">
                <div><a href="/maintain/main">Поддержка</a></div>
                <div class="divider">|</div>
            </@bg.security.authorized>

                <div><a href="/warehouse/order/status">Отслеживание заказов</a></div>

                <div class="divider">|</div>
                <div><a href="/assistance/payment">Оплата</a></div>

                <div class="divider">|</div>
                <div><a href="/assistance/shipping">Доставка</a></div>

                <div class="divider">|</div>
                <div><a href="/assistance">Помощь</a></div>
            </div>
        </td>
    </tr>
</table>

