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
        <#if state == OrderState.ACCEPTED>
            Ваш заказ принят в обработку. Вы можете посмотреть параметры вашего заказа ниже. Пожалуйста, если вы не
            включали
            уведомления о состоянии заказа, мы рекомендуем записать вам его номер для дальнейшего самостоятельного
            отслеживания.
        <#elseif state == OrderState.FAILED>
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

<#assign shipment=order.shipment/>

<#macro parcelTable parcel number>
    <#if !parcel?has_content>
        <#assign items=order.getItems()/>
    <#else>
        <#assign items=order.getItems(parcel)/>
    </#if>

    <tbody class="parcel <#if parcel?has_content>${parcel.state.code}</#if>">
        <#if parcel?has_content>
        <tr>
            <td colspan="5" style="border: none; padding-top: 20px"></td>
        </tr>

        <tr>
            <td colspan="5" class="info">
                <#assign stateName=parcel.state.code/>
                <div>
                    <div style="display: inline-block">
                        <b>Посылка #${order.id}:${parcel.id}</b>
                    </div>

                    <div style="float: right; white-space: nowrap">
                        <#include "widget/pstate.ftl"/>
                    </div>
                </div>
                <div>
                    <div class="sample" style="display: inline-block">
                        <@message code="parcel.status.${stateName}.description"/>
                    </div>
                    <#if parcel.state != ParcelState.CLOSED>
                        <div style="float: right">
                            <#if parcel.state == ParcelState.SHIPPED>
                                <form class="parcelCloseForm" action="/warehouse/order/close" method="post">
                                    <input type="hidden" name="order" value="${order.id}">
                                    <input type="hidden" name="email" value="${order.payment.payer}">
                                    <button name="parcel" value="${parcel.id}">&nbsp;Подтвердить получение
                                        посылки&nbsp;</button>
                                </form>
                            </#if>
                        </div>
                    </#if>
                </div>
            </td>
        </tr>
        </#if>

    <tr>
        <th colspan="2" width="100%">Наименование</th>
        <th>Цена</th>
        <th>Кол-во</th>
        <th>Итого</th>
    </tr>

        <#list items as i>
            <#assign product=i.product/>
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
        </tr>
        </#list>
    </tbody>
</#macro>

    <div class="basket">
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

            <tbody id="grandTotal">
            <tr>
                <td colspan="5" style="border: none; padding-top: 20px"></td>
            </tr>

            <tr>
                <th colspan="3" align="left">Всего за товары</th>
                <th nowrap="nowrap" class="price">
                    <span>${order.productsCount}</span>
                </th>
                <th nowrap="nowrap" align="left">
                <@bg.ui.price order.amount/>
                </th>
            </tr>
            <#if order.discount.coupon?? && (order.discount.amount>0)>
            <tr>
                <th colspan="4" nowrap="nowrap" align="left">Скидка по купону</th>
                <th nowrap="nowrap" align="left">
                    <@bg.ui.price order.discount.amount/>
                </th>
            </tr>
            </#if>
            <tr>
                <th colspan="4" align="left">Стоимость доставки</th>
                <th><@bg.ui.price shipment.amount/></th>
            </tr>
            <tr>
                <th colspan="4" align="left">Итоговая сумма заказа</th>
                <th><@bg.ui.price order.grandTotal/></th>
            </tr>
            </tbody>
        </table>
    </div>

<#if OrderStateUnion.BILLING.contains(order.state) && personalityContext.hasRole("member")>
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
    $(".parcelCloseForm").submit(function () {
        return confirm("Вы уверены, что хотите закрыть заказ? При нажатии кнопки \"Да\", вы подтверждаете получение вами заказа в полном объеме и в хорошем состоянии.");
    });
</script>