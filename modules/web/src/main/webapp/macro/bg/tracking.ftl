<#macro system sym=""><#-- @ftlvariable name="sym" type="java.lang.String" --><@link "http://www.banggood.com/view_order.php?order_id={sym}" sym/></#macro>

<#macro china sym=""><#-- @ftlvariable name="sym" type="java.lang.String" --><@link "http://www.flytexpress.com/ShowTraceInfo.aspx?orderid={sym}" sym/></#macro>

<#macro international sym=""><#-- @ftlvariable name="sym" type="java.lang.String" --><@link "http://gdeposylka.ru/{sym}?tos=accept&apikey=418832.b3a52a082d&country=RU" sym/></#macro>

<#macro link template sym=""><#if sym?has_content><#list sym?split(",") as s><a
        href="${template?replace("{sym}", s?trim)}">${s?trim}</a><#if s_has_next>,</#if></#list><#else><#nested></#if></#macro>
