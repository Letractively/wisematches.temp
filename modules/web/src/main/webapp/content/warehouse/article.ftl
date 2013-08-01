<#-- @ftlvariable name="article" type="billiongoods.server.warehouse.Article" -->

<#include "/core.ftl">

<div class="article_layout">
    <table>
        <tr>
            <td valign="top">
                <div class="view">
                    <div class="preview">
                        <img src="<@bg.ui.image article article.previewImageId ImageSize.MEDIUM/>"/>
                    </div>

                    <div class="thumb">
                    <#list article.imageIds as i>
                        <div><img <#if i==article.previewImageId>class="selected"</#if>
                                  src="<@bg.ui.image article i ImageSize.TINY/>"/></div>
                    </#list>
                    </div>
                </div>
            </td>
            <td valign="top" width="100%">
                <div class="info">
                    <div class="name">${article.name}</div>

                    <div class="articular">Артикул: <span class="sku">${article.code}</span> Продано: <span
                            class="sold">${article.soldCount}</span></div>

                    <div class="stock">
                        <div class="ability">${article.restockDate!"В наличии"}</div>
                        <div class="shipment">Бесплатная доставка</div>
                    </div>

                    <table class="props">
                    <#list article.properties as p>
                        <tr>
                            <td>${p.attribute.name}</td>
                            <td>${p.value} ${p.attribute.unit}</td>
                        </tr>
                    </#list>
                    </table>

                    <div class="panel">
                        <div class="ops">
                        <#list article.options as o>
                            <div><strong>${o.attribute.name}:</strong></div>
                            <div class="options">
                                <#list o.values as v>
                                    <div class="option">
                                        <input type="radio" id="option${v_index}" name="option${o.attribute.id}"
                                               value="${v_index}">
                                        <label for="option${v_index}">${v}</label>
                                    </div>
                                </#list>
                            </div>
                        </#list>
                        </div>

                        <div class="price">
                        <@bg.ui.price article.price/>
                        </div>

                        <div class="action">
                            <div class="quantity_layout">
                                <span>Количество: </span>
                                <a id="q_down" href="#"> - </a>
                                <input id="quantity" class="quantity" name="quantity" value="1">
                                <a id="q_up" href="#"> + </a>
                            </div>
                            <div>
                                <button type="button">Добавить в Корзину</button>
                                <button type="button">Купить Сейчас</button>
                            </div>
                        </div>
                    </div>
                </div>
            </td>
        </tr>
    </table>


    <div class="description">
    ${article.description!""}
    </div>

    <table>
    <#list article.accessories as a>
        <tr>
            <td>
                <@bg.link.article a>Accessory id: ${a.id} ${a.name}</@bg.link.article>
            </td>
        </tr>
    </#list>
    </table>
</div>