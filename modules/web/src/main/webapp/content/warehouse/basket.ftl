<#-- @ftlvariable name="basket" type="billiongoods.server.services.basket.Basket" -->
<#-- @ftlvariable name="payment" type="billiongoods.server.web.servlet.mvc.warehouse.form.PaymentInfo" -->

<#include "/core.ftl"/>

<div class="basket">
<form action="/warehouse/basket" method="post">
<table class="cnt">
<#if basket?has_content>
    <tr>
        <th></th>
        <th>Наименование</th>
        <th>Опции</th>
        <th>Количество</th>
        <th>Вес</th>
        <th>Стоимость</th>
    </tr>
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

                <div class="quantity">
                    <button class="q_down bg-ui-button" disabled="disabled" type="button"> -</button>
                    <input class="q_input" name="itemQuantities" value="${i.quantity}">
                    <button class="q_up bg-ui-button" type="button"> +</button>
                </div>
                <div class="controls">

                    <button type="button">Удалить</button>
                </div>
            </td>
            <td valign="middle" align="center" nowrap="nowrap">
            ${article.weight} кг
            </td>
            <td valign="middle" nowrap="nowrap" align="left">
                <@bg.ui.price article.price/>
            </td>
        </tr>
    </#list>
    <tr>
        <th align="right" colspan="6" class="controls">
            <button type="button">Очистить Корзину</button>
        </th>
    </tr>
<#else>
    <tr>
        <td valign="top" align="center">
            У вас в корзине нет ни одного товара.
        </td>
    </tr>
</#if>
</table>

<#if rollback??>
<div class="ui-state-error" style="margin-top: 10px; padding: 10px">
    К сожалению у нас сейчас возникла внутренняя ошибка при работе с системой PayPal. В даннай момент мы не можем
    обработать ваш заказ. Пожалуйста, попробуйте еще раз.
</div>
</#if>

<div class="unregistered" <#if payment.freeRegisteredShipment>style="display: none"</#if>>
    Вы можете получить бесплатный номер для отслеживания (зарегистрированное отправление) добавив еще товара на
    сумму
    <span class="price"><@bg.ui.price payment.amountForRegistered/></span>
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
                    <table style="width: auto">
                        <tr>
                            <td valign="top" colspan="2" style="padding-bottom: 10px">
                                <span class="tit">Способ доставки</span>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top">
                                <input id="shipmentFree" type="radio" name="shipment"
                                       <#if payment.freeRegisteredShipment>disabled="disabled"</#if>
                                       value="${ShipmentType.FREE}"
                                       <#if bg.ui.statusValue==ShipmentType.FREE>checked="checked"</#if>/>
                            </td>
                            <td valign="top">
                                <label for="shipmentFree">Почта китая (<span class="price"><span class="usd">Бесплатная доставка</span></span>
                                    за 30-40 рабочих
                                    дней)</label>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top">
                                <input id="shipmentRegistered" type="radio" name="shipment"
                                       value="${ShipmentType.REGISTERED}"
                                       <#if bg.ui.statusValue==ShipmentType.REGISTERED>checked="checked"</#if>/>
                            </td>
                            <td valign="top">
                                <label for="shipmentRegistered">Зарегистрированная посылка
                                    (<#if payment.freeRegisteredShipment>
                                    Бесплатная доставка<#else><span
                                        class="price"><@bg.ui.price 1.70/></span></#if>
                                    доставка за 30-40 рабочих
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
                                <span class="price"><@bg.ui.price payment.amount "b"/></span>
                            </td>
                        </tr>
                        <tr class="payment-shipment">
                            <td valign="top" align="right">
                                    <span>
                                        Стоимость доставки:
                                    </span>
                            </td>
                            <td>
                                <span class="price"><@bg.ui.price payment.shipment "b"/></span>
                            </td>
                        </tr>
                        <tr class="payment-total">
                            <td valign="top" align="right">
                                <span>Общая стоимость:</span>
                            </td>
                            <td>
                                <span class="price"><@bg.ui.price payment.total "r"/></span>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" valign="bottom" align="right">
                                <div class="paypal">
                                    <button type="submit" style="background: transparent; border: none">
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

</form>
</div>