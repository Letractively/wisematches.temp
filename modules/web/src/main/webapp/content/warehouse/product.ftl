<#-- @ftlvariable name="product" type="billiongoods.server.warehouse.Product" -->
<#-- @ftlvariable name="mode" type="billiongoods.server.warehouse.ProductPreview[]" -->
<#-- @ftlvariable name="groups" type="billiongoods.server.warehouse.Group[]" -->
<#-- @ftlvariable name="similar" type="billiongoods.server.warehouse.ProductPreview[]" -->
<#-- @ftlvariable name="accessories" type="billiongoods.server.warehouse.ProductPreview[]" -->
<#-- @ftlvariable name="relationships" type="billiongoods.server.warehouse.Relationship[]" -->
<#-- @ftlvariable name="registeredTracking" type="java.util.EnumSet<billiongoods.server.services.tracking.TrackingType>" -->

<#include "/core.ftl">

<script type="text/javascript" src="<@bg.ui.static "js/jquery.sly-1.0.2.min.js"/>"></script>

<link rel="stylesheet" href="<@bg.ui.static "css/jquery.prettyPhoto-3.1.5.css"/>" type="text/css" charset="utf-8"/>
<script type="text/javascript" src="<@bg.ui.static "js/jquery.prettyPhoto-3.1.5.js"/>"></script>

<#assign sku=messageSource.getProductCode(product)/>
<#assign stockInfo=product.stockInfo/>

<div class="product ${product.state.name()?lower_case}" itemscope itemtype="http://schema.org/Product">
<meta itemprop="url" content="http://www.billiongoods.ru/warehouse/product/${product.symbolicUri}"/>
<meta itemprop="productID" content="${product.id}"/>
<meta itemprop="releaseDate" content="${product.registrationDate?date?string("yyyy-MM-dd")}"/>
<link itemprop="itemCondition" href="http://schema.org/NewCondition"/>

<table>
<tr>
<td valign="top" width="176px">
    <div class="view">
        <div class="preview">
        <@bg.ui.productImage product product.previewImageId!"" ImageSize.MEDIUM {"itemprop":"image"}/>
                <@bg.ui.discountDiv product/>
        </div>

        <div class="thumb">
        <#list product.imageIds as i>
            <#if (i==product.previewImageId)><#assign class="selected"/><#else><#assign class=""/></#if>
            <#assign viewURL><@bg.ui.productImageUrl product i ImageSize.LARGE/></#assign>
            <div>
                <@bg.ui.productImage product i ImageSize.TINY {"class":"${class}", "page":"${i_index}", "view":"${viewURL}"}/>
            </div>
        </#list>
        </div>
    </div>
    bang
