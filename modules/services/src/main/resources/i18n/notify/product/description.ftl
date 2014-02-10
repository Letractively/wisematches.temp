<#-- @ftlvariable name="context" type="billiongoods.server.warehouse.Product" -->

<#import "../utils.ftl" as util>

<p>
    Как вы и просили, мы добавили описание для
    товара <@util.link href="/warehouse/product/${context.symbolicUri}">${context.name}</@util.link>.
</p>

<p>
    Вы можете ознакомиться с полным описание товара на нашем сайте по
    ссылке <@util.link href="/warehouse/product/${context.symbolicUri}"/>.
</p>