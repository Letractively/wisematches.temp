<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->
<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->
<#-- @ftlvariable name="parcel" type="billiongoods.server.services.payment.Parcel" -->
<#-- @ftlvariable name="nullParcel" type="billiongoods.server.services.payment.Parcel" -->

<#include "/core.ftl"/>

<#assign shipment=order.shipment/>

<#assign grandTotalAmountUSD=0/>

<#macro parcelTable parcel number>
    <#if !parcel?has_content>
        <#assign items=order.getItems()/>
    <#else>
        <#assign items=order.getItems(parcel)/>
    </#if>

<tbody class="parcel <#if parcel?has_content>${parcel.state.code}</#if>">
    <#if parcel?has_content>
    <tr>
        <td colspan="10" style="border: none; padding-top: 20px"></td>
    </tr>

    <tr>
        <td colspan="10" class="info">
            <#assign stateName=parcel.state.code/>
            <div class="tit" style="display: inline-block">
                Посылка #${order.id}:${parcel.id}
                (<@bg.tracking.system parcel.number?string/>)
            </div>

            <div class="" style="float: right">
                <#include "/content/warehouse/order/widget/pstate.ftl"/>
            </div>
        </td>
    </tr>
    </#if>

<tr>
    <th>#</th>
    <th colspan="2" width="100%">Наименование</th>
    <th>Цена</th>
    <th>Кол-во</th>
    <th>Итого</th>
    <th colspan="2">Управление</th>
    <th colspan="2">
        <input name="checkAllItems" type="checkbox" checked="checked">
    </th>
</tr>

    <#assign totalCount=0/>
    <#list items as i>
        <#assign product=i.product/>

    <tr class="item">
        <td valign="top">
        ${i.number}
        </td>
        <td valign="top" width="50px" style="border-right: none">
            <@bg.link.product product><@bg.ui.productImage product product.previewImageId!"" ImageSize.TINY/></@bg.link.product>
        </td>
        <td valign="top" width="100%" align="left" style="border-left: none;">
            <@bg.link.product product>${messageSource.getProductCode(product)}</@bg.link.product>
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
            <input name="item" checked="checked" type="checkbox" value="${i.number}">
        </td>
    </tr>
    </#list>

<tr>
    <td colspan="10" align="right">
        <#if parcel?has_content && !parcel.state.finished>
            <div style="display: inline-block; float: left">
                <#list ParcelState.values() as s>
                    <#if parcel.state.allowed(s)>
                        <button class="manage-parcel-button" type="button" name="${s.name()?lower_case}"
                                value="${parcel.id}">${s}</button>
                    </#if>
                </#list>
            </div>
        </#if>

        <#if (!parcel?has_content && (order.state == OrderState.ACCEPTED || order.state == OrderState.SUSPENDED)) || ((items?size>1) && (parcel?has_content && !parcel.state.finished))>
            <div style="display: inline-block">
                <button class="manage-parcel-create" type="button">Разделить</button>
            </div>
        </#if>
    </td>
</tr>
</tbody>
</#macro>

<div class="order ${order.state.code}">
<#include "/content/warehouse/order/widget/title.ftl"/>
<#include "/content/warehouse/order/widget/progress.ftl"/>
<#include "/content/warehouse/order/widget/details.ftl"/>


    <div class="basket">
    <#if (order.amount+order.payment.refundAmount) != order.payment.paymentAmount>
        <div class="info" style="margin-top: 20px; padding: 5px; padding-left: 20px">
            <div style="display: inline-block">
                <strong>Сумма, необходимая к
                    возврату: <span
                            class="item"><@bg.ui.price order.payment.paymentAmount-(order.amount+order.payment.refundAmount) "b"/></span>
                </strong>
            </div>
            <div style="float: right">
                <button name="refund" class="manage-parcel-button">Оформить возврат</button>
            </div>
        </div>
    </#if>

        <table class="cnt">
        <#if order.parcels?size == 0>
            <div style="padding-top: 20px">
                <@parcelTable parcel="" number=0/>
            </div>
        <#else>
            <#list order.parcels as parcel>
                <@parcelTable parcel=parcel number=parcel_index+1/>
            </#list>
        </#if>

        <#if !order.state.finished>
            <tbody id="operations">
            <tr>
                <td colspan="10" style="border: none; padding: 20px 0 0;">
                    <div class="info" style="text-align: right; padding: 5px">
                        <div style="float: left">
                            <#if order.state == OrderState.BILLING>
                                <button name="accepted" class="manage-parcel-button" type="button">Оплачен</button>
                            </#if>

                            <button name="suspended" class="manage-parcel-button" type="button">Приостановить</button>
                            <button name="cancelled" class="manage-parcel-button" type="button">Отменить</button>
                            <#if (order.items?size >1)>
                                &nbsp;&nbsp;
                                <button id="deleteItems" type="button">Удалить товары</button>
                            </#if>
                        </div>

                        <div>
                            <form method="get" action="/maintain/order/export">
                                <button type="submit" name="order" value="${order.id}">Загрузить CSV для импорта
                                </button>
                            </form>
                        </div>
                    </div>
                </td>
            </tr>
            </tbody>
        </#if>


            <tbody id="grandTotal">
            <tr>
                <td colspan="10" style="border: none; padding-top: 20px"></td>
            </tr>

            <tr>
                <th colspan="4" align="left">Всего за товары</th>
                <th nowrap="nowrap" class="price">
                    <span>${order.productsCount}</span>
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
            <#if order.discount.coupon?? && (order.discount.amount>0)>
            <tr>
                <th colspan="4" nowrap="nowrap" align="left">Скидка по купону</th>
                <th nowrap="nowrap" align="left">
                    <@bg.ui.price order.discount.amount/>
                </th>
                <th valign="middle" nowrap="nowrap" align="left" colspan="3">
                    <a href="/maintain/coupon/view?code=${order.discount.coupon}">${order.discount.coupon}</a>
                </th>
            </tr>
            </#if>
            <tr>
                <th colspan="5" align="left">Стоимость доставки</th>
                <th><@bg.ui.price shipment.amount/></th>
                <th colspan="3">&nbsp;</th>
            </tr>
            <tr>
                <th colspan="5" align="left">Итоговая сумма заказа</th>
                <th><@bg.ui.price order.grandTotal/></th>
                <th colspan="3">&nbsp;</th>
            </tr>
            </tbody>
        </table>
    </div>
