<#include "/core.ftl">

<div id="recovery" class="assistance">
    <div id="info-recovery-header">
        <div class="tit"><@message code="account.recovery.request.label"/></div>

        <div class="toc"><@message code="account.recovery.request.description"/></div>
    </div>

    <div id="info-recovery-form" class="cnt">
        <form id="recoveryForm" action="/account/recovery/request" method="post">
            <div>
            <#--@declare id="email"-->
                <label class="label" for="email"><@message code="account.register.email.label"/>:</label>
            <@bg.ui.input path="recovery.email"/>
                <span class="sample"><@message code="account.recovery.email.description"/></span>
            </div>
            <div>
                <button id="recoveryAccount"
                        class="bg-ui-button"
                        name="recoveryAccount"
                        type="submit"
                        value="true"><@message code='account.recovery.submit.label'/></button>
            </div>
        </form>
    </div>

    <div id="info-recovery-footer" style="padding: 10px">
    <@message code="account.recovery.request.info.description"/>
    </div>
</div>