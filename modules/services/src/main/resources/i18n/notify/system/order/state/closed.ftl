<#-- @ftlvariable name="context" type="billiongoods.server.services.payment.Order" -->
<p>
    Заказ #${context.id} от ${context.payment.payerName!context.payment.payer} был
    закрыт<#if  context.commentary?has_content>: ${context.commentary}</#if>
</p>
