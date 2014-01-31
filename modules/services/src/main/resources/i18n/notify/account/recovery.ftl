<#-- @ftlvariable name="context.recipient" type="billiongoods.server.services.notify.Recipient.Person" -->
<#-- @ftlvariable name="context.recoveryToken" type="java.lang.String" -->
<#import "../utils.ftl" as util>

<p>
    Данное письмо содержит код для смены вашего пароля. Скопируйте его в соответствующее поле восстановления и введите
    новый пароль для вашего BillionGoods аккаунта.
</p>
<p>
    Код восстановления: ${context.recoveryToken}.
</p>

<p>
    Вы так же можете восстановить пароль использую специальную ссылку:
<@util.link href='/account/recovery/confirmation?email=${context.recipient.email}&token=${context.recoveryToken}'/>
</p>

<br>

<p>
    Если вы не начинали процедуру восстановления пароля BillionGoods и получение данного письма вызывает у вас
    недоумение,
    это значит что кто-то пытается взламать ваш BillionGoods аккаунт. Мы рекомендуем вам обратиться в службу поддержки
    BillionGoods по адресу: <@util.mailto box="account-support"/>.
</p>