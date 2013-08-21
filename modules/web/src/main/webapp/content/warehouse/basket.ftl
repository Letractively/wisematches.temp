<#-- @ftlvariable name="basket" type="billiongoods.server.services.basket.Basket" -->
<#-- @ftlvariable name="shipmentRates" type="billiongoods.server.services.payment.ShipmentRates" -->

<#include "/core.ftl"/>

<div class="basket">
<form action="/warehouse/basket" method="post">
<#if rollback??>
<div class="ui-state-error" style="margin-bottom: 10px; padding: 10px">
    К сожалению у нас сейчас возникла внутренняя ошибка при работе с системой PayPal. В даннай момент мы не
    можем
    обработать ваш заказ. Пожалуйста, попробуйте еще раз.
</div>
</#if>

<table class="cnt">
    <tr>
        <th></th>
        <th width="100%">Наименование</th>
        <th>Опции</th>
        <th>Количество</th>
        <th>Вес</th>
        <th>Итого</th>
    </tr>
<#if basket?has_content>
    <#list basket.basketItems as i>
        <#assign article=i.article/>
        <tr class="item">
            <td valign="top" style="margin-right: 10px">
                <img src="<@bg.ui.articleImg article article.previewImageId!"" ImageSize.TINY/>" width="50px"
                     height="50px"/>
            </td>
            <td valign="top" width="100%" align="left">
                <@bg.link.article article>${article.name}</@bg.link.article>
            </td>
            <td valign="middle" nowrap="nowrap">
                <ul>
                    <#list i.options as o>
                        <li>${o.attribute.name}: ${o.value}</li>
                    </#list>
                </ul>
            </td>
            <td valign="middle" nowrap="nowrap" align="center">
                <input type="hidden" name="itemNumbers" value="${i.number}"/>
                <input type="hidden" name="itemAmounts" value="${i.article.price.amount}"/>
                <input type="hidden" name="itemWeights" value="${i.article.weight}"/>

                <div class="quantity">
                    <button class="q_down bg-ui-button" disabled="disabled" type="button"> -</button>
                    <input class="q_input" name="itemQuantities" value="${i.quantity}">
                    <button class="q_up bg-ui-button" type="button"> +</button>
                </div>
                <div class="controls">
                    <button class="removeItem" type="button">Удалить</button>
                </div>
            </td>
            <td valign="middle" align="center" nowrap="nowrap">
                <span class="itemWeight">${article.weight?string("0.00")} кг</span>
            </td>
            <td valign="middle" nowrap="nowrap" align="left">
                <span class="itemAmount"><@bg.ui.price article.price.amount * i.quantity "b"/></span>
            </td>
        </tr>
    </#list>
    <tr>
        <th align="right" colspan="6" class="controls">
            <div class="changeWarning">
                Корзина была изменена. Вы можете <a id="saveChanges" href="#" onclick="return false">сохранить</a>
                изменения либо <a id="revertChanges" href="#" onclick="return false">отменить</a> их.
            </div>
            <button type="submit" name="action" value="clear">Очистить Корзину</button>
        </th>
    </tr>
</table>

<div class="unregistered" <#if shipmentRates.isFreeShipment(ShipmentType.REGISTERED)>style="display: none"</#if>>
    Вы можете получить бесплатный номер для отслеживания (зарегистрированное отправление) добавив еще товара на
    сумму
    <span class="price"><@bg.ui.price 25.0-shipmentRates.amount "b"/></span>
</div>

