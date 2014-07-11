<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->

<#include "/core.ftl"/>

<#assign state=order.orderState/>

<table class="info">
    <tr>
        <td valign="top" nowrap="nowrap">
            <label>Статус заказа:</label>
        </td>
        <td style="position: relative">
            <div style="display: inline-block">
                <span class="status">
                <@message code="order.status.${state.code}.label"/>
                <#if order.orderState==OrderState.SUSPENDED && order.expectedResume??>
                    до ${messageSource.formatDate(order.expectedResume, locale)}
                <#else>
                ${messageSource.formatDate(order.timestamp, locale)}
                </#if>
                </span>

                <div class="sample">
                <@message code="order.status.${state.code}.description"/>
                </div>
            </div>
            <div style="display: inline-block; position: absolute; top: 0; right: 0; white-space: nowrap">
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
            Оплачен через PayPal
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
            <#if order.shipment.type==ShipmentType.FREE>
                Бесплатная доставка без номера отслеживания
            <#elseif order.shipment.type==ShipmentType.REGISTERED>
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
        <#assign address=order.shipment.address/>
        ${address.fullName}
            <br>
        ${address.postcode}, ${address.region}, ${address.city}
            <br>
        ${address.location}
        </td>
    </tr>
</table>

<script type="application/javascript">
    $("#showOrderLogs").click(function () {
        $("#orderLogs").modal({overlayClose: true, minHeight: 360, minWidth: 800, maxWidth: 800});
    });
</script>
