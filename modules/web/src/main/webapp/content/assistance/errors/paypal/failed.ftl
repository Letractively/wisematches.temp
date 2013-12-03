<#-- @ftlvariable name="errorArguments" type="java.lang.Object[]" -->

<#assign exception=errorArguments[0]/>
<p>
    Мы очень сожалеем, но в связи с ошибкой при работе с PayPal мы не смогли провести ваш платеж.
</p>

<p>Обращаем ваше внимание,
    что транзакция не была завершена и мы не сняли никакие денежные средства с вашего счета. Тем не менее,
    PayPal уже мог
    зарезервировать
    денежные средства на вашем счету, если вы оплачивали товар пластиковой картой. Пажалуйста, обратитесь в
    <a
            href="https://www.paypal.com/ru/cgi-bin/webscr?cmd=xpt/User/customerservice/GXOLogin-outside">службу
        поддержки
        PayPal</a>, в случае каких либо осложнений с возвратом денежных средств.
</p>

<p>
    Пожалуйста, обратитесь в <a href="/assistance/contacts">отдел поддержки пользователей</a>, если у вас
    возникли
    вопросы. Мы готовы предоставить любую техническую информацию, которая побребуется.
</p>

<p>
    Пожалуйста, запишите внутренней транзакции вашей транзакции PayPal, это значительно упростит процедуру
    получения
    необходимых данные: <strong>${exception.tnxId}</strong>.
</p>

<#if exception.queryError??>
    <#assign error=exception.queryError/>
<p>
    Ответ, полученный от системы PayPal:
<table class="paypal-failed-details">
    <tr>
        <th>Код ошибки:</th>
        <td>${error.code}</td>
    </tr>
    <tr>
        <th>Короткое сообщение:</th>
        <td>${error.shortMessage}</td>
    </tr>
    <tr>
        <th>Длинное сообшение:</th>
        <td>${error.longMessage}</td>
    </tr>
</table>
</p>
</#if>

<p>
    Приносим нам свои извенения и постараемся сделать так, что бы это не повторялось в дальнейшем.
</p>
