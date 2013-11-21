<#-- @ftlvariable name="userIds" type="org.springframework.social.connect.Connection" -->
<#-- @ftlvariable name="connection" type="org.springframework.social.connect.Connection" -->
<#include "/core.ftl">

<html>
<head>
<#include "/content/head.ftl"/>
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

<div style="padding: 3px">
<#assign provider=connection.key.providerId/>
    <div style="text-align: center; font-size: 18px">Мы практически закончили</div>

    <div style="padding-top: 10px; padding-bottom: 10px">
        Продолжив, вы сможете входить в интернет-магазин BillionGoods без ввода пароля, при помощи
        профиля
        <span style="font-size: 16px">
        <i class="social-icon-16 social-icon-${provider}"></i> <strong>${connection.displayName}</strong></span>.
        <br>
        <br>
        Пожалуйста, дайте нам знать, являетесь ли вы новым покупателем, либо у вас уже есть логин / пароль в нашем
        магазине, с которым следует связать вашу учетную запись.
    </div>
</div>

<hr>
<div style="padding: 3px">

    <form action="/account/social/association" method="post">
        <button name="type" value="new">
            Я новый покупатель
        </button>

        <button name="type" value="registered">
            У меня уже есть логин
        </button>
    </form>
</div>

</body>
</html>