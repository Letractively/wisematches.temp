<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->
<#-- @ftlvariable name="confirmation" type="boolean" -->

<#include "/core.ftl"/>

<#assign state=order.orderState/>
<#assign shipment=order.shipment/>
<#assign stateName=state.name()?lower_case/>

<div class="order ${stateName}">
<#if confirmation?? && confirmation>
<div class="info">
    <#if state.accepted>
        Ваш заказ принят в обработку. Вы можете посмотреть параметры вашего заказа ниже. Пожалуйста, если вы не
        включали
        уведомления о состоянии заказа, мы рекомендуем записать вам его номер для дальнейшего самостоятельного
        отслеживания.
    <#elseif state.failed>
        Мы приносим свои извинения, но мы не смогли обработать ваш заказ. Мы знаем об этой ошибке и постараемся ее
        осправить как можно скорее. Обращаем ваше внимание, что данный заказ будет удален из системы в ближайщем
        будущем без каких-либо уведомлений с нашей стороны но мы сохраним все данные о транзакции с PayPal.
    </#if>
</div>

    <#include "metrics.ftl"/>
</#if>

<div class="tit">
    <div style="display: inline-block">
        Заказ #${order.id} от ${messageSource.formatDate(order.creationTime, locale)}
    </div>
    <div style="display: inline-block; float: right">
    <#if order.internationalTracking?has_content>
            <@bg.link.tracking order.internationalTracking/>
        </#if>
    </div>
</div>

<table class="info">
    <tr>
        <td valign="top" nowrap="nowrap">
            <label>Статус заказа:</label>
        </td>
        <td>
            <div style="display: inline-block">
                <span class="status"><@message code="order.status.${stateName}.label"/></span>,
            ${messageSource.formatDate(order.timestamp, locale)}

                <div class="sample">
                <@message code="order.status.${stateName}.description"/>
                </div>
            </div>
            <div style="display: inline-block; float: right">
                <a id="showOrderLogs" href="#">история обработки</a>

                <div id="orderLogs" style="display: none">
                    <table>
                    <#list order.orderLogs as l>
                        <#assign state=l.orderState/>
                        <#assign stateName=state.name()?lower_case/>
                        <tr class="order-log">
                            <td valign="top" nowrap="nowrap">${messageSource.formatDate(l.timeStamp, locale)}
                                <br>${messageSource.formatTime(l.timeStamp, locale)}
                            </td>
                            <td valign="top">
                                <div>
                                    <@message code="order.status.${stateName}.label"/>
                                </div>
                                <div class="sample">
                                    <@message code="order.status.${stateName}.description"/>
                                </div>

                                <#if l.commentary?has_content>
                                    <div class="comment">
                                    ${l.commentary}
                                    </div>
                                </#if>
                            </td valign="top">
                            <td valign="top" width="20%">
                                <#if state.billing>
                                    Номер счета:<br>${l.parameter!""}
                                <#elseif state.accepted || state.rejected>
                                    Номер платежа:<br>${l.parameter!""}
                                <#elseif state.processing>
                                    Номер комплектации:<br>
                                    <#if l.parameter?has_content>${l.parameter}<#else>ожидает обработки</#if>
                                <#elseif state.shipping>
                                    Код почты Китая:<br><a
                                        href="http://www.flytexpress.com/ShowTraceInfo.aspx?orderid=${l.parameter!""}">${l.parameter!"не предоставляется"}</a>
                                <#elseif state.shipped>
                                    Международный код:<br>
                                    <#if l.parameter?has_content>
                                        <a href="http://gdeposylka.ru/${l.parameter}?tos=accept&apikey=418832.b3a52a082d&country=RU">${l.parameter}</a>
                                    <#else>
                                        не предоставляется
                                    </#if>
                                <#elseif  state.failed>
                                    Заказ отменен:<br>${l.parameter!""}
                                </#if>
                            </td>
                        </tr>
                    </#list>
                    </table>
                </div>
            </div>
        </td>
    </tr>

<#if order.commentary?has_content>
    <tr>
        <td valign="top" nowrap="nowrap">
            <label for="">Комментарий:</label>
        </td>
        <td>
        ${order.commentary}
        </td>
    </tr>
</#if>

<#if order.paymentId?has_content && order.payer?has_content>
    <tr>
        <td valign="top" nowrap="nowrap">
            <label for="">Статус оплаты:</label>
        </td>
        <td>
            Оплачен через PayPal.
            <#if order.payer?has_content>
                <br> Аккаунт: ${order.payer}
            </#if>
            <#if order.paymentId?has_content>
                <br> Код операции: ${order.paymentId}
            </#if>
            <#if order.payerNote?has_content>
                <br> Комментарий к платежу: ${order.payerNote}
            </#if>
        </td>
    </tr>
</#if>

    <tr>
        <td valign="top" nowrap="nowrap">
            <label for="">Способ доставки:</label>
        </td>
        <td>
            <div class="shipment" style="padding-bottom: 10px">
            <#if shipment.type==ShipmentType.FREE>
                Бесплатная доставка без номера отслеживания
            <#elseif shipment.type==ShipmentType.REGISTERED>
                <#if order.internationalTracking?has_content>
                    Отслеживаемое отправление: <strong><@bg.link.tracking order.internationalTracking/></strong>
                <#else>
                    Отслеживаемое отправление: номер отслеживания еще не назначен
                </#if>
            </#if>
            </div>
        </td>
    </tr>

    <tr>
        <td valign="top" nowrap="nowrap">
            <label for="">Адрес доставки:</label>
        </td>
        <td>
        <#assign address=shipment.address/>
        ${address.name}
            <br>
        ${address.streetAddress},
            <br>
        ${address.city}, ${address.region}, ${address.postalCode}
        </td>
    </tr>

