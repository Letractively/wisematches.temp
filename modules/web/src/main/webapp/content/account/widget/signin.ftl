<#-- @ftlvariable name="principal" type="billiongoods.core.Member" -->

<#include "/core.ftl">

<div class="table-cell header-welcome">
<@bg.security.permitted "member";member>
    <#if member>
        Добро Пожаловать, <a class="highlight" href="/privacy/view">${principal.username}</a>
    <#else>
        Добро Пожаловать!
        <a class="highlight" href="/account/signin">Войти</a>
    </#if>
</@bg.security.permitted>

</div>

<div style="display: none">
    <form action="">
        <button value=""></button>
    </form>
</div>