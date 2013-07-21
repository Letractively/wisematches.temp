<#-- @ftlvariable name="showRememberMe" type="java.lang.Boolean" -->
<#-- @ftlvariable name="showRegistration" type="java.lang.Boolean" -->
<#-- @ftlvariable name="showPredefinedUsername" type="java.lang.Boolean" -->
<#include "/core.ftl">

<#--<script src="http://vk.com/js/api/openapi.js" type="text/javascript"></script>-->

<table width="100%">
    <tr>
        <td width="100%" style="vertical-align: top; text-align: left;">
        <#include "/content/assistance/static.ftl">
        </td>

        <td width="270" style="vertical-align: top;">
            <div id="login-navigation">
            <@wm.ui.panel.round>
                <@wm.ui.panel.round>
                    <div id="login-panel">
                        <div id="login-title"><@message code="account.login.title"/></div>
                        <div id="login-type"><@message code="account.header"/></div>

                        <form id="login-form" method="post" action="/account/loginProcessing">
                            <table cellpadding="0" cellspacing="0" border="0">
                                <tr>
                                    <td>
                                    <#--@declare id="j_username"-->
                                        <label style="white-space: nowrap;"
                                               for="j_username"><@message code="account.login.email.label"/>:</label>
                                    </td>
                                    <td>
                                        <#if showPredefinedUsername>
                                            <@wm.ui.bind "login.j_username"/>
                                            <span style="font-weight: bold;">${wm.ui.statusValue}</span>
                                            <input type="hidden" id="j_username" name="j_username"
                                                   value="${wm.ui.statusValue}"/>
                                        <#else>
                                            <@wm.ui.input path="login.j_username" size="0"/>
                                        </#if>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                    <#--@declare id="j_password"-->
                                        <label style="white-space: nowrap;"
                                               for="j_password"><@message code="account.login.password.label"/>:</label>
                                    </td>
                                    <td>
                                        <@wm.ui.input path="login.j_password" fieldType="password" size="0"/>
                                    </td>
                                </tr>
                                <#if showRememberMe>
                                    <tr>
                                        <td align="right" valign="middle"
                                            style="text-align: right; vertical-align: middle;">
                                            <@wm.ui.field path="login.rememberMe">
                                                <input type="checkbox" id="rememberMe" name="rememberMe" value="true"
                                                       <#if wm.ui.statusValue=="true">checked="checked"</#if>/>
                                            </@wm.ui.field>
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
                                    <td></td>
                                    <td align="left">
                                    <#--<button type="button" onclick="doLogin();">asd</button>-->
                                        <button type="submit"><@message code="account.login.signin.label"/></button>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2" height="33.0" align="center" valign="bottom"
                                        style="text-align: center; vertical-align: bottom;">
                                        <div>
                                            <a id="recoveryLink"
                                               href="/account/recovery/request"><@message code="account.login.recovery.label"/></a>
                                        </div>
                                        <#if showPredefinedUsername>
                                            <div>
                                                <a href="/account/login"><@message code="account.login.another.label"/></a>
                                            </div>
                                        </#if>
                                    </td>
                                </tr>
                            </table>
                        </form>
                    </div>
                </@wm.ui.panel.round>
            </@wm.ui.panel.round>
            <#if showRegistration>
                <div style="height:10px;"></div>
                <@wm.ui.panel.round>
                    <div id="register-panel">
                        <div id="register-link">
                            <button id="createAnAccount" class="account-button"
                                    onclick="wm.util.url.redirect('/account/create')">
                                <@message code="account.register.label"/>
                            </button>
                        </div>
                        <div class="separator"><@message code="separator.or"/></div>
                        <div id="login-guest-link">
                            <a href="/account/loginGuest"><@message code="account.guest.label"/></a>
                        </div>
                        <div class="separator"><@message code="separator.or"/></div>
                        <div id="info-links">
                            <@message code="info.readmore.label"/>
                            <a href="/info/about"><@message code="info.principles.label"/></a>
                            <@message code="separator.and"/><br>
                            <a href="/info/features"><@message code="info.features.label"/></a>
                        </div>
                    </div>
                </@wm.ui.panel.round>
            </#if>
            </div>
        </td>
    </tr>
</table>

<script type="text/javascript">
    $("#recoveryLink").click(function () {
        wm.util.url.redirect(wm.util.url.extend($(this).attr('href'), "email", $("#j_username").val(), true));
        return false;
    });
</script>

<#--
<script type="text/javascript">
    VK.init({
        apiId: 3540475
    });

    function doLogin() {
        VK.Auth.login(function(response) {
            alert(response);
        });
    }
</script>
-->
