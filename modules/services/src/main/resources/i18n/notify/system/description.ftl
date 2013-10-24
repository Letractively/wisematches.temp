<#-- @ftlvariable name="context" type="billiongoods.server.warehouse.ProductPreview" -->
<#import "../utils.ftl" as util>

Получен новый запрос на добавление описание для товара: <@util.link "/maintain/product?id=${context.id}">A${context.id}: ${context.name}</@util.link>