</td>
<td valign="top" width="100%">
    <div class="info">
        <div class="name" itemprop="name">
        ${product.name}
        </div>

        <div class="articular">
            Артикул: <span class="sku" itemprop="sku">${sku}</span>
        <@bg.security.authorized "moderator">
            (<a href="${product.supplierInfo.referenceUrl.toExternalForm()}"
                target="_blank">${product.supplierInfo.referenceCode}</a>)
        </@bg.security.authorized>
        <#if (product.soldCount>0)>
            Продано: <span class="sold">${product.soldCount}</span>
        </#if>
        <@bg.security.authorized "moderator">
            <div style="float: right">
                <button type="button"
                        onclick="bg.warehouse.Maintain.recommend(${product.id}, ${(!product.recommended)?string})">
                    <#if product.recommended>Не рекомендовать<#else>Рекомендовать</#if>
                </button>
                <button type="button" onclick="bg.warehouse.Maintain.editProduct(${product.id})">Изменить
                </button>
            </div>
        </@bg.security.authorized>
        </div>
    <@bg.security.authorized "moderator">
        <div align="right">
            <#list groups as g>
                <a href="/maintain/group?id=${g.id}">#${g.id} ${g.name} (${g.type})</a>
            </#list>
            <br>
            <#list relationships as r>
                <#assign g=r.group/>
                <a href="/maintain/group?id=${g.id}">#${g.id} ${g.name} (${r.type})</a>
            </#list>
        </div>
    </@bg.security.authorized>

        <div class="stock">
            <div class="ability">
            <#switch stockInfo.stockState>
                <#case StockState.IN_STOCK>
                    В наличии, обычно отправлается в течении 2-3 рабочих дней
                    <#break/>
                <#case StockState.LIMITED_NUMBER>
                    Торопитесь, осталось всего ${stockInfo.leftovers} штук!
                    <#break/>
                <#case StockState.SOLD_OUT>
                    Товар распродан
                    <#break/>
                <#case StockState.OUT_STOCK>
                    Нет на складе. Поступление
                    ожидается ${messageSource.formatDate(stockInfo.restockDate, locale)}
                    <#break/>
            </#switch>
            </div>
            <div class="shipment">
                Бесплатная доставка за 30-40 дней
            </div>
        </div>

        <div class="props">
            <table>
                <tr>
                    <td>Вес</td>
                    <td>${product.weight?string("0.00")} кг</td>
                </tr>
            <#if product.properties?has_content>
                <#list product.properties as p>
                    <#if p.value?has_content>
                        <tr>
                            <td>${p.attribute.name}</td>
                            <td>${messageSource.formatPropertyValue(p.value, locale)} ${p.attribute.unit}</td>
                        </tr>
                    </#if>
                </#list>
            </#if>
            </table>
        </div>

        <div style="text-align: right; width: 100%">
            <a href="#description">Описание</a>
        <#if accessories?has_content>| <a href="#accessories">Запасные части</a></#if>
        <#if mode?has_content>| <a href="#mode">Модификации</a></#if>
        <#if similar?has_content>| <a href="#similar">Похожие продукты</a></#if>
        </div>

        <form id="shoppingForm" itemprop="offers" itemscope itemtype="http://schema.org/Offer">
            <meta itemprop="deliveryLeadTime" content="30 days"/>
            <input type="hidden" name="product" value="${product.id}"/>

            <div class="panel">
                <div class="cost">
                    <span><strong>Стоимость:</strong></span>

                <@bg.ui.price product.price.amount/>
                <#if product.price.primordialAmount??><span
                        class="primordial">(<@bg.ui.price product.price.primordialAmount "g"/>)</span></#if>

                    <span class="protection product-sprite">
                        <a href="#" onclick="productController.showPriceProtection(); return false">
                            <div class="image"></div><span>Ценовая
                            защита</span></a>
                    </span>
                </div>

            <#if product.options?has_content>
                <div class="ops">
                    <#list product.options as o>
                        <div><strong>${o.attribute.name}:</strong></div>
                        <div class="options">
                            <input type="hidden" name="optionIds" value="${o.attribute.id}"/>

                            <#list o.values as v>
                                <div class="option">
                                    <input type="radio" id="option${o.attribute.id}_${v_index}"
                                           name="optionValues"
                                           value="${v}" <#if v_index==0>checked="checked"</#if>>
                                    <label for="option${o.attribute.id}_${v_index}">${v}</label>
                                </div>
                            </#list>
                        </div>
                    </#list>
                </div>
            </#if>

                <div class="action">
                <#if stockInfo.stockState == StockState.IN_STOCK || stockInfo.stockState == StockState.LIMITED_NUMBER>
                    <link itemprop="availability" href="http://schema.org/InStock"/>

                    <div class="quantity">
                        <span>Количество: </span>
                        <button class="q_down bg-ui-button" disabled="disabled" type="button"> -</button>
                        <input class="q_input" name="quantity" value="1">
                        <button class="q_up bg-ui-button" type="button"> +</button>
                    </div>

                    <div class="controls">
                        <button id="add" class="bg-ui-button" type="button">
                            Добавить в Корзину
                        </button>
                        <button id="buy" class="bg-ui-button" type="button">
                            Купить Сейчас
                        </button>
                    </div>
                <#else>
                    <div>
                        <link itemprop="availability" href="http://schema.org/OutOfStock"/>
                        <#if stockInfo.restockDate??>
                            <meta itemprop="availabilityStarts"
                                  content="${stockInfo.restockDate?date?string("yyyy-DD-mm")}"/>
                        </#if>

                        <#assign subscribed=registeredTracking?? && registeredTracking?seq_contains(TrackingType.AVAILABILITY)/>
                        <div class="availabilityTracking" <#if !subscribed>style="display: none"</#if>>
                            <p>
                                Товара нет в наличии и вы уже подписаны на получение извещения при сотуплении
                                товара.
                                Вы можете проверить список ваших подписок в <a href="/privacy/tracking">личном
                                кабинете</a>.
                            </p>

                            <p align="right">
                                <button type="button"
                                        onclick="tracking.remove(this, '${product.id}', '${TrackingType.AVAILABILITY.name()}')">
                                    Отписаться от получения уведомления
                                </button>
                            </p>
                        </div>

                        <div class="availabilityTracking" <#if subscribed>style="display: none"</#if>>
                            <p>
                                Товара нет в наличии в данный момент, но вы можете подписаться на обновления
                                и мы вышлим вам письмо, когда товар снова будет в наличии.
                            </p>

                            <p align="right">
                                <button type="button"
                                        onclick="tracking.add(this, '${product.id}', '${TrackingType.AVAILABILITY.name()}', ${member?has_content?string})">
                                    Подписаться на поступление
                                </button>
                            </p>
                        </div>
                    </div>
                </#if>
                </div>
            <#--

                            <div class="action-ext">
                                <a id="addWishListButton" href="#wishItem" onclick="return false;"
                                   title="Получить извещение, когда данный продукт снова станет доступным.">Добавить в список
                                    желаний</a>
                            </div>
            -->
            </div>
        </form>
    </div>
