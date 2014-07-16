<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->
<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->
<#-- @ftlvariable name="parcel" type="billiongoods.server.services.payment.Parcel" -->
<#-- @ftlvariable name="nullParcel" type="billiongoods.server.services.payment.Parcel" -->

<#include "/core.ftl"/>

<#assign shipment=order.shipment/>

<#assign grandTotalCount=0/>
<#assign grandTotalAmountUSD=0/>

<#macro parcelTable parcel number>
    <#if parcel?has_content>
        <#assign items=order.getItems(parcel)/>
    <#else>
        <#assign items=order.getItems(nullParcel)/>
    </#if>

    <#if !items?has_content>
        <#return/>
    </#if>

<tbody>
    <#if parcel?has_content>
    <tr>
        <td colspan="9" style="border: none; padding-top: 20px"></td>
    </tr>

    <tr>
        <td colspan="9" class="info">
            <#assign stateName=parcel.state.code/>
            <div class="tit" style="display: inline-block">
                Посылка №${number}
                (<@bg.tracking.system parcel.number?string/>)
            </div>

            <div class="" style="float: right">
                <span class="status">
                <@message code="order.status.${stateName}.label"/>
                    <#if order.state==ParcelState.SUSPENDED && parcel.expectedResume??>
                        до ${messageSource.formatDate(parcel.expectedResume, locale)}
                    <#else>
                    ${messageSource.formatDate(parcel.timestamp, locale)}
                    </#if>
                </span>
            </div>
        </td>
    </tr>
    </#if>

<tr>
    <th colspan="2" width="100%">Наименование</th>
    <th>Цена</th>
    <th>Кол-во</th>
    <th>Итого</th>
    <th colspan="3">Управление</th>
</tr>

    <#assign totalCount=0/>
    <#list items as i>
        <#assign product=i.product/>

        <#assign totalCount=totalCount+i.quantity/>
        <#assign grandTotalCount=grandTotalCount+i.quantity/>
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
        <td valign="middle" nowrap="nowrap" align="center">
            <span class="itemAmount"><@bg.ui.price i.amount "b"/></span>
        </td>
        <td valign="middle" nowrap="nowrap" align="center">
        ${i.quantity}
            <#--<input value="${i.quantity}">-->
        </td>
        <td valign="middle" nowrap="nowrap" align="left">
            <span class="itemAmount"><@bg.ui.price i.amount * i.quantity "b"/></span>
        </td>

        <td valign="middle" nowrap="nowrap">
            <a href="${product.supplierInfo.referenceUrl.toString()}"
               target="_blank">${product.supplierInfo.referenceCode}</a>
        </td>
        <td valign="middle" nowrap="nowrap" align="left">
            <#assign amountUsd=product.supplierInfo.price.amount * i.quantity/>
                    <#assign grandTotalAmountUSD=grandTotalAmountUSD+amountUsd/>
                    <@bg.ui.priceU amountUsd/>
        </td>
        <td>
            <input checked="checked" type="checkbox" value="${product.id}">
        </td>
    </tr>
    </#list>

<tr>
    <td colspan="9" align="right">
        <#if parcel?has_content>
            <div style="display: inline-block; float: left">
                <button class="manage-parcel-shipping" type="button" value="${parcel.number}">Отправление</button>
                <button class="manage-parcel-shipped" type="button" value="${parcel.number}">Отправлена</button>
                <button class="manage-parcel-suspend" type="button" value="${parcel.number}">Приостановить</button>
                <button class="manage-parcel-cancelled" type="button" value="${parcel.number}">Отменить</button>
                <button class="manage-parcel-close" type="button" value="${parcel.number}">Закрыть</button>
            </div>
        </#if>

        <div style="display: inline-block">
            <button class="manage-parcel-create" type="button">Разделить</button>
        </div>
    </td>
</tr>
</tbody>
</#macro>


