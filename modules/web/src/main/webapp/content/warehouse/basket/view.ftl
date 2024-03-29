<#-- @ftlvariable name="addressBook" type="billiongoods.server.services.address.AddressBook" -->
<#-- @ftlvariable name="coupon" type="billiongoods.server.services.coupon.Coupon" -->
<#-- @ftlvariable name="basket" type="billiongoods.server.services.basket.Basket" -->
<#-- @ftlvariable name="shipmentRates" type="billiongoods.server.services.payment.ShipmentRates" -->
<#-- @ftlvariable name="shipmentManager" type="billiongoods.server.services.payment.ShipmentManager" -->
<#-- @ftlvariable name="order" type="billiongoods.server.web.servlet.mvc.warehouse.form.BasketCheckoutForm" -->
<#-- @ftlvariable name="checkoutError" type="billiongoods.server.services.paypal.PayPalException" -->
<#-- @ftlvariable name="salesOperation" type="billiongoods.server.services.sales.SalesOperation" -->

<#include "/core.ftl"/>

<#macro salesWarning>
    <#if salesOperation?? && salesOperation.salesClosed>
    <div class="shipment"
         style="color: red; padding-bottom: 10px;font-style: italic; text-align: center; width: 100%; font-size: 18px">
        Внимание: отправка приостановлена
        до ${messageSource.formatDate(salesOperation.startSalesDate.toLocalDate(), locale)}
    </div>
    </#if>
</#macro>

<div class="basket">
<form id="processBasketForm" action="/warehouse/basket" method="post">
<div class="header">
<#if basket??><#assign cnt=basket.products/></#if>
    <div>
        <span>Ваша корзина покупок</span>
    <span style="color: #000000; font-weight: normal">(${cnt!"нет"}
        товар${messageSource.getCountEnding(cnt!0, locale)})</span>
    </div>
<#if basket??>
    <div>
        Ожидаемая дата отправки ${messageSource.getExceptedDeliveryDate(basket.items, locale)}
    </div>
</#if>
</div>

<#if checkoutError??>
<div class="ui-state-error" style="margin-bottom: 10px; padding: 10px">
    В данный момент платежная система PayPal не может обработать ваш
    платеж:
    <strong>${messageSource.getDefaultMessage("paypal.error." + checkoutError.code, checkoutError.fullMessage, locale)}</strong>.
</div>
</#if>

<div class="showNoItems">
<@salesWarning/>
</div>

<table class="cnt">
    <tr>
        <th>
            <input name="checkAllItems" type="checkbox" title="выделить все">
        </th>
        <th width="100%" colspan="2">Товар</th>
        <th>Наличие</th>
        <th>Стоимость</th>
        <th>Количество</th>
        <th>Итого</th>
    </tr>

    <tr class="hideNoItems" <#if basket?has_content>style="display: none"</#if>>
        <td colspan="7">
            <div class="empty">
                У вас в корзине нет ни одного товара. Посмотрите наши <a href="/warehouse/arrivals">последние
                поступления</a> что бы
                выбрать подходящую новинку для себя.
            </div>
        </td>
    </tr>

<#if basket?has_content>
    <#list basket.items as i>
        <#assign product=i.product/>
        <tr class="item">
            <td style="vertical-align: top; padding-top: 10px">
                <input name="item" type="checkbox" value="${i.number}" title="Выделить">
            </td>
            <td style="vertical-align: top">
                <@bg.ui.productImage product product.previewImageId!"" ImageSize.TINY/>
            </td>
            <td width="100%" align="left" style="vertical-align: top; padding-top: 10px">
                <@bg.link.product product>${product.name} (${messageSource.getProductCode(product)})</@bg.link.product>
                <ul class="sample" style="padding-top: 10px">
                    <#list i.options as o>
                        <li>${o.attribute.name}: ${o.value}</li>
                    </#list>
                </ul>
            </td>
            <td nowrap="nowrap" align="center">
                <#assign stockInfo=i.product.stockInfo/>
                <#if stockInfo.stockState == StockState.SOLD_OUT>
                    распродан
                <#elseif (stockInfo.shipDays<=2)>
                    в наличие
                <#elseif stockInfo.arrivalDate??>
                    ожидается<br> ${messageSource.formatDate(stockInfo.arrivalDate, locale)}
                <#else>
                    ожидается<br> через ${stockInfo.shipDays} ${messageSource.formatDays(stockInfo.shipDays, locale)}
                </#if>
            </td>
            <td nowrap="nowrap" align="right">
                <span class="itemPrice"><@bg.ui.price product.price.amount "b"/></span> x
            </td>
            <td nowrap="nowrap" align="center">
                <input type="hidden" name="itemNumbers" value="${i.number}"/>
                <input type="hidden" name="itemAmounts" value="${i.product.price.amount}"/>
                <input type="hidden" name="itemWeights" value="${i.product.weight}"/>

                <input type="hidden" name="itemDiscounts"
                       value="<#if coupon??>${coupon.getDiscount(i.product, catalog)}<#else>0</#if>"/>

                <div class="quantity">
                    <input class="q_input" name="itemQuantities" value="${i.quantity}">
                </div>
            </td>
            <td nowrap="nowrap" align="left">
                <span class="itemAmount"><@bg.ui.price product.price.amount * i.quantity "b"/></span>
            </td>
        </tr>
    </#list>
