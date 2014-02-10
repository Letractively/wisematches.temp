<#-- @ftlvariable name="context" type="billiongoods.server.warehouse.Product" -->
<#import "../utils.ftl" as util>


<p>
    Мы получили новую партию <@util.link href="/warehouse/product/${context.symbolicUri}">${context.name}</@util.link>,
    которым
    вы интересовались и готовы отправить его, если он всё еще интересен для вас.
</p>

<p>
    Вы можете заказать данный товар, воспользовавшись следующей
    ссылкой: <@util.link href="/warehouse/product/${context.symbolicUri}"/>.
</p>