<div class="order ${order.state.code}">
<#include "/content/warehouse/order/widget/title.ftl"/>
<#include "/content/warehouse/order/widget/progress.ftl"/>
<#include "/content/warehouse/order/widget/details.ftl"/>

    <div class="info" style="padding: 5px; text-align: right; margin-top: 20px; margin-bottom: 20px">
    <#if order.state == OrderState.ACCEPTED>
        <div style="float: left">
            <button id="suspendOrder" type="button">Приостановить</button>
            <button id="cancelOrder" type="button">Отменить</button>
        </div>
    </#if>
        <div>
            <form method="get" action="/maintain/order/export">
                <button type="submit" name="order" value="${order.id}">Загрузить CSV для импорта</button>
            </form>
        </div>
    </div>

    <div class="basket">
        <table class="cnt">
        <@parcelTable parcel="" number=0/>

        <#list order.parcels as parcel>
            <@parcelTable parcel=parcel number=parcel_index+1/>
        </#list>

            <tbody id="grandTotal">
            <tr>
                <td colspan="9" style="border: none; padding-top: 20px"></td>
            </tr>

            <tr>
                <th colspan="3" align="left">Всего за товары</th>
                <th nowrap="nowrap" class="price">
                    <span>${grandTotalCount}</span>
                </th>
                <th nowrap="nowrap" align="left">
                <@bg.ui.price order.amount/>
                </th>
                <th valign="middle" nowrap="nowrap" align="left">

                </th>
                <th valign="middle" nowrap="nowrap" align="left">
                <@bg.ui.priceU grandTotalAmountUSD/>
                </th>
                <th valign="middle" nowrap="nowrap" align="left">&nbsp;</th>
            </tr>
            <#if order.coupon?? && (order.discount>0)>
            <tr>
                <th colspan="3" nowrap="nowrap" align="left">Скидка по купону</th>
                <th nowrap="nowrap" align="left">
                    <@bg.ui.price order.discount/>
                </th>
                <th valign="middle" nowrap="nowrap" align="left" colspan="3">
                    <a href="/maintain/coupon/view?code=${order.coupon}">${order.coupon}</a>
                </th>
            </tr>
            </#if>
            <tr>
                <th colspan="4" align="left">Стоимость доставки</th>
                <th><@bg.ui.price shipment.amount/></th>
                <th colspan="3">&nbsp;</th>
            </tr>
            <tr>
                <th colspan="4" align="left">Итоговая сумма заказа</th>
                <th><@bg.ui.price order.amount + shipment.amount - order.discount/></th>
                <th colspan="3">&nbsp;</th>
            </tr>
            </tbody>
        </table>
    </div>
</div>

<div id="shippingForm" style="display: none">
    <form action="/maintain/order/promoteParcel" method="post">
        <table>
            <tr>
                <th><label for="chinaMailTracking">Номер:</label></th>
                <td>
                    <input id="chinaMailTracking" name="value">
                </td>
            </tr>
            <tr>
                <th>Коментарий:</th>
                <td>
                    <textarea name="commentary" rows="12" cols="50"></textarea>
                </td>
            </tr>
        </table>
        <input type="hidden" name="order" value="${order.id}"/>
        <input type="hidden" name="number" value=""/>
        <input type="hidden" name="state" value=""/>
    </form>
</div>

<div id="suspendForm" style="display: none">
    <div style="display: inline-block; vertical-align: top">
        <div>Дата возобновления:</div>
        <div class="datepicker"></div>
    </div>
    <div style="display: inline-block; vertical-align: top">
        Коментарий:<br>
    </div>
</div>

<script type="application/javascript">
    function suspendOrder() {
        alert("asd");
    }

    function suspendParcel() {
    }

    function shippingParcel(parcel, form) {
        form.find("input[name='number']").val(parcel);
        form.find("input[name='state']").val("${ParcelState.SHIPPING.name()}");

        form.find("form").submit();
    }

    function showDialog(id, funct) {
        var opts = {
            suspend: {title: "Приостановить", form: "#suspendForm", height: 350, width: 660},
            shipping: {title: "Отправление", form: "#shippingForm", height: 350, width: 600}
        };

        return function () {
            var val = $(this).val();

            var caption = opts[id].caption;
            var form = $(opts[id].form);
            var dialog = form.dialog({
                title: opts[id].title,
                autoOpen: true,
                height: opts[id].height,
                width: opts[id].width,
                modal: true,
                resizable: false,
                buttons: {
                    "Применить": function () {
                        funct(val, form)
                    },
                    "Отменить": function () {
                        dialog.dialog("close");
                    }
                }
            });
        }
    }

    /*
        <button class="manage-parcel-shipping" type="button">Отправление</button>
        <button class="manage-parcel-shipped" type="button">Отправлена</button>
        <button class="manage-parcel-suspend" type="button">Приостановить</button>
        <button class="manage-parcel-cancelled" type="button">Отменить</button>
        <button class="manage-parcel-close" type="button">Закрыть</button>
    */


    $("#suspendOrder").click(showDialog("suspend", suspendOrder));

    $(".manage-parcel-shipping").click(showDialog("shipping", shippingParcel));

    $(".manage-parcel-suspend").click(showDialog("suspend", suspendParcel));
    /*
        function updateParcel() {
            alert("update");
        }

        var dialog = $("#parcelEditForm").dialog({
            autoOpen: false,
            height: 300,
            width: 350,
            modal: true,
            buttons: {
                "Изменить": updateParcel,
                "Отменить": function () {
                    dialog.dialog("close");
                }
            },
            close: function () {
    //            form[ 0 ].reset();
    //            allFields.removeClass("ui-state-error");
            }
        });
    */

    $(".manage-parcel-edit").click(function () {
        // TODO: fill fields here
        dialog.dialog("open");
    });

    $(".manage-parcel-create").click(function () {
        var number = prompt("Введите номер посылки", "");
        if (number === null) {
            return;
        }

        var items = [];
        $(this).closest("tbody").find("input:checked").each(function (index, item) {
            items.push($(item).val());
        });

        order.createParcel(${order.id}, number, items, function (parcel) {
            bg.util.url.reload();
        });
    });

    $(".datepicker").datepicker({"dateFormat": "yy.mm.dd"});