</#if>

    <tr>
        <th align="left" colspan="7" class="controls">
            <div style="display: inline-block">
                <button id="removeSelected" type="button" disabled="disabled">Удалить выбранные</button>
            </div>

            <div style="display: inline-block; padding-left: 10px">
                <div class="changeWarning" style="display: none">
                    Корзина была изменена. Вы можете <a id="saveChanges" href="#" onclick="return false">сохранить</a>
                    изменения либо <a id="revertChanges" href="#" onclick="return false">отменить</a> их.
                </div>
            </div>
        <#--<button type="submit" name="action" value="clear">Очистить Корзину</button>-->
        </th>
    </tr>
</table>

<div class="showNoItems">
<#if basket?has_content>
    <@salesWarning/>

<div class="unregistered" <#if shipmentRates.isFreeShipment(ShipmentType.REGISTERED)>style="display: none"</#if>>
    Вы можете получить бесплатный номер для отслеживания (зарегистрированное отправление) добавив еще товара на
    сумму
    <@bg.ui.price shipmentManager.freeShipmentAmount-shipmentRates.amount "b"/>
</div>

<div class="order">
<table>
<tr>
<td style="padding-right: 10px" width="50%">
    <div class="address">
        <#assign hasAddressBook=(addressBook?? &&addressBook.addresses?has_content)/>

        <div>
            <span class="tit">Адрес доставки</span>
            <@bg.ui.input path="order.selectionTab" fieldType="hidden"/>
        </div>

        <#if hasAddressBook>
            <div class="selection" <#if !order.selectionTab>style="display: none" </#if>>
                <div class="desc">
                    Выберите один из ваших адресов.
                </div>
                <table>
                    <#list addressBook.addresses as a>
                        <tr>
                            <td width="20px">
                                <input id="address${a.id}" type="radio" name="id" value="${a.id}"
                                       <#if a.id=order.id>checked="checked"</#if>
                                       style="margin-top: 4px">
                            </td>
                            <td>
                                <label for="address${a.id}" style="font-weight: normal; white-space: normal">
                                ${a.fullName}, ${a.postcode}, ${a.region}, ${a.city}, ${a.location}
                                </label>
                            </td>
                        </tr>
                    </#list>
                </table>

                <button id="createNewAddress" type="button" onclick="">Другой адрес доставки</button>
            </div>
        </#if>

        <div class="form" <#if hasAddressBook && order.selectionTab>style="display: none" </#if>>
            <div class="desc">
                Пожалуйста, все поля латинскими буквами.
            </div>
            <table style="width: auto">
                <tr>
                    <td>
                        <label for="firstName">Ваше имя и фамилия: </label>
                    </td>
                    <td style="padding-bottom: 20px">
                        <div class="name layout-line">
                            <@bg.ui.input path="order.firstName"/>
                            <@bg.ui.input path="order.lastName"/>
                        </div>
                        <div class="sample">Например: Ivan Ivanov</div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="postcode">Индекс: </label>
                    </td>
                    <td>
                        <@bg.ui.input "order.postcode"/>
                        <div class="sample">Например: 123321</div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="region">Область/Регион: </label>
                    </td>
                    <td>
                        <@bg.ui.input "order.region"/>
                        <div class="sample">Например: Leningradskaya oblast, Gatchinskii raion</div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="city">Населенный пункт: </label>
                    </td>
                    <td>
                        <@bg.ui.input "order.city"/>
                        <div class="sample">Например: Gadchinskoye</div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="location">Адрес: </label>
                    </td>
                    <td>
                        <@bg.ui.input "order.location"/>
                        <div class="sample">Например: ul. Tretiya, d. 321/98, korp. 7, kv. 654</div>
                    </td>
                </tr>

                <#if addressBook??>
                    <tr>
                        <td></td>
                        <td>
                            <@bg.ui.input path="order.remember" fieldType="checkbox" attributes="style='width: auto; vertical-align: middle'">
                                <label for="remember">сохранить в адресной книге</label>
                            </@bg.ui.input>
                        </td>
                    </tr>
                </#if>
            </table>

            <#if hasAddressBook>
                <button id="selectAddress" type="button" onclick="">Выбрать адрес</button>
            </#if>
        </div>
    </div>
