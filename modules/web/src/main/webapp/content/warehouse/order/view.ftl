<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->
<#-- @ftlvariable name="confirmation" type="boolean" -->

<#include "/core.ftl"/>

<#assign state=order.orderState/>
<#assign shipment=order.shipment/>
<#assign stateName=state.code/>

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
        Заказ #${order.id} от ${messageSource.formatDate(order.created, locale)}
    </div>
    <div style="display: inline-block; float: right">
    <@bg.tracking.international order.internationalTracking/>
    </div>
</div>

<div class="steps">
<#assign steps=[OrderState.BILLING, OrderState.ACCEPTED, OrderState.PROCESSING, OrderState.SHIPPED, OrderState.CLOSED]/>

<#assign alignedState=state/>
<#if alignedState == OrderState.SHIPPING || alignedState == OrderState.SUSPENDED>
    <#assign alignedState=OrderState.PROCESSING/>
<#elseif alignedState == OrderState.FAILED || alignedState == OrderState.CANCELLED>
    <#assign alignedState=OrderState.CLOSED/>
</#if>

<#assign currentIndex=steps?seq_index_of(alignedState)/>
    <table width="100%" cellpadding="0" cellspacing="0">
        <tr>
        <#list steps as s>
            <#assign index=s_index/>
            <td class="<#if index==0>first<#elseif index==(steps?size-1)>last</#if> <#if (index==currentIndex)>current</#if> <#if (index==currentIndex-1)>previous</#if> <#if (currentIndex>index)>done</#if>"
                width="${100/(steps?size)}%">
                <@message code="order.steps.${s.code}.label"/>
            </td>
        </#list>
        </tr>
    </table>
</div>

<table class="info">
    <tr>
        <td valign="top" nowrap="nowrap">
            <label>Статус заказа:</label>
        </td>
        <td>
            <div style="display: inline-block">
                <span class="status">
                <@message code="order.status.${stateName}.label"/>
                <#if state==OrderState.SUSPENDED && order.expectedResume??>
                    до ${messageSource.formatDate(order.expectedResume, locale)}
                <#else>
                ${messageSource.formatDate(order.timestamp, locale)}
                </#if>
                </span>

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
                                <#elseif state.accepted>
                                    Номер платежа:<br>${l.parameter!""}
                                <#elseif state.processing>
                                    <#if l.parameter?has_content>
                                        Номер комплектации:<br>${l.parameter}
                                    </#if>
                                <#elseif state.shipping>
                                    <#if l.parameter?has_content>
                                        Код почты Китая:<br><@bg.tracking.china l.parameter/>
                                    </#if>
                                <#elseif state.shipped>
                                    <#if l.parameter?has_content>
                                        Международный код:<br><@bg.tracking.international l.parameter/>
                                    </#if>
                                <#elseif  state.suspended>
                                    <#if order.expectedResume??>
                                        Приостановлен до:<br>
                                    ${messageSource.formatDate(order.expectedResume, locale)}
                                    </#if>
                                <#elseif state.closed>
                                    <#if l.parameter?has_content>
                                        Дата вручения:<br>
                                    ${messageSource.formatDate(l.parameter?number?long, locale)}
                                    </#if>
                                <#elseif  state.cancelled>
                                    <#if l.parameter?has_content>
                                        Код возврата средств:<br>${l.parameter}
                                    </#if>
                                <#elseif  state.failed>
                                    Описание ошибки:<br>${l.parameter!""}
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
                Отслеживаемое отправление:
                <#if order.internationalTracking?has_content>
                    <strong><@bg.tracking.international order.internationalTracking/></strong>
                <#else>
                    номер отслеживания еще не назначен
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
        ${address.firstName} ${address.lastName}
            <br>
        ${address.postcode}, ${address.region}, ${address.city}
            <br>
        ${address.location}
        </td>
    </tr>
</table>

<#if order.payer?has_content>
    <#if !order.orderState.finalState>
    <div class="info" style="padding: 5px; text-align: right">
        <div class="operations">
            <#if order.orderState==OrderState.SHIPPED>
                <div class="confirm">
                    <form action="/warehouse/order/status" method="post">
                        <input type="hidden" name="order" value="${order.id}">
                        <input type="hidden" name="email" value="${order.payer}">

                        <button id="closeOrder" type="button">
                            Подтвердить получения заказа
                        </button>
                    </form>
                </div>
            <#elseif !order.orderState.finalState>
                <div class="tracking">
                    <button type="button" value="true" <#if order.tracking>style="display: none"</#if>>Включить
                        уведомления по e-mail
                    </button>
                    <button type="button" value="false" <#if !order.tracking>style="display: none"</#if>>
                        Отключить
                        уведомления по e-mail
                    </button>
                    <span class="sample">(${order.payer})</span>
                </div>
            </#if>
        </div>
    </div>
    </#if>
<#elseif order.orderState==OrderState.BILLING && personalityContext.hasRole("member")>
<div class="info" style="padding: 5px; text-align: right">
    <div class="confirm">
        <form action="/privacy/order" method="post">
            <input type="hidden" name="orderId" value="${order.id}"/>

            <button name="action" value="remove"
                    onclick="return confirm('Вы уверены что хотите удалить данный заказ?')">
                Удалить заказ
            </button>
        </form>
    </div>
</div>
</#if>

<div class="basket">
<#assign totalCount=0/>
<#assign totalWeight=0/>
    <table class="cnt">
        <tr>
            <th colspan="2" width="100%">Наименование</th>
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
            <td valign="top" width="50px" style="border-right: none">
                <@bg.link.product product><@bg.ui.productImage product product.previewImageId!"" ImageSize.TINY/></@bg.link.product>
            </td>
            <td valign="top" width="100%" align="left" style="border-left: none">
                <@bg.link.product product>${product.name}</@bg.link.product>
                <#if i.options??>
                    <div class="options">
                    ${i.options}
                    </div>
                </#if>
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
    <#if order.coupon?? && (order.discount>0)>
        <tr>
            <th colspan="${colspan+2}" nowrap="nowrap" align="left">Скидка по купону</th>
            <th nowrap="nowrap" align="left">
                <@bg.ui.price order.discount/>
            </th>
            <@bg.security.authorized "moderator">
                <th valign="middle" nowrap="nowrap" align="left">
                    <a href="/maintain/coupon/view?code=${order.coupon}">${order.coupon}</a>
                </th>
            </@bg.security.authorized>
        </tr>
    </#if>
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
            <@bg.ui.price order.amount + shipment.amount - order.discount/>
            </th>
        <@bg.security.authorized "moderator">
            <th valign="middle" nowrap="nowrap" align="left">
            </th>
        </@bg.security.authorized>
        </tr>
    </table>
</div>

<#if order.orderState==OrderState.BILLING && personalityContext.hasRole("member")>
<div class="paypal" style="text-align: right; padding-top: 20px">
    <form action="/privacy/order" method="post">
        <input type="hidden" name="orderId" value="${order.id}"/>
        <button type="submit" name="action" value="checkout"
                style="background: transparent; border: none">
            <img src="https://www.paypal.com/ru_RU/i/btn/btn_xpressCheckout.gif"
                 align="left">
        </button>
    </form>
</div>
</#if>
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

    $("#closeOrder").click(function () {
        var btn = $(this);
        btn.attr('disabled', 'disabled');
        order.confirmReceived(${order.id}, "${order.payer}", function (approved) {
            if (approved) {
                $(".confirm form").submit();
            } else {
                btn.removeAttr('disabled');
            }
        });
    });
    </#if>
</script>