<@bg.security.unauthorized "moderator">
    <#if order.payer?has_content>
        <tr>
            <td colspan="2" align="right">
                <div class="tracking">
                    <button type="button" value="true" <#if order.tracking>style="display: none"</#if>>Включить
                        уведомления по e-mail
                    </button>
                    <button type="button" value="false" <#if !order.tracking>style="display: none"</#if>>Отключить
                        уведомления по e-mail
                    </button>
                    <span class="sample">(${order.payer})</span>
                </div>
            </td>
        </tr>
    </#if>
</@bg.security.unauthorized>
</table>

<div class="basket">
<#assign totalCount=0/>
<#assign totalWeight=0/>
    <table class="cnt">
        <tr>
            <th width="100%">Наименование</th>
            <th>Опции</th>
        <@bg.security.authorized "moderator">
            <th>SKU</th>
        </@bg.security.authorized>
            <th>Цена</th>
            <th>Количество</th>
            <th>Вес</th>
            <th>Итого</th>
        <@bg.security.authorized "moderator">
            <th>Поставщик</th>
        </@bg.security.authorized>
        </tr>

    <#assign totalAmountUsd=0/>
    <#list order.orderItems as i>
        <#assign product=i.product/>
        <#assign totalCount=totalCount+i.quantity/>
        <#assign totalWeight=totalWeight+i.weight/>
        <tr class="item">
            <td valign="top" width="100%" align="left">
                <@bg.link.product product>${product.name}</@bg.link.product>
            </td>
            <td valign="middle" nowrap="nowrap">
            ${i.options}
            </td>
            <@bg.security.authorized "moderator">
                <td valign="middle" nowrap="nowrap">
                ${product.supplierInfo.referenceCode}
                </td>
            </@bg.security.authorized>
            <td valign="middle" nowrap="nowrap" align="center">
                <span class="itemAmount"><@bg.ui.price i.amount "b"/></span>
            </td>
            <td valign="middle" nowrap="nowrap" align="center">
            ${i.quantity}
            </td>
            <td valign="middle" align="center" nowrap="nowrap">
                <span class="itemWeight">${i.weight?string("0.00")} кг</span>
            </td>
            <td valign="middle" nowrap="nowrap" align="left">
                <span class="itemAmount"><@bg.ui.price i.amount * i.quantity "b"/></span>
            </td>
            <@bg.security.authorized "moderator">
                <td valign="middle" nowrap="nowrap" align="left">
                    <#assign amountUsd=product.supplierInfo.price.amount * i.quantity/>
                    <#assign totalAmountUsd=totalAmountUsd+amountUsd/>
                    <@bg.ui.priceU amountUsd/>
                </td>
            </@bg.security.authorized>
        </tr>
    </#list>

    <#assign colspan=3/>
    <@bg.security.authorized "moderator">
        <#assign colspan=4/>
    </@bg.security.authorized>
        <tr>
            <th colspan="${colspan}" nowrap="nowrap" align="left">Всего за товары</th>
            <th nowrap="nowrap" class="price">
                <span>${totalCount}</span>
            </th>
            <th nowrap="nowrap" class="price">
                <span>${totalWeight?string("0.00")} кг</span>
            </th>
            <th nowrap="nowrap" align="left">
            <@bg.ui.price order.amount/>
            </th>
        <@bg.security.authorized "moderator">
            <th valign="middle" nowrap="nowrap" align="left">
                <@bg.ui.priceU totalAmountUsd/>
            </th>
        </@bg.security.authorized>
        </tr>
        <tr>
            <th colspan="${colspan+2}" nowrap="nowrap" align="left">Стоимость доставки</th>
            <th nowrap="nowrap" align="left">
            <@bg.ui.price shipment.amount/>
            </th>
        <@bg.security.authorized "moderator">
            <th valign="middle" nowrap="nowrap" align="left">
            </th>
        </@bg.security.authorized>
        </tr>
        <tr>
            <th colspan="${colspan+2}" nowrap="nowrap" align="left">Итоговая сумма заказа</th>
            <th nowrap="nowrap" align="left">
            <@bg.ui.price order.amount + shipment.amount/>
            </th>
        <@bg.security.authorized "moderator">
            <th valign="middle" nowrap="nowrap" align="left">
            </th>
        </@bg.security.authorized>
        </tr>
    </table>
</div>
</div>

<script type="application/javascript">
    $("#showOrderLogs").click(function () {
        $("#orderLogs").modal({overlayClose: true, minHeight: 360, minWidth: 800, maxWidth: 800});
    });

    <#if order.payer?has_content>
    var order = new bg.warehouse.Order();
    $(".tracking button").click(function () {
        order.changeTracking(${order.id}, "${order.payer}", $(this).val() === 'true', function () {
            $(".tracking button").toggle();
        });
    });
    </#if>
</script>