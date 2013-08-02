<#-- @ftlvariable name="article" type="billiongoods.server.warehouse.Article" -->

<#include "/core.ftl">

<div class="article_layout">
    <table>
        <tr>
            <td valign="top">
                <div class="view">
                    <div class="preview">
                        <img alt="${article.name}"
                             src="<@bg.ui.image article article.previewImageId ImageSize.MEDIUM/>"/>
                    </div>

                    <div class="thumb">
                    <#list article.imageIds as i>
                        <div><img <#if i==article.previewImageId>class="selected"</#if>
                                src="<@bg.ui.image article i ImageSize.TINY/>" width="50px" height="50px"/></div>
                    </#list>
                    </div>
                </div>
            </td>
            <td valign="top" width="100%">
                <div class="info">
                    <div class="name">${article.name}</div>

                    <div class="articular">
                        Артикул: <span class="sku">${article.code}</span>
                    <#--Продано: <span class="sold">${article.soldCount}</span>-->
                    </div>

                    <div class="stock">
                        <div class="ability">${article.restockDate!"В наличии, доставка в текечии 2х рабочих дней"}</div>
                        <div class="shipment">Бесплатная доставка</div>
                    </div>

                <#if article.properties?has_content>
                    <div class="props">
                        <table>
                            <#list article.properties as p>
                                <tr>
                                    <td>${p.attribute.name}</td>
                                    <td>${p.value} ${p.attribute.unit}</td>
                                </tr>
                            </#list>
                        </table>

                        <div style="text-align: right; width: 100%">
                            <a href="#description">Подробее</a>
                        </div>
                    </div>
                </#if>

                    <form id="shoppingForm">
                        <input type="hidden" name="article" value="${article.id}"/>

                        <div class="panel">
                            <div class="price">
                            <@bg.ui.price article.price/>
                            </div>

                        <#if article.options?has_content>
                            <div class="ops">
                                <#list article.options as o>
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
                                <div class="quantity">
                                    <span>Количество: </span>
                                    <button id="q_down" disabled="disabled" class="bg-ui-button" type="button">
                                        -
                                    </button>
                                    <input id="quantity" name="quantity" value="1">
                                    <button id="q_up" class="bg-ui-button" type="button">
                                        +
                                    </button>
                                </div>
                                <div class="controls">
                                    <button id="add" class="bg-ui-button" type="button">
                                        Добавить в Корзину
                                    </button>
                                    <button id="buy" class="bg-ui-button" type="button">
                                        Купить Сейчас
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </td>
        </tr>
    </table>

    <div id="description" class="description">
        <a name="description"></a>

        <div class="tit">
            Описание
        </div>

        <div class="cnt">
        ${article.description!""}
        </div>
    </div>


<#if article.accessories?has_content>
    <div id="accessories" class="accessories">
        <a name="accessories"></a>

        <div class="tit">
            Принадлежности
        </div>

        <div class="cnt">
            <table>
                <#list article.accessories as a>
                    <#if a_index==0>
                    <tr></#if>
                    <td valign="top" width="50%">
                        <div class="article-item list">
                            <div class="image">
                                <@bg.link.article a><img
                                        alt="${a.name}"
                                        title="${a.name}"
                                        src="<@bg.ui.image a a.previewImageId!"" ImageSize.SMALL/>"
                                        width="75px" height="75px"/></@bg.link.article>
                            </div>
                            <div class="name"><@bg.link.article a>${a.name}</@bg.link.article></div>
                            <div class="price"><@bg.ui.price a.price/></div>
                    </td>
                    <#if a_index-1==0></tr></#if>
                </#list>
            </table>
        </div>
    </div>
</#if>
</div>

<script type="application/javascript">
    new bg.warehouse.Controller();
</script>