</div>

<form id="removeItemsForm" action="/maintain/order/modify" method="post" style="display: none">
    <input type="hidden" name="orderId" value="${order.id}"/>

    <div id="removeItemsPanel">
    </div>
</form>

<form id="promoteParcelForm" action="/maintain/order/action" method="post" style="display: none">
    <input type="hidden" name="orderId" value="${order.id}"/>
    <input type="hidden" name="parcelId" value=""/>
    <input type="hidden" name="state" value=""/>
    <input type="hidden" name="value" value=""/>

    <table>
        <tr>
            <td valign="top">
                <div id="refundForm" style="display: none">
                    <div>
                        <div><label for="refundToken">Уникальный код:</label></div>
                        <div><input id="refundToken"></div>
                    </div>
                    <div>
                        <div><label for="refundAmount">Сумма возврата:</label></div>
                        <div><input
                                id="refundAmount"
                                value="${order.payment.paymentAmount-(order.amount+order.payment.refundAmount)}">
                        </div>
                    </div>
                </div>

                <div id="shippingForm" style="display: none">
                    <div><label for="chinaMailTracking">Номер:</label></div>
                    <div><input id="chinaMailTracking"></div>
                </div>

                <div id="shippedForm" style="display: none">
                    <div><label for="internationalTracking">Номер:</label></div>
                    <div><input id="internationalTracking"></div>
                </div>

                <div id="cancelledForm" style="display: none">
                    <div><label for="refundParcelToken">Номер возврата:</label></div>
                    <div><input id="refundParcelToken"></div>
                </div>

                <div id="suspendedForm" style="display: none">
                    <div>
                        <div><label for="expectedParcelResume">Дата возобновления:</div>
                        <div>
                            <input id="expectedParcelResume" class="datepicker"/>
                        </div>
                    </div>
                </div>

                <div id="closedForm" style="display: none">
                    <div>
                        <div><label for="closeParcelDate">Дата получения:</div>
                        <div>
                            <input id="closeParcelDate" class="datepicker"/>
                        </div>
                    </div>
                </div>
            </td>
            <td valign="top" width="60%">
                <div>
                    <div>Коментарий:</div>
                    <div>
                        <textarea name="commentary" rows="12" cols="45"></textarea>
                    </div>
                </div>
            </td>
        </tr>
    </table>
</form>

<script type="application/javascript">
    var order = new bg.warehouse.Order();

    function getSelectedProducts(root) {
        var items = [];
        root.closest("tbody").find("input[name='item']:checked").each(function (index, item) {
            items.push($(item).val());
        });
        return items;
    }

    function getParcelManageValue(parcel, state, form) {
        switch (state) {
            case 'shipping':
                return form.find("#chinaMailTracking").val();
            case 'shipped':
                return form.find("#internationalTracking").val();
            case 'suspended':
                return form.find("#expectedParcelResume").val();
            case 'cancelled':
                return form.find("#refundParcelToken").val();
            case 'closed':
                return form.find("#closeParcelDate").val();
            case 'refund':
                return form.find("#refundAmount").val() + ":" + form.find("#refundToken").val();
        }
        return null;
    }

    $(".manage-parcel-create").click(function () {
        var number = prompt("Введите номер посылки", "");
        if (number === null) {
            return;
        }

        var items = getSelectedProducts($(this));
        order.createParcel(${order.id}, number, items, function (parcel) {
            bg.util.url.reload();
        });
    });

    $(".manage-parcel-button").click(function () {
        var button = $(this);
        var name = button.attr('name');
        var parcel = button.val();

        var form = $("#promoteParcelForm");
        var localForm = $("#" + name + "Form").show();
        var dialog = form.dialog({
            title: name,
            autoOpen: true,
            height: 360,
            width: 660,
            modal: true,
            resizable: false,
            buttons: {
                "Применить": function () {
                    form.find("input[name='parcelId']").val(parcel);
                    form.find("input[name='state']").val(name.toUpperCase());
                    form.find("input[name='value']").val(getParcelManageValue(parcel, name, form));

                    form.submit();
                    localForm.hide();
                },
                "Отменить": function () {
                    dialog.dialog("close");
                    localForm.hide();
                }
            }
        });
    });

    $("input[name='checkAllItems']").click(function () {
        var t = $(this);
        t.closest("tbody").find("input[name='item']").prop('checked', t.is(':checked'));
    });

    $("#deleteItems").click(function () {
        var items = getSelectedProducts($(".parcel"));
        order.removeProducts(${order.id}, items, function () {
            bg.util.url.reload();
        });
    });

    $(".datepicker").datepicker({"dateFormat": "yy-mm-dd"});
</script>