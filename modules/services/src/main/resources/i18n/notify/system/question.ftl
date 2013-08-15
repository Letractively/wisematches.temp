<#-- @ftlvariable name="context.name" type="java.lang.String" -->
<#-- @ftlvariable name="context.email" type="java.lang.String" -->
<#-- @ftlvariable name="context.message" type="java.lang.String" -->

<p>
    Получено новое сообщение о заказчика.
</p>
<p>
    EMail: ${context.email}
</p>
<p>
    Имя: ${context.name}
</p>
<hr>
<p>
${context.message}
</p>