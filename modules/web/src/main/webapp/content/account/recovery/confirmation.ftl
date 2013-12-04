<#-- @ftlvariable name="submittedEmail" type="java.lang.String" -->
<#-- @ftlvariable name="notificationWasSent" type="java.lang.Boolean" -->
<#include "/core.ftl">

<div id="recovery" class="assistance">
    <div id="info-recovery-header">
        <div class="tit"><@message code="account.recovery.check.label"/></div>

        <div class="toc"><@message code="account.recovery.check.description"/></div>
    </div>

    <div id="info-recovery-form" class="cnt">
        <form id="recoveryForm" action="/account/recovery/confirmation" method="post">
            <table class="x-panel" style="padding-left: 10px; padding-right: 10px;">
                <tr>
                    <td colspan="2">
                        <div class="section label"><@message code="account.recovery.check.email.label"/></div>
                    </td>
                </tr>
                <tr>
                    <td>
                    <#--@declare id="email"-->
                        <label for="email"><@message code="account.register.email.label"/>:</label>
                    </td>
                    <td>
                        <div>${submittedEmail}</div>
                        <div class="sample"><@message code="account.recovery.check.email.description" args=[submittedEmail]/></div>
                        <input type="hidden" name="email" value="${submittedEmail}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                    <#--@declare id="token"-->
                        <label for="token"><@message code="account.recovery.check.token.label"/>:</label>
                    </td>
                    <td>
                    <@bg.ui.input path="recovery.token"/>
                    </td>
                </tr>

                <tr>
                    <td colspan="2">
                        <hr>
                    </td>
                </tr>

                <tr>
                    <td colspan="2">
                        <div class="section label"><@message code="account.recovery.check.pwd.label"/></div>
                    </td>
                </tr>
                <tr>
                    <td>
                    <#--@declare id="password"-->
                        <label for="password"><@message code="account.register.pwd.label"/>:</label>
                    </td>
                    <td>
                    <@bg.ui.input path="recovery.password" fieldType="password"/>
                    </td>
                </tr>
                <tr>
                    <td>
                    <#--@declare id="confirm"-->
                        <label for="confirm"><@message code="account.register.pwd-cfr.label"/>:</label>
                    </td>
                    <td>
                    <@bg.ui.input path="recovery.confirm" fieldType="password"/>
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                    <@bg.ui.field path="recovery.rememberMe">
                        <input type="checkbox" id="rememberMe" name="rememberMe"
                               <#if bg.ui.statusValue=="true">checked="checked"</#if>/>
                        <label for="rememberMe"><@message code="account.signin.remember.label"/></label>
                    </@bg.ui.field>
                    </td>
                </tr>

                <tr>
                    <td colspan="2">
                        <hr>
                    </td>
                </tr>

                <tr>
                    <td></td>
                    <td>
                        <button id="recoveryAccount"
                                class="bg-ui-button"
                                name="recoveryAccount"
                                type="submit"
                                value="true"><@message code='account.recovery.submit.label'/></button>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