</script>


<#--
<#if order.orderState!=OrderState.CLOSED>
<div class="order">
    <form action="/maintain/order/promote" method="post">
        <input name="id" type="hidden" value="${order.id}">

        <div class="info">
            <#if order.orderState==OrderState.ACCEPTED>
                Внутренний номер поставщика:
            <#elseif order.orderState==OrderState.PROCESSING>
                Номер доставки почты Китая:
            <#elseif order.orderState==OrderState.SHIPPING>
                Международный номер доставки (если есть):
            <#elseif order.orderState==OrderState.SHIPPED>
                Дата вручения (yyyy.MM.dd):
            <#else>
                Внутренний номер поставщика/Номер доставки почты Китая/Международный номер доставки:
            </#if>
            <@bg.ui.input path="form.value" fieldType="text"/>

            Коментарий:
            <@bg.ui.field path="form.commentary">
                <textarea rows="2" style="width: 100%"
                          name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
            </@bg.ui.field>

            <#if order.orderState==OrderState.ACCEPTED || order.orderState==OrderState.SUSPENDED>
                <button type="submit" name="state" value="${OrderState.PROCESSING.name()}">
                    Перевести в статус "Обработка" (PROCESSING)
                </button>
            </#if>
            <#if order.orderState==OrderState.PROCESSING || order.orderState==OrderState.SUSPENDED>
                <button type="submit" name="state" value="${OrderState.SHIPPING.name()}">
                    Перевести в статус "Доставка" (SHIPPING)
                </button>
            </#if>
            <#if order.orderState==OrderState.PROCESSING || order.orderState==OrderState.SHIPPING || order.orderState==OrderState.SUSPENDED>
                <button type="submit" name="state" value="${OrderState.SHIPPED.name()}">
                    Перевести в статус "Отправлено" (SHIPPED)
                </button>
            </#if>
            <#if order.orderState==OrderState.SHIPPED>
                <button type="submit" name="state" value="${OrderState.CLOSED.name()}">
                    Перевести в статус "Завершено" (CLOSED)
                </button>
            </#if>

            <#if order.orderState==OrderState.PROCESSING || order.orderState == OrderState.ACCEPTED || order.orderState == OrderState.SUSPENDED>
                <div id="extendedOptions">
                    <input id="allowExtendedOperations" type="checkbox">
                    <button type="submit" name="state" value="${OrderState.SUSPENDED.name()}" disabled="disabled">
                        Перевести в статус "Приостановлено" (SUSPENDED)
                    </button>
                    <button type="submit" name="state" value="${OrderState.CANCELLED.name()}" disabled="disabled">
                        Перевести в статус "Отменен" (CANCELLED)
                    </button>
                </div>
            </#if>
        </div>
    </form>
</div>

<script type="text/javascript">
    $("#allowExtendedOperations").change(function () {
        $("#extendedOptions").find("button").prop('disabled', !$(this).prop('checked'));
    });

        <#if order.orderState==OrderState.SHIPPED>
        $("#value").datepicker({ "dateFormat": "yy.mm.dd"});
        </#if>
</script>
</#if>
-->