<div class="order">
<table>
    <tr>
        <td valign="top" style="padding-right: 10px" width="50%">
            <div class="address">
                <table style="width: auto">
                    <tr>
                        <td valign="top" colspan="2" style="padding-bottom: 10px">
                            <span class="tit">Адрес доставки</span>

                            <div class="desc">
                                Пожалуйста, введите ваше имя фамилия и адрес латинскими
                                буквами.
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td valign="top">
                            <label for="name">Ваше имя и фамилия: </label>
                        </td>
                        <td valign="top" style="padding-bottom: 20px">
                            <@bg.ui.input "order.name"/>
                            <div class="sample">Например: Ivanov Ivan</div>
                        </td>
                    </tr>
                    <tr>
                        <td valign="top">
                            <label for="postalCode">Индекс: </label>
                        </td>
                        <td valign="top">
                            <@bg.ui.input "order.postalCode"/>
                            <div class="sample">Например: 123321</div>
                        </td>
                    </tr>
                    <tr>
                        <td valign="top">
                            <label for="region">Область/Регион: </label>
                        </td>
                        <td valign="top">
                            <@bg.ui.input "order.region"/>
                            <div class="sample">Например: Leningradskaya oblast, Gatchinskii raion</div>
                        </td>
                    </tr>
                    <tr>
                        <td valign="top">
                            <label for="city">Населенный пункт: </label>
                        </td>
                        <td valign="top">
                            <@bg.ui.input "order.city"/>
                            <div class="sample">Например: Gadchinskoye</div>
                        </td>
                    </tr>
                    <tr>
                        <td valign="top">
                            <label for="streetAddress">Адрес: </label>
                        </td>
                        <td valign="top">
                            <@bg.ui.input "order.streetAddress"/>
                            <div class="sample">Например: ul. Tretiya sleva, d. 321/98, korp. 7, kv. 654
                            </div>
                        </td>
                    </tr>
                </table>
            </div>

        </td>
        <td valign="top" style="padding-left: 10px; position: relative" width="50%">
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
                        <td valign="top" colspan="2" style="padding-bottom: 10px">
                            <span class="tit">Способ доставки</span>
                        </td>
                    </tr>
                    <tr>
                        <td valign="top" style="padding-top: 3px">
                            <input id="shipmentFree" type="radio" name="shipment"
                                   <#if freeRegisteredShipment>disabled="disabled"</#if>
                                   value="${ShipmentType.FREE}"
                                   <#if shipmentType==ShipmentType.FREE>checked="checked"</#if>/>
                        </td>
                        <td valign="top">
                            <label for="shipmentFree">Обычная посылка (<span class="price"><span class="usd">Бесплатная доставка</span></span>
                                за 30-40 рабочих
                                дней)</label>
                        </td>
                    </tr>
                    <tr>
                        <td valign="top" style="padding-top: 3px">
                            <input id="shipmentRegistered" type="radio" name="shipment"
                                   value="${ShipmentType.REGISTERED}"
                                   <#if shipmentType==ShipmentType.REGISTERED>checked="checked"</#if>/>
                        </td>
                        <td valign="top">
                            <label for="shipmentRegistered">Зарегистрированная посылка
                                (<span id="freeRegisteredShipment"
                                       <#if !freeRegisteredShipment>style="display: none"</#if>><span
                                        class="price"><span class="usd">Бесплатная доставка</span></span></span><span
                                        id="paidRegisteredShipment"
                                        <#if freeRegisteredShipment>style="display: none"</#if>><span
                                        class="price"><@bg.ui.price shipmentRates.getShipmentCost(ShipmentType.REGISTERED)/></span></span>
                                за 30-40 рабочих
                                дней)</label>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="notification">
                <table style="width: auto">
                    <tr>
                        <td valign="top" style="padding-bottom: 10px; padding-top: 30px">
                            <span class="tit">Извещения о заказе</span>
                        </td>
                    </tr>
                    <tr>
                        <td valign="top">
                            <@bg.ui.input path="order.notifications" fieldType="checkbox">
                                <label for="notifications">Получать извещения о статусе заказа по электронной
                                    почте,
                                    связанной с PayPal аккаунтом.</label>

                                <div class="sample">(мы оставляем за собой право связаться с вами по электронной почте
                                    в случае если у нас возникнут вопросы по заказу либо какие-либо сложности с его
                                    оформлением)
                                </div>
                            </@bg.ui.input>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="total">
                <table class="payment">
                    <tr class="payment-order">
                        <td valign="top" align="right">
                                    <span>
                                        Стоимость заказа:
                                    </span>
                        </td>
                        <td>
                            <span class="price"><@bg.ui.price shipmentRates.amount "b"/></span>
                        </td>
                    </tr>
                    <tr class="payment-shipment">
                        <td valign="top" align="right">
                                    <span>
                                        Стоимость доставки:
                                    </span>
                        </td>
                        <td>
                            <span class="price"><@bg.ui.price shipmentCost "b"/></span>
                        </td>
                    </tr>
                    <tr class="payment-total">
                        <td valign="top" align="right">
                            <span>Общая стоимость:</span>
                        </td>
                        <td>
                            <span class="price"><@bg.ui.price shipmentRates.amount+shipmentCost "r"/></span>
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
</#if>
    <tr class="empty" <#if basket?has_content>style="display: none"</#if>>
        <td colspan="6">
            <div class="empty">
                У вас в корзине нет ни одного товара. Посмотрите наши <a href="/warehouse/arrivals">последние
                поступления</a> что бы
                выбрать подходящую новинку для себя.
            </div>
        </td>
    </tr>
</table>
</div>
</form>

</div>

<script type="application/javascript">
    new bg.warehouse.Basket(${exchangeManager.exchangeRate});
</script>