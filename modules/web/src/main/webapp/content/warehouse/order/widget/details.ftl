<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->

<#include "/core.ftl"/>

<#macro parameter msg val><#if val?has_content>${msg}:<br>${val}<#else>&nbsp;</#if></#macro>

<table class="info">
    <tr>
        <td valign="top" nowrap="nowrap">
            <label>Статус заказа:</label>
        </td>
        <td style="position: relative">
            <div style="display: inline-block">
                <span class="status">
                <@message code="order.status.${order.state.code}.label"/>
                    <#--TODO: COMMENTED-->
                <#--
                                <#if order.state==OrderState.SUSPENDED && order.expectedResume??>
                                    до ${messageSource.formatDate(order.expectedResume, locale)}
                                <#else>
                                ${messageSource.formatDate(order.timestamp, locale)}
                                </#if>
                -->
                </span>

                <div class="sample">
                <@message code="order.status.${order.state.code}.description"/>
                </div>
            </div>
            <div style="display: inline-block; position: absolute; top: 0; right: 0; white-space: nowrap">
                <a id="showOrderLogs" href="#">история обработки</a>

                <div id="orderLogs" style="display: none">
                    <table>
                    <#list order.logs as l>
                        <tr class="order-log">
                            <td valign="top" nowrap="nowrap">${messageSource.formatDate(l.timeStamp, locale)}
                                <br>${messageSource.formatTime(l.timeStamp, locale)}
                            </td>

                            <#if l.refundChange>
                                <td valign="top">
                                    <div>
                                        Возврат денежных средств: ${l.refundAmount?string("0.00")}руб
                                    </div>
                                    <div class="sample">
                                        Мы выполнили полный либо частичный возврат денежных средств по данному заказу
                                    </div>

                                    <#if l.commentary?has_content>
                                        <div class="comment">
                                        ${l.commentary}
                                        </div>
                                    </#if>
                                </td>

                                <td valign="top" width="20%">
                                    <@parameter "Код возврата", l.parameter!"&nbsp;"/>
                                </td>
                            <#elseif l.parcelChange>
                                <#assign state=l.parcelState/>
                                <#assign stateName=state.name()?lower_case/>
                                <td valign="top">
                                    <div>
                                        <@message code="parcel.status.${stateName}.label"/> #${order.id}:${l.parcelId}
                                    </div>
                                    <div class="sample">
                                        <@message code="parcel.status.${stateName}.description"/>
                                    </div>

                                    <#if l.commentary?has_content>
                                        <div class="comment">
                                        ${l.commentary}
                                        </div>
                                    </#if>
                                </td>

                                <td valign="top" width="20%">
                                    <#switch state>
                                        <#case ParcelState.PROCESSING><@parameter "Номер посылки", "${order.id}:${l.parcelId}"/><#break/>
                                        <#case ParcelState.SHIPPING><@parameter "Код почты Китая", l.parameter!""/><#break/>
                                        <#case ParcelState.SHIPPED><@parameter "Международный код", l.parameter!""/><#break/>
                                        <#case ParcelState.SUSPENDED><@parameter "Приостановлена до", l.parameter!""/><#break/>
                                        <#case ParcelState.CLOSED><@parameter "Дата вручения", l.parameter!""/><#break/>
                                        <#case ParcelState.CANCELLED><@parameter "Код возврата средств", l.parameter!""/><#break/>
                                        <#default>&nbsp;
                                    </#switch>
                                </td>
                            <#else>
                                <#assign state=l.orderState/>
                                <#assign stateName=state.name()?lower_case/>
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
                                </td>
                                <td valign="top" width="20%">
                                    <#switch state>
                                        <#case OrderState.BILLING><@parameter "Номер счета", l.parameter!""/><#break/>
                                        <#case OrderState.ACCEPTED><@parameter "Номер платежа", l.parameter!""/><#break/>
                                        <#case OrderState.PROCESSING><@parameter "Номер комплектации", l.parameter!""/><#break/>
                                        <#case OrderState.SHIPPING><@parameter "Код почты Китая", l.parameter!""/><#break/>
                                        <#case OrderState.SHIPPED><@parameter "Международный код", l.parameter!""/><#break/>
                                        <#case OrderState.SUSPENDED><@parameter "Причина остановки", "Ожидание ответа от покупателя"/><#break/>
                                        <#case OrderState.CLOSED><@parameter "Дата вручения", l.parameter!""/><#break/>
                                        <#case OrderState.CANCELLED><@parameter "Код возврата средств", l.parameter!""/><#break/>
                                        <#case OrderState.FAILED><@parameter "Описание ошибки", l.parameter!""/><#break/>
                                        <#default>&nbsp;
                                    </#switch>
                                </td>
                            </#if>
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

<#if order.payment?has_content>
    <#assign payment=order.payment/>
    <#if payment.paymentId?has_content && payment.payer?has_content>
        <tr>
            <td valign="top" nowrap="nowrap">
                <label for="">Статус оплаты:</label>
            </td>
            <td>
                Оплачен через PayPal: ${payment.payer}
            </td>
        </tr>
    </#if>


    <#if (payment.refundAmount>0)>
        <tr>
            <td valign="top" nowrap="nowrap">
                <label for="">Возвращенная сумма:</label>
            </td>
            <td>
                <@bg.ui.price payment.refundAmount "b"/>
                <#if payment.refundId??>(код возврата ${payment.refundId})</#if>
            </td>
        </tr>
    </#if>
</#if>

    <tr>
        <td valign="top" nowrap="nowrap">
            <label for="">Способ доставки:</label>
        </td>
        <td>
            <div class="shipment">
            <#if order.shipment.type==ShipmentType.FREE>
                Бесплатная доставка без номера отслеживания
            <#elseif order.shipment.type==ShipmentType.REGISTERED>
                Отслеживаемое отправление
                <#list order.internationalTracking as t>
                    <@bg.tracking.international t/><#if t_has_next>, </#if>
                </#list>
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
