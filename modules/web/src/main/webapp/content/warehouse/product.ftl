<#-- @ftlvariable name="product" type="billiongoods.server.warehouse.Product" -->
<#-- @ftlvariable name="related" type="billiongoods.server.warehouse.ProductDescription[]" -->
<#-- @ftlvariable name="accessories" type="billiongoods.server.warehouse.ProductDescription[]" -->

<#include "/core.ftl">

<script type="text/javascript" src="<@bg.ui.static "js/jquery.sly-1.0.2.min.js"/>"></script>

<link rel="stylesheet" href="<@bg.ui.static "css/jquery.prettyPhoto-3.1.5.css"/>" type="text/css" charset="utf-8"/>
<script type="text/javascript" src="<@bg.ui.static "js/jquery.prettyPhoto-3.1.5.js"/>"></script>

<#assign sku=messageSource.getProductCode(product)/>

<div class="product ${product.state.name()?lower_case}" itemscope itemtype="http://schema.org/Product">
<meta itemprop="url" content="http://www.billiongoods.ru/warehouse/product/${product.id}"/>
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
        </td>
        <td valign="top" width="100%">
            <div class="info">
                <div class="name" itemprop="name">
                ${product.name}
                <@bg.security.authorized "moderator">
                    <div style="float: right">
                        <a href="/maintain/product?id=${product.id}">Изменить</a>
                    </div>
                </@bg.security.authorized>
                </div>

                <div class="articular">
                    Артикул: <span class="sku" itemprop="sku">${sku}</span>
                <@bg.security.authorized "moderator">
                    (<a href="${product.supplierInfo.referenceUrl.toExternalForm()}"
                        target="_blank">${product.supplierInfo.referenceCode}</a>)
                </@bg.security.authorized>
                <#--Продано: <span class="sold">${product.stockInfo.sold}</span>-->
                </div>

                <div class="stock">
                    <div class="ability">
                    <#if product.stockInfo.restockDate??>
                        Нет на складе. Поступление
                        ожидается ${messageSource.formatDate(product.stockInfo.restockDate, locale)}
                    <#else>
                        <#if product.stockInfo.rest??>
                            Торопитесь, осталось всего ${product.stockInfo.rest} штук!
                        <#else>
                            В наличии, обычно отправлается в течении 2-3 рабочих дней
                        </#if>
                    </#if>
                    </div>
                    <div class="shipment">Бесплатная доставка</div>
                </div>

            <#if product.properties?has_content>
                <div class="props">
                    <table>
                        <#list product.properties as p>
                            <#if p.value?has_content>
                                <tr>
                                    <td>${p.attribute.name}</td>
                                    <td>${p.value} ${p.attribute.unit}</td>
                                </tr>
                            </#if>
                        </#list>
                    </table>
                </div>
            </#if>

                <div style="text-align: right; width: 100%">
                    <a href="#description">Описание</a>
                <#if accessories?has_content>| <a href="#accessories">Запачные части</a></#if>
                <#if related?has_content>| <a href="#related">Похожие продукты</a></#if>
                </div>

                <form id="shoppingForm" itemprop="offers" itemscope itemtype="http://schema.org/Offer">
                    <meta itemprop="deliveryLeadTime" content="30 days"/>
                    <input type="hidden" name="product" value="${product.id}"/>

                    <div class="panel">
                        <div class="price">
                        <@bg.ui.price product.price.amount/>
                        <#if product.price.primordialAmount??><span
                                class="primordial"><@bg.ui.price product.price.primordialAmount/></span></#if>
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
                        <#if product.stockInfo.available>
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
                            <link itemprop="availability" href="http://schema.org/OutOfStock"/>
                            <#if product.stockInfo.restockDate??>
                                <meta itemprop="availabilityStarts"
                                      content="${product.stockInfo.restockDate?date?string("yyyy-DD-mm")}"/>
                            </#if>

                            Товара нет в наличии в данный момент. Вы можете <a href="/assistance/contacts#section3">отправить
                            нам заявку</a> и мы пришлем вам письмо, когда товар снова будет в наличии.
                            <br>
                            Не забудьте указать артикул товара: ${sku}!
                        </#if>
                        </div>
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
        <p>
            Мы еще не подготовили описание этого товара. Пожалуйста, если он вас заинтересовал и вы бы желали
            увидеть его описание в ближайшее время, <a href="/assistance/contacts#section3">свяжитесь с нами</a> и
            мы
            добавим описание в ближайшее время.
        </p>

        <p>
            Пожалуйста, не забудьте указать артикул интересующего вас
            товара: ${sku}!
        </p>
        <#else>
        <p itemprop="description">
        ${product.description!""}
        </p>
        </#if>
    </@bg.ui.panel>
</#if>

<#if accessories?has_content>
    <@bg.ui.panel "Запасные части <ul class=\"sly-pages\"></ul>" "accessories">
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

<#if related?has_content>
    <@bg.ui.panel "Похожие продукты <ul class=\"sly-pages\"></ul>" "related">
    <div class="sly-frame">
        <ul>
            <#list related as a>
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

<script type="application/javascript">
    new bg.warehouse.ProductController();
</script>
