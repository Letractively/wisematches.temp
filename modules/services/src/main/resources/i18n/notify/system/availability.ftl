<#-- @ftlvariable name="context" type="billiongoods.server.warehouse.ProductPreview" -->
<#import "../utils.ftl" as util>

Получен новый запрос на поступление товара: <@util.link "/warehouse/product/${context.id}">A${context.id}: ${context.name}</@util.link>
