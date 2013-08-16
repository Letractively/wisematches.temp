<#-- @ftlvariable name="context.order" type="billiongoods.server.services.payment.Order" -->

<#assign order=context.order/>

<#import "../utils.ftl" as util>

<#if order.orderState.new>
<p>
    Мы сформировали новый заказ по вашему запросу #${order.id} и ожадием его оплаты.
</p>
<#elseif order.orderState.billing>
<p>
    По вашему заказу #${order.id} вам выставлен счет #${order.token}. Пожалуйста, оплатите счет в течении 30 минут до
    истечения срока действия.
</p>
<p>
    После подтверждения факта оплаты мы перейдем к формированию заказа.
</p>
<#elseif order.orderState.accepted>
<p>
    Мы получили подтверждения оплаты заказа #${order.id}. В ближайщее время мы отправим заказ на формирования.
</p>
<#elseif order.orderState.rejected>
<p>
    Мы получили отказ от оплаты счета #${order.id}. В ближайщее время счет будет аннулирован.
</p>
<#elseif order.orderState.failed>
<p>
    Мы не смогли обработать счет #${order.id} из-за внутренней ошибки при работе с PayPal.
</p>
<p>
    Мы постараемся исправить данную ошибку как можно быстрее. Пожалуйста, свяжитесь с нами по адресу
    <@util.mailto "support"/> если вам необходима дополнительная информация.
</p>
<#elseif order.orderState.processing>
<p>
    Ваш заказ #${order.id} отправлен в службу доставки. Упаклвка и отправка товара займен 2-3 рабочих дня.
</p>
<#elseif order.orderState.shipping>
<p>
    Ваш заказ был передан в обработку на почту Китая. Номер вашего отправления на почте Китая:
    <a href="http://www.flytexpress.com/ShowTraceInfo.aspx?orderid=${order.chinaMailTracking}">${order.chinaMailTracking}</a>
</p>
<#elseif order.orderState.shipped>
<p>
    Ваш заказ был отправл из Китая.
    <#if order.internationalTracking?has_content>
        Ваш номер для отслеживания посылки на сайте почты России: ${order.internationalTracking}. Вы так же
        можете отслеживать теперь свой заказ с помощью сервиса <a
            href="http://gdeposylka.ru/${order.internationalTracking}?tos=accept&apikey=418832.b3a52a082d&country=RU">ГдеПосылка.ру</a>
    <#else>
        К сожалению, ваш заказ не предусматривает получение международного номер для отслеживания.
    </#if>
</p>
<#else>
<p>
    Мы помним о вашем заказе и работаем над его доставкой. В данный момент заказ #${order.id} изменил свое состояние на
${order.orderState.name()}.
</p>
<p>
    Пожалуйста, обратитесь в службу поддержки <@util.mailto "support"/> для получения более подробной информации.
</p>
</#if>
<p>
    Вы можете посмотреть детали вашего заказа и историю изменений на
    странице <@util.link "/warehouse/order/status?order=${order.id}">
    Отслеживания заказов</@util.link>.
    <br>Пожалуйста,
    укажите номер вашего заказа ${order.id} и адрес электронной почты PayPal, с которого осуществлялась оплата его
    оплата.
</p>