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
    Ваш заказ был передан отправлена на почту Китая.
    <#if context.internationalTracking?has_content>
        Номер вашего отправления на почте Китая: <a
            href="http://www.flytexpress.com/ShowTraceInfo.aspx?orderid=${context.chinaMailTracking}">${context.chinaMailTracking}</a>
    <#else>
        К сожалению, ваш заказ не предусматривает получение номера для отслеживания.
    </#if>
</p>
<#elseif context.orderState.shipped>
<p>
    Ваш заказ был отправлен.
    <#if context.internationalTracking?has_content>
        Ваш международный номер для отслеживания посылки: ${context.internationalTracking}.
        Вы можете проверить ее статус на почте <a
            href="http://www.russianpost.ru/rp/servise/ru/home/postuslug/trackingpo">сайте почты России</a>
        либо воспользоваться сторонним сервисом <a
            href="http://gdeposylka.ru/${context.internationalTracking}?tos=accept&apikey=418832.b3a52a082d&country=RU">ГдеПосылка.ру</a>
        для отслеживания посылки.
    <#else>
        К сожалению, ваш заказ не предусматривает получение международного номера для отслеживания.
    </#if>
</p>
<#elseif context.orderState.suspended>
<p>
    Обработка вашего заказа была приостановлена в связи с: ${context.commentary}. Мы продолжим обработку вашего заказа
    как только выясним всю необходимую информацию для оформления заказа.
</p>
<#elseif context.orderState.cancelled>
<p>
    Ваш заказ был отменен в связи с: ${context.commentary}. Пожалуйста, оформите новый заказ, если данный товар всё
    еще интересен для вас, пожалуйста сформируйте новый счет для оплаты.
</p>
<#elseif context.orderState.closed>
<p>
    Ваш заказ был закрыт в связи с окончанием его обработки. Мы надеемся, что вы остались довольны оказанными нами
    услугами
</p>
<p>
    Если вы считаете, что ваш заказ был закрыт ошибочно либо у вас все еще остались какие-либо вопросы
    по поводу заказа, пожалуйста, обратитесь в службу поддержки <@util.mailto "support"/> для получения более
    подробной информации.
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