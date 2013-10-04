<#-- @ftlvariable name="context" type="billiongoods.server.warehouse.Product" -->
<#import "../utils.ftl" as util>


<p>
    Мы получили новую партию <@util.link href="/warehouse/product/${context.id}">${context.name}</@util.link>, которым
    вы интересовались и готовы поставить его вам, если он всё еще интересен для вас.
</p>

<p>
    Вы можете заказать данный товар, воспользовавшись следующей
    ссылкой: <@util.link href="/warehouse/product/${context.id}"/>.
</p>