</td>
<td style="padding-left: 10px; position: relative" width="50%">
    <div class="shipment">
        <@bg.ui.bind path="order.shipment"/>
        <#assign shipmentType=bg.ui.actualValue/>
        <#assign freeRegisteredShipment=shipmentRates.isFreeShipment(ShipmentType.REGISTERED)/>

        <#if (bg.ui.actualValue==ShipmentType.FREE && freeRegisteredShipment)>
            <#assign shipmentType= ShipmentType.REGISTERED/>
        </#if>
        <#assign shipmentCost=shipmentRates.getShipmentCost(shipmentType)/>
        <table style="width: auto">
            <tr>
                <td colspan="2">
                    <span class="tit">Способ доставки</span>
                </td>
            </tr>
            <tr>
                <td style="padding-top: 3px">
                    <input id="shipmentFree" type="radio" name="shipment"
                           <#if freeRegisteredShipment>disabled="disabled"</#if>
                           value="${ShipmentType.FREE}"
                           <#if shipmentType==ShipmentType.FREE>checked="checked"</#if>/>
                </td>
                <td>
                    <label for="shipmentFree">Обычная посылка (<span class="price"><span
                            class="usd">Бесплатная доставка</span></span>
                        за 30-40 рабочих
                        дней)</label>
                </td>
            </tr>
            <tr>
                <td style="padding-top: 3px">
                    <input id="shipmentRegistered" type="radio" name="shipment"
                           value="${ShipmentType.REGISTERED}"
                           <#if shipmentType==ShipmentType.REGISTERED>checked="checked"</#if>/>
                </td>
                <td>
                    <label for="shipmentRegistered">Зарегистрированная посылка
                        (<span id="freeRegisteredShipment"
                               <#if !freeRegisteredShipment>style="display: none"</#if>><span
                                class="price"><span class="usd">Бесплатная доставка</span></span></span><span
                                id="paidRegisteredShipment"
                                <#if freeRegisteredShipment>style="display: none"</#if>><@bg.ui.price shipmentRates.getShipmentCost(ShipmentType.REGISTERED)/></span>
                        за 30-40 рабочих
                        дней)</label>
                </td>
            </tr>
        </table>
    </div>

    <div class="coupon">
        <#assign discountAmount=0/>
        <#if coupon??>
            <#assign discountAmount=coupon.getDiscount(basket, catalog)/>
        </#if>
        <table style="width: auto">
            <tr>
                <td style="padding-top: 10px">
                    <span class="tit">Купон</span>
                </td>
            </tr>
            <tr>
                <td>
                    <@bg.ui.input path="order.coupon">
                        <button type="submit" name="action" value="update">Применить</button>
                    </@bg.ui.input>
                    <#if coupon??><span class="sample"><@bg.ui.coupon coupon/></span></#if>
                </td>
            </tr>
        </table>
    </div>

    <div class="total">
        <table class="payment">
            <tr class="payment-order">
                <td align="right">
                    <span>Стоимость заказа:</span>
                </td>
                <td>
                    <@bg.ui.price shipmentRates.amount "b"/>
                </td>
            </tr>
            <tr class="payment-shipment">
                <td align="right">
                    <span>Стоимость доставки:</span>
                </td>
                <td>
                    <@bg.ui.price shipmentCost "b"/>
                </td>
            </tr>
            <#if coupon??>
                <tr class="payment-discount">
                    <td align="right">
                        <span>Скидка по купону:</span>
                    </td>
                    <td>
                        <@bg.ui.price discountAmount "b"/>
                    </td>
                </tr>
            </#if>
            <tr class="payment-total">
                <td align="right">
                    <span>Общая стоимость:</span>
                </td>
                <td>
                    <@bg.ui.price shipmentRates.amount+shipmentCost-discountAmount "r"/>
                </td>
            </tr>
            <tr>
                <td colspan="2" valign="bottom" align="right">
                    <div class="paypal">
                        <button type="submit" name="action" value="checkout"
                                style="background: transparent; border: none">
                            <img src="https://www.paypal.com/ru_RU/i/btn/btn_xpressCheckout.gif"
                                 align="left">
                        </button>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</td>
</tr>
</table>
</div>
</#if>
</div>
</form>
</div>

<script type="application/javascript">
    new bg.warehouse.Basket();

    var address = $(".basket .address");
    $("#createNewAddress").click(function () {
        address.find(".selection").slideUp();
        address.find(".form").slideDown();
        address.find("#selectionTab").val('false');
    });

    $("#selectAddress").click(function () {
        address.find(".selection").slideDown();
        address.find(".form").slideUp();
        address.find("#selectionTab").val('true');
    });

    <#if salesOperation?? && salesOperation.salesClosed>
    $("#processBasketForm").submit(function () {
        return confirm("Внимание! Ваша посылка будет оформлена только после ${messageSource.formatDate(salesOperation.startSalesDate.toLocalDate(), locale)}. Желаете продолжить?");
    });
    </#if>
</script>