</td>
</tr>

<tr>
    <td colspan="2">
    </td>
</tr>
</table>

<#if product.description?has_content>
    <@bg.ui.panel caption="Описание" id="description">
        <#if product.state.promoted>
            <#assign subscribed=registeredTracking?? && registeredTracking?seq_contains(TrackingType.DESCRIPTION)/>
        <div class="descriptionTracking" <#if !subscribed>style="display: none"</#if>>
            <p>
                Мы еще не подготовили описание этого товара, но вы уже подписаны на получение уведомления, как только
                описание будет добавлено. Вы можете проверить список ваших подписок в <a href="/privacy/tracking">личном
                кабинете</a>.
            </p>

            <p align="right">
                <button type="button"
                        onclick="tracking.remove(this, '${product.id}', '${TrackingType.DESCRIPTION.name()}')">
                    Отписаться от получения уведомления
                </button>
            </p>
        </div>

        <div class="descriptionTracking" <#if subscribed>style="display: none"</#if>>
            <p>
                Мы еще не подготовили описание этого товара. Если же вы хотели бы получить его описание, пожалуйста,
                дайте нам знать об этом и мы добавим описание в самое ближайшее время.
            </p>

            <p align="right">
                <button type="button"
                        onclick="tracking.add(this, '${product.id}', '${TrackingType.DESCRIPTION.name()}', ${member?has_content?string})">
                    Заказать описание товара
                </button>
            </p>
        </div>
        <#else>
        <p itemprop="description">
        ${product.description!""}
        </p>
        </#if>
    </@bg.ui.panel>
</#if>

<#if accessories?has_content>
    <@bg.ui.panel "Запасные части <ul class=\"sly-pages\"></ul>" "accessories" "accessories group-vertical">
    <div class="sly-scrollbar vertical">
        <div class="handle">
            <div class="mousearea"></div>
        </div>
    </div>

    <div class="sly-frame">
        <table>
            <#list accessories as a>
                <@bg.ui.tableSplit accessories?size 2 a_index>
                    <td valign="top">
                        <@bg.ui.productItem a 'list'/>
                    </td>
                </@bg.ui.tableSplit>
            </#list>
        </table>
    </div>
    </@bg.ui.panel>
</#if>

<#if mode?has_content>
    <@bg.ui.panel "Модификации <ul class=\"sly-pages\"></ul>" "mode" "mode group-horizontal">
    <div class="sly-frame">
        <ul>
            <#list mode as a>
                <li>
                    <@bg.ui.productItem a 'grid'/>
                </li>
            </#list>
        </ul>
    </div>

    <div class="sly-scrollbar horizontal">
        <div class="handle">
            <div class="mousearea"></div>
        </div>
    </div>
    </@bg.ui.panel>
</#if>

<#if similar?has_content>
    <@bg.ui.panel "Похожие продукты <ul class=\"sly-pages\"></ul>" "similar" "similar group-horizontal">
    <div class="sly-frame">
        <ul>
            <#list similar as a>
                <li>
                    <@bg.ui.productItem a 'grid'/>
                </li>
            </#list>
        </ul>
    </div>

    <div class="sly-scrollbar horizontal">
        <div class="handle">
            <div class="mousearea"></div>
        </div>
    </div>
    </@bg.ui.panel>
</#if>
</div>

<div id="trackingEmailForm" style="display: none">
    <p>
        Пожалуйста, оставьте ваш адрес электронной почты или <a href="/account/signin">войдите в личный кабинет</a>,
        что бы получить уведомление по почте.
    </p>

    <form name="subscribeDescriptionForm">
        <label for="subscribeDescriptionEmail">Адрес эл. почты: </label>
        <input id="subscribeDescriptionEmail" name="email" type="text" style="width: 100%">
    </form>
</div>

<script type="text/javascript">
    var tracking = new bg.privacy.Tracking();
    var productController = new bg.warehouse.ProductController();
</script>
