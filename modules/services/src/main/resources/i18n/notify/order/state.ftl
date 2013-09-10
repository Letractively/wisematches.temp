<#-- @ftlvariable name="context" type="billiongoods.server.services.payment.Order" -->

<#import "../utils.ftl" as util>

<#if context.orderState.new>
<p>
    Мы сформировали новый заказ по вашему запросу #${context.id} и ожадием его оплаты.
</p>
<#elseif context.orderState.billing>
<p>
    По вашему заказу #${context.id} вам выставлен счет #${context.token}. Пожалуйста, оплатите счет в течении 30 минут
    до
    истечения срока действия.
</p>
<p>
    После подтверждения факта оплаты мы перейдем к формированию заказа.
</p>
<#elseif context.orderState.accepted>
<p>
    Мы получили подтверждения оплаты заказа #${context.id}. В ближайщее время мы отправим заказ на формирования.
</p>
<#elseif context.orderState.rejected>
<p>
    Мы получили отказ от оплаты счета #${context.id}. В ближайщее время счет будет аннулирован.
</p>
<#elseif context.orderState.failed>
<p>
    Мы не смогли обработать счет #${context.id} из-за внутренней ошибки при работе с PayPal.
</p>
<p>
    Мы постараемся исправить данную ошибку как можно быстрее. Пожалуйста, свяжитесь с нами по адресу
    <@util.mailto "support"/> если вам необходима дополнительная информация.
</p>
<#elseif context.orderState.processing>
<p>
    Ваш заказ #${context.id} отправлен в службу доставки. Упаковка и отправка товара займет 2-3 рабочих дня.
</p>
<#elseif context.orderState.shipping>
<p>
    Ваш заказ был передан в обработку на почту Китая. Номер вашего отправления на почте Китая:
    <a href="http://www.flytexpress.com/ShowTraceInfo.aspx?orderid=${context.chinaMailTracking}">${context.chinaMailTracking}</a>
</p>
<#elseif context.orderState.shipped>
<p>
    Ваш заказ был отправл из Китая.
    <#if context.internationalTracking?has_content>
        Ваш номер для отслеживания посылки на сайте почты России: ${context.internationalTracking}. Вы так же
        можете отслеживать теперь свой заказ с помощью сервиса <a
            href="http://gdeposylka.ru/${context.internationalTracking}?tos=accept&apikey=418832.b3a52a082d&country=RU">ГдеПосылка.ру</a>
    <#else>
        К сожалению, ваш заказ не предусматривает получение международного номер для отслеживания.
    </#if>
</p>
<#else>
<p>
    Мы помним о вашем заказе и работаем над его доставкой. В данный момент заказ #${context.id} изменил свое состояние
    на
${context.orderState.name()}.
</p>
<p>
    Пожалуйста, обратитесь в службу поддержки <@util.mailto "support"/> для получения более подробной информации.
</p>
</#if>
<p>
    Вы можете посмотреть детали вашего заказа и историю изменений на
    странице <@util.link "/warehouse/order/status?order=${context.id}">
    Отслеживания заказов</@util.link>.
    <br>Пожалуйста,
    укажите номер вашего заказа ${context.id} и адрес электронной почты PayPal, с которого осуществлялась его
    оплата.
</p>