<#-- @ftlvariable name="showRememberMe" type="java.lang.Boolean" -->
<#include "/core.ftl">

<div class="account-action">
    <div class="account-login">
        <form id="loginForm" method="post" action="/account/loginProcessing">
            <table cellpadding="0" cellspacing="0" border="0">
                <tr>
                    <td>
                        <label style="white-space: nowrap;"
                               for="j_username">Ваш EMail адрес:</label>
                    </td>
                    <td>
                    <@bg.ui.input path="login.j_username" size="0"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label style="white-space: nowrap;"
                               for="j_password">Ваш пароль:</label>
                    </td>
                    <td>
                    <@bg.ui.input path="login.j_password" fieldType="password" size="0"/>
                    </td>
                </tr>
            <#if showRememberMe?? && showRememberMe>
                <tr>
                    <td align="right" valign="middle"
                        style="text-align: right; vertical-align: middle;">
                        <@bg.ui.field path="login.rememberMe">
                            <input type="checkbox" id="rememberMe" name="rememberMe" value="true"
                                   <#if bg.ui.statusValue=="true">checked="checked"</#if>/>
                        </@bg.ui.field>
                    </td>
                    <td align="left" valign="middle"
                        style="text-align: left; vertical-align: middle;">
                        <label for="rememberMe"><@message code="account.login.remember.label"/></label>
                    </td>
                </tr>
            <#else>
                <tr>
                    <td colspan="2">
                        <input type="hidden" id="rememberMe" name="rememberMe" value="true"/>
                    </td>
                </tr>
            </#if>
                <tr>
                    <td>
                        <button type="submit">Войти</button>
                    </td>
                    <td align="right">
                        <a id="recoveryLink"
                           href="/account/recovery/request">Восстановить пароль</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>

    <div class="account-register">
        <form id="createForm" method="post" action="/account/create">
            <table>
                <tr>
                    <td>
                        <label style="white-space: nowrap;"
                               for="username">Ваше имя:</label>
                    </td>
                    <td>
                    <@bg.ui.input path="registration.username" size="0"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label style="white-space: nowrap;"
                               for="email">EMail адрес:</label>
                    </td>
                    <td>
                    <@bg.ui.input path="registration.email" size="0"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label style="white-space: nowrap;"
                               for="password">Пароль:</label>
                    </td>
                    <td>
                    <@bg.ui.input path="registration.password" fieldType="password"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label style="white-space: nowrap;"
                               for="confirm">Подтверждение:</label>
                    </td>
                    <td>
                    <@bg.ui.input path="registration.confirm" fieldType="password"/>
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td align="left">
                        <button type="submit">Зарегистрироваться</button>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    $("#recoveryLink").click(function () {
        wm.util.url.redirect(wm.util.url.extend($(this).attr('href'), "email", $("#j_username").val(), true));
        return false;
    });
</script>
