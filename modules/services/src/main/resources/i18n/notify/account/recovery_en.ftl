<#-- @ftlvariable name="context.principal" type="billiongoods.core.personality.player.account.Account" -->
<#-- @ftlvariable name="context.recoveryToken" type="java.lang.String" -->
<#import "../utils.ftl" as util>


<p>
    This email contains account recovery token. Please copy and paste the token to appropriate field of
    recovery password form.
</p>
<p>
    Recovery Token: ${context.recoveryToken}.
</p>

<p>
    You also can use the following link for your account recovering:
<@util.link href='/account/recovery/confirmation?language=${context.principal.language.code}&email=${context.principal.email}&token=${context.recoveryToken}'/>
</p>

<br>
<p>
    If you didn't initiate recovery BillionGoods password and don't recognize this message, it means someone tried
    to force your a account at BillionGoods. We recommend contacting a
    BillionGoods Support Team by email: <@util.mailto box="account-support"/>
</p>