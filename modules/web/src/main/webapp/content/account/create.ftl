<#include "/core.ftl">

<#assign termsPages = ["terms", "policy", "naming"]/>
<script type="text/javascript">
    function loadTermsPage(name) {
        $('#terms_tabs a').removeClass('active');
        $('#terms-' + name + '-page-link').addClass('active');

        $.get('/assistance/' + name + '?plain=true',
                function (data) {
                    var el = $('#terms_panel');
                    el.height($('#form').height() - $('#terms_tabs').height() - 5);
                    el.html(data);
                }).error(function (jqXHR, textStatus, errorThrown) {
                    if (textStatus == 'parsererror') {
                        var el = $('#terms_panel');
                        el.height($('#form').height() - $('#terms_tabs').height() - 5);
                        el.html(jqXHR.responseText);
                    }
                });
    }

    $(document).ready(function () {
        $('#form').submit(function () {
            $('button[type=submit]', this).attr('disabled', 'disabled');
        });
        loadTermsPage('${termsPages[0]}');
    });
</script>

<div id="registration">
    <div class="info-header">
        <div class="info-label"><@message code="account.register.label"/></div>

        <div class="info-description"><@message code="account.register.description"/></div>
    </div>

    <table cellpadding="0" cellspacing="0" border="0" width="100%">
        <tbody>
        <tr>
            <td width="500px">
                <form id="form" class="form" action="/account/create" method="post">
                    <table class="ui-widget-content" width="100%">
                        <tr>
                            <td colspan="2">
                                <div class="section label"><@message code="account.register.group.required.label"/></div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                            <#--@declare id="email"-->
                                <label for="email"><@message code="account.register.email.label"/>:</label>
                            </td>
                            <td>
                            <@wm.ui.input path="registration.email"/>
                                <span class="sample"><@message code="account.register.email.description"/></span>
                            </td>
                        </tr>

                        <tr>
                            <td>
                            <#--@declare id="username"-->
                                <label for="username"><@message code="account.register.username.label"/>:</label>
                            </td>
                            <td>
                            <@wm.ui.input path="registration.username"/>
                                <span class="sample"><@message code="account.register.username.description"/></span>
                            </td>
                        </tr>
                    <#--
                        <tr>
                            <td></td>
                            <td>
                                <button id="checkAvailability" type="button"
                                        onclick="wm.account.checkAvailability(this)">
                                <@message code="account.register.availability.check.label"/>
                                </button>
                            </td>
                        </tr>
-->

                        <tr>
                            <td>
                            <#--@declare id="password"-->
                                <label for="password"><@message code="account.register.pwd.label"/>:</label>
                            </td>
                            <td>
                            <@wm.ui.input path="registration.password" fieldType="password"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                            <#--@declare id="confirm"-->
                                <label for="confirm"><@message code="account.register.pwd-cfr.label"/>:</label>
                            </td>
                            <td>
                            <@wm.ui.input path="registration.confirm" fieldType="password"/>
                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>
                            <@wm.ui.field path="registration.rememberMe">
                                <input type="checkbox" id="rememberMe" name="rememberMe"
                                       <#if wm.ui.statusValue=="true">checked="checked"</#if>/>
                                <label for="rememberMe"><@message code="account.login.remember.label"/></label>
                            </@wm.ui.field>
                            </td>
                        </tr>

                        <tr>
                            <td colspan="2">
                                <hr>
                            </td>
                        </tr>

                        <tr>
                            <td colspan="2">
                                <div class="section label"><@message code="account.register.group.getstarted.label"/></div>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <label for="language"><@message code="account.register.language.label"/>:</label>
                            </td>
                            <td>
                            <@wm.ui.field path="registration.language">
                                <select id="language" name="language" style="width: 170px;">
                                    <#list ["en", "ru"] as l>
                                        <option value="${l}" <#if (locale==l)>selected="selected"</#if>>
                                            <@message code="language.${l?lower_case}"/>
                                        </option>
                                    </#list>
                                </select>
                            </@wm.ui.field>
                                <input type="hidden" id="timezone" name="timezone" value="0">
                                <script type="text/javascript">
                                    document.getElementById('timezone').value = new Date().getTimezoneOffset();
                                </script>
                                <span class="sample"><@message code="account.register.language.description"/></span>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <label><@message code='account.register.terms.label'/>:</label>
                            </td>
                            <td align="center">
                                <div style="padding-bottom: 10px;"><@message code="account.register.terms.description"/></div>

                                <button id="createAccount"
                                        name="createAccount"
                                        type="submit"
                                        value="submit"><@message code='account.register.submit.label'/></button>
                            </td>
                        </tr>
                    </table>
                </form>
            </td>
            <td>
                <div id="terms">
                    <div id="terms_tabs" class="ui-widget-content" style="padding: 0 0 0 10px;">
                    <#list termsPages as page>
                        <a id="terms-${page}-page-link" class="ui-widget-content tab <#if page_index == 0>active</#if>"
                           style="padding: 0"
                           href="javascript: loadTermsPage('${page}');">
                            <@message code="info.policies.${page}.label"/>
                        </a>
                    </#list>
                        <div style="clear: left;"></div>
                    </div>
                    <div id="terms_panel" class="ui-widget-content" style="padding: 0 2 0 5px;"></div>
                </div>
            </td>
        </tr>
        </tbody>
    </table>
</div>