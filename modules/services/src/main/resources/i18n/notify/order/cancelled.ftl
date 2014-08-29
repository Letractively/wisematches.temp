<#-- @ftlvariable name="OrderState" type="billiongoods.server.services.payment.OrderState.static" -->
<#-- @ftlvariable name="context" type="billiongoods.server.services.payment.Order" -->

<#import "../utils.ftl" as util>

<p>
    Ваш заказ был отменен<#if context.commentary?has_content>: ${context.commentary}</#if>.
</p>

<p>
    Мы выполним возврат денежных редств по данному заказу в течение следующих 48 часов.
</p>