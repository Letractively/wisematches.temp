<#-- @ftlvariable name="accounts" type="billiongoods.core.account.Account[]" -->
<#-- @ftlvariable name="connection" type="org.springframework.social.connect.Connection" -->

<#include "/core.ftl">

<html>
<head>
<#include "/content/meta.ftl"/>
</head>
<body>
<table style="height: 28px; padding: 3px">
    <tr>
        <td align="left">
            <div class="header-logo">
                <a href="/"><img alt="logo" src="<@bg.ui.static "images/logo.png"/>"/></a>
            </div>
        </td>

        <td align="left" valign="middle" style="padding-left: 20px; font-size: 20px">
            <strong>Авторизация</strong>
        </td>
    </tr>
</table>

<hr>
<#assign provider=connection.key.providerId/>

<#if accounts?has_content>
<div style="padding: 3px">
    <div style="text-align: center; font-size: 18px">Выбор аккаунта</div>

    <div style="padding-top: 10px; padding-bottom: 10px">
        Пожалуйста, выберите пользователя, чей аккаунт вы хотели бы использовать для работы в нашем магазине:
        <table>
            <#list accounts as c>
                <tr>
                    <td>${c.passport.username}</td>
                </tr>
            </#list>
        </table>
    </div>
<#else>
    <div style="text-align: center; font-size: 18px">Мы практически закончили</div>

    <div style="padding-top: 10px; padding-bottom: 10px">
        Продолжив, вы сможете входить в интернет-магазин BillionGoods без ввода пароля, при помощи
        профиля
        <span style="font-size: 16px">
        <i class="social-icon-16 social-icon-${provider}"
           style="vertical-align: middle"></i> <strong>${connection.displayName!""}</strong></span>.
    </div>
</div>
</#if>

<#if !accounts?has_content>
<hr>
<div style="padding: 3px">
    <form action="/account/social/association" method="post">
        <button name="action" value="new">
            Я новый покупатель
        </button>
        * Если вы уже зарегистрированы в нашем магазине с помощью логина/пароля либо любой другой
        социальной сети, пожалуйста, закройте данное окно и войдите в аккаунт магазина ранее использованным способом.
        После этого вы сможете добавить связи с <i><@message code="account.social.type.${provider}"/></i> в ваших
        настройках.
    </form>
</div>
</#if>

</body>
</html>