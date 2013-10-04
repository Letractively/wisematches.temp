<#-- @ftlvariable name="context" type="billiongoods.server.warehouse.ProductDescription" -->
<#import "../utils.ftl" as util>

Получен новый запрос на поступление товара: <@util.link "/maintain/product?id=${context.id}">A${context.id}: ${context.name}</@util.link>
