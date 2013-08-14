<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->
<#-- @ftlvariable name="articleManager" type="billiongoods.server.warehouse.ArticleManager" -->

<#include "/core.ftl"/>

<script type="text/javascript" src="<@bg.ui.static "js/jquery.simplemodal.js"/>"></script>

<div class="order">
    <div class="tit">
        <div style="display: inline-block">
            Заказ #${order.id}
        </div>
        <div style="display: inline-block; float: right">
        <#if order.internationalTracking?has_content>
            <a href="http://gdeposylka.ru/${order.internationalTracking}?tos=accept&country=RU">${order.internationalTracking}</a>
        </#if>
        </div>
    </div>

    <table class="info">
        <tr>
            <td valign="top">
                <label>Статус заказа:</label>
            </td>
            <td>
                <div style="display: inline-block">
                ${messageSource.formatDate(order.timestamp, locale)}
                <@message code="order.status.${order.orderState.name()?lower_case}.label"/>
                    <div class="sample">
                    <@message code="order.status.${order.orderState.name()?lower_case}.description"/>
                    </div>
                </div>
                <div style="display: inline-block; float: right">
                    <a id="showOrderLogs" href="#">история обработки</a>

                    <div id="orderLogs" style="display: none">
                        <table>
                        <#list order.orderLogs as l>
                            <tr>
                                <td>${messageSource.formatTime(l.timeStamp, locale)}</td>
                                <td>${l.code}</td>
                                <td>${l.orderState}</td>
                                <td>${l.parameter}</td>
                            </tr>
                        </#list>
                        </table>
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <td valign="top">
                <label for="">Доставки заказа:</label>
            </td>
            <td>
                <div class="shipment" style="padding-bottom: 10px">
                <#if order.shipmentType==ShipmentType.FREE>
                    Бесплатная доставка без номера отслеживания
                <#elseif order.shipmentType==ShipmentType.REGISTERED>
                    <#if order.shipment == 0>
                        Бесплатная доставка с номером отслеживания
                    <#else>
                        Отслеживоемое отправление <span class="price"><@bg.ui.price order.shipment/></span>
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
                    <span class="itemAmount"><@bg.ui.price i.amount * i.quantity/></span>
                </td>
            </tr>
        </#list>
            <tr>
                <th colspan="2" nowrap="nowrap" align="left">Итого</th>
                <th nowrap="nowrap">
                ${totalCount}
                </th>
                <th nowrap="nowrap">
                ${totalWeight?string("0.00")} кг
                </th>
                <th nowrap="nowrap">
                    <div class="price"><@bg.ui.price order.amount/></div>
                </th>
            </tr>
        </table>
    </div>
</div>

<script type="application/javascript">
    $("#showOrderLogs").click(function () {
        $("#orderLogs").modal();
    });
</script>