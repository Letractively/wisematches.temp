<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->
<#-- @ftlvariable name="confirmation" type="boolean" -->
<#-- @ftlvariable name="parcel" type="billiongoods.server.services.payment.Parcel" -->
<#-- @ftlvariable name="nullParcel" type="billiongoods.server.services.payment.Parcel" -->

<#include "/core.ftl"/>

<#assign state=order.state/>
<#assign shipment=order.shipment/>

<div class="order ${state.code}">
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

<#include "/content/warehouse/order/widget/title.ftl"/>

<#include "/content/warehouse/order/widget/progress.ftl"/>

<#include "/content/warehouse/order/widget/details.ftl"/>

<#if order.payer?has_content &&  !order.state.finalState>
<div class="info" style="padding: 5px; text-align: right; margin: 20px 0 0;">
    <@bg.security.authorized "moderator">
        <div style="float: left">
            <button type="button" onclick="bg.util.url.redirect('/maintain/order/view?id=${order.id}&type=id')">
                Редактировать
            </button>
        </div>
    </@bg.security.authorized>
    <div class="operations">
        <#if order.state==OrderState.SHIPPED>
            <div class="confirm">
                <form action="/warehouse/order/status" method="post">
                    <input type="hidden" name="order" value="${order.id}">
                    <input type="hidden" name="email" value="${order.payer}">

                    <button id="closeOrder" type="button">
                        Подтвердить получения заказа
                    </button>
                </form>
            </div>
        <#elseif !order.state.finalState>
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

<#--
<#assign grandTotalCount=0/>
<#assign grandTotalWeight=0/>
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
        <td colspan="6" style="border: none; padding-top: 20px"></td>
    </tr>

    <tr>
        <td colspan="6" class="info">
            <#assign stateName=parcel.state.code/>
            <div>
                <div class="tit" style="display: inline-block">
                    Посылка №${number}
                </div>

                <div class="" style="float: right">
                <span class="status">
                <@message code="order.status.${stateName}.label"/>
                    <#if state==ParcelState.SUSPENDED && parcel.expectedResume??>
                        до ${messageSource.formatDate(parcel.expectedResume, locale)}
                    <#else>
                    ${messageSource.formatDate(parcel.timestamp, locale)}
                    </#if>
                </span>
                </div>
            </div>

            <div style="padding-top: 5px">
                <div class="sample">
                    <@message code="order.status.${stateName}.description"/>
                </div>
            </div>
        </td>
    </tr>
    </#if>

<tr>
    <th colspan="2" width="100%">Наименование</th>
    <th>Цена</th>
    <th>Количество</th>
    <th>Вес</th>
    <th>Итого</th>
</tr>

    <#assign totalCount=0/>
    <#assign totalWeight=0/>
    <#list items as i>
        <#assign product=i.product/>

        <#assign totalCount=totalCount+i.quantity/>
        <#assign totalWeight=totalWeight+i.weight/>
        <#assign grandTotalCount=grandTotalCount+i.quantity/>
        <#assign grandTotalWeight=grandTotalWeight+i.weight/>
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
        </td>
        <td valign="middle" align="center" nowrap="nowrap">
            <span class="itemWeight">${i.weight?string("0.00")} кг</span>
        </td>
        <td valign="middle" nowrap="nowrap" align="left">
            <span class="itemAmount"><@bg.ui.price i.amount * i.quantity "b"/></span>
        </td>
    </tr>
    </#list>
</tbody>
</#macro>

<div class="basket">
    <table class="cnt">
    <@parcelTable parcel="" number=0/>

    <#list order.orderParcels as parcel>
        <@parcelTable parcel=parcel number=parcel_index+1/>
    </#list>

        <tbody id="grandTotal">
        <tr>
            <td colspan="6" style="border: none; padding-top: 20px"></td>
        </tr>

        <tr>
            <th colspan="3" align="left">Всего за товары</th>
            <th nowrap="nowrap" class="price">
                <span>${grandTotalCount}</span>
            </th>
            <th nowrap="nowrap" class="price">
                <span>${grandTotalWeight?string("0.00")} кг</span>
            </th>
            <th nowrap="nowrap" align="left">
            <@bg.ui.price order.amount/>
            </th>
        </tr>

        <#if order.coupon?? && (order.discount>0)>
        <tr>
            <th colspan="3" nowrap="nowrap" align="left">Скидка по купону</th>
            <th nowrap="nowrap" align="left">
                <@bg.ui.price order.discount/>
            </th>
        </tr>
        </#if>

        <tr>
            <th colspan="5" align="left">Стоимость доставки</th>
            <th><@bg.ui.price shipment.amount/></th>
        </tr>
        <tr>
            <th colspan="5" align="left">Итоговая сумма заказа</th>
            <th><@bg.ui.price order.amount + shipment.amount - order.discount/></th>
        </tr>
        </tbody>
    </table>
</div>-->

<div class="basket" style="margin-top: 20px">
<#assign totalCount=0/>
<#assign totalWeight=0/>
    <table class="cnt">
        <tr>
            <th colspan="2" width="100%">Наименование</th>
            <th>Цена</th>
            <th>Количество</th>
            <th>Вес</th>
            <th>Итого</th>
        </tr>

    <#assign grandTotalAmountUSD=0/>
    <#list order.items as i>
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
        </tr>
    </#list>

        <tr>
            <th colspan="3" nowrap="nowrap" align="left">Всего за товары</th>
            <th nowrap="nowrap" class="price">
                <span>${totalCount}</span>
            </th>
            <th nowrap="nowrap" class="price">
                <span>${totalWeight?string("0.00")} кг</span>
            </th>
            <th nowrap="nowrap" align="left">
            <@bg.ui.price order.amount/>
            </th>
        </tr>
    <#if order.coupon?? && (order.discount>0)>
        <tr>
            <th colspan="5" nowrap="nowrap" align="left">Скидка по купону</th>
            <th nowrap="nowrap" align="left">
                <@bg.ui.price order.discount/>
            </th>
        </tr>
    </#if>
        <tr>
            <th colspan="5" nowrap="nowrap" align="left">Стоимость доставки</th>
            <th nowrap="nowrap" align="left">
            <@bg.ui.price shipment.amount/>
            </th>
        </tr>
        <tr>
            <th colspan="5" nowrap="nowrap" align="left">Итоговая сумма заказа</th>
            <th nowrap="nowrap" align="left">
            <@bg.ui.price order.amount + shipment.amount - order.discount/>
            </th>
        </tr>
    </table>
</div>

<#if order.state==OrderState.BILLING && personalityContext.hasRole("member")>
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