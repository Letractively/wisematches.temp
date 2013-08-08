Еще ничего нет, но, возможно, у вас уже есть ваша роль.

<#include "/core.ftl"/>

<@bg.security.authorize "admin">
Точняк, вы админ. Сюда, пожалуйста: <a href="/maintain/main">Администрирование</a>
</@bg.security.authorize>