<#macro system order><#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" --><@link "http://www.banggood.com/view_order.php?order_id={sym}" order.referenceTracking/></#macro>

<#macro china order><#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" --><@link "http://www.flytexpress.com/ShowTraceInfo.aspx?orderid={sym}" order.chinaMailTracking/></#macro>

<#macro international order><#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" --><@link "http://gdeposylka.ru/{sym}?tos=accept&apikey=418832.b3a52a082d&country=RU" order.internationalTracking/></#macro>

<#macro link template sym><#if sym?has_content><#list sym.split(",") as s><a
        href="${template?replace("{sym}", s)}">${s}</a><#if s_has_next>,</#if></#list><#else><#nested></#if></#macro>