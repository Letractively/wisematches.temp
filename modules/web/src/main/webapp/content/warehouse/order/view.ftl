<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->
<#-- @ftlvariable name="confirmation" type="boolean" -->
<#-- @ftlvariable name="articleManager" type="billiongoods.server.warehouse.ArticleManager" -->

<#include "/core.ftl"/>

<#assign state=order.orderState/>
<#assign stateName=state.name()?lower_case/>

<script type="text/javascript" src="<@bg.ui.static "js/jquery.simplemodal.js"/>"></script>

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
                            <tr>
                                <td valign="top" nowrap="nowrap">${messageSource.formatDate(l.timeStamp, locale)}
                                    <br>${messageSource.formatTime(l.timeStamp, locale)}</td>
                                <td valign="top">
                                    <@message code="order.status.${stateName}.label"/>
                                    <div class="sample">
                                        <@message code="order.status.${stateName}.description"/>
                                    </div>
                                </td valign="top">
                                <td valign="top" width="20%">
                                    <#if state.billing>
                                        Номер счета:
                                    <#elseif state.accepted || state.rejected>
                                        Номер платежа:
                                    <#elseif state.processing>
                                        Номер комплектации:
                                    <#elseif state.shipping>
                                        Код почты Китая:
                                    <#elseif state.shipped>
                                        Международный код:
                                    <#elseif  state.failed>
                                        Ошибка отказа:
                                    </#if>
                                    <br>
                                ${l.parameter!""}
                                </td>
                            </tr>
                        </#list>
                        </table>
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <td valign="top" nowrap="nowrap">
                <label for="">Доставка заказа:</label>
            </td>
            <td>
                <div class="shipment" style="padding-bottom: 10px">
                <#if order.shipmentType==ShipmentType.FREE>
                    Бесплатная доставка без номера отслеживания
                <#elseif order.shipmentType==ShipmentType.REGISTERED>
                    <#if order.internationalTracking?has_content>
                        Отслеживоемое отправление: <strong><@bg.link.tracking order.internationalTracking/></strong>
                    <#else>
                        Отслеживаемое отправление: номер отслеживания еще не назначен
                    </#if>
                </#if>
                </div>

            <#assign address=order.address/>
            ${address.name}
                <br>
            ${address.streetAddress},
                <br>
            ${address.city}, ${address.region}, ${address.postalCode}
            </td>
        </tr>

    <#if order.payerNote?has_content>
        <tr>
            <td valign="top" nowrap="nowrap">
                <label for="">Ваш комментарий:</label>
            </td>
            <td>
            ${order.payerNote}
            </td>
        </tr>
    </#if>

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
                </div>
            </td>
        </tr>
    </#if>
    </table>

    <div class="basket">
    <#assign totalCount=0/>
    <#assign totalWeight=0/>
        <table class="cnt">
            <tr>
                <th width="100%">Наименование</th>
                <th>Опции</th>
                <th>Количество</th>
                <th>Вес</th>
                <th>Итого</th>
            </tr>
        <#list order.orderItems as i>
            <#assign totalCount=totalCount+i.quantity/>
            <#assign totalWeight=totalWeight+i.weight/>
            <tr class="item">
                <td valign="top" width="100%" align="left">
                    <a href="/warehouse/article/${i.article}">${i.name}</a>
                </td>
                <td valign="middle" nowrap="nowrap">
                ${i.options}
                </td>
                <td valign="middle" nowrap="nowrap" align="center">
                ${i.quantity}
                </td>
                <td valign="middle" align="center" nowrap="nowrap">
                    <span class="itemWeight">${i.weight?string("0.00")} кг</span>
                </td>
                <td valign="middle" nowrap="nowrap" align="left">
                    <span class="itemAmount"><@bg.ui.price i.amount * i.quantity "b" order.exchangeRate/></span>
                </td>
            </tr>
        </#list>
            <tr>
                <th colspan="2" nowrap="nowrap" align="left">Всего за товары</th>
                <th nowrap="nowrap">
                ${totalCount}
                </th>
                <th nowrap="nowrap">
                ${totalWeight?string("0.00")} кг
                </th>
                <th nowrap="nowrap" align="left">
                    <div class="price"><@bg.ui.price order.amount "g" order.exchangeRate/></div>
                </th>
            </tr>
            <tr>
                <th colspan="4" nowrap="nowrap" align="left">Стоимость доставки</th>
                <th nowrap="nowrap" align="left">
                    <div class="price"><@bg.ui.price order.shipment "g" order.exchangeRate/></div>
                </th>
            </tr>
            <tr>
                <th colspan="4" nowrap="nowrap" align="left">Итоговая сумма заказа</th>
                <th nowrap="nowrap" align="left">
                    <div class="price"><@bg.ui.price order.amount + order.shipment "g" order.exchangeRate/></div>
                </th>
            </tr>
        </table>
    </div>
</div>

<script type="application/javascript">
    $("#showOrderLogs").click(function () {
        $("#orderLogs").modal({overlayClose: true});
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