<#-- @ftlvariable name="article" type="billiongoods.server.warehouse.Article" -->
<#-- @ftlvariable name="relationships" type="billiongoods.server.warehouse.Relationships" -->

<#include "/core.ftl">

<div class="article_layout">
    <table>
        <tr>
            <td valign="top">
                <div class="view">
                    <div class="preview">
                        <img alt="${article.name}"
                             src="<@bg.ui.articleImg article article.previewImageId!"" ImageSize.MEDIUM/>"/>
                    <@bg.ui.discountDiv article/>
                    </div>

                    <div class="thumb">
                    <#list article.imageIds as i>
                        <div><img <#if i==article.previewImageId!"">class="selected"</#if>
                                  src="<@bg.ui.articleImg article i ImageSize.TINY/>" width="50px" height="50px"/></div>
                    </#list>
                    </div>
                </div>
            </td>
            <td valign="top" width="100%">
                <div class="info">
                    <div class="name">
                    ${article.name}
                    <@bg.security.authorized "moderator">
                        <div style="float: right">
                            <a href="/maintain/article?id=${article.id}">Изменить</a>
                        </div>
                    </@bg.security.authorized>
                    </div>

                    <div class="articular">
                        Артикул: <span class="sku">${messageSource.getArticleCode(article)}</span>
                    <@bg.security.authorized "moderator">
                        (<a href="http://banggood.com/-p-${article.supplierInfo.referenceId}.html"
                            target="_blank">${article.supplierInfo.referenceCode}</a>)
                    </@bg.security.authorized>
                    <#--Продано: <span class="sold">${article.soldCount}</span>-->
                    </div>

                    <div class="stock">
                        <div class="ability">${article.restockDate!"В наличии, отправка в течение 2х рабочих дней"}</div>
                        <div class="shipment">Бесплатная доставка</div>
                    </div>

                <#if article.properties?has_content>
                    <div class="props">
                        <table>
                            <#list article.properties as p>
                                <#if p.value?has_content>
                                    <tr>
                                        <td>${p.attribute.name}</td>
                                        <td>${p.value} ${p.attribute.unit}</td>
                                    </tr>
                                </#if>
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
                            <@bg.ui.price article.price.amount/>
                            <#if article.price.primordialAmount??><span
                                    class="primordial"><@bg.ui.price article.price.primordialAmount/></span></#if>
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
                            </div>
                        </div>
                    </form>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">

            <#if article.description?has_content>
                <@bg.ui.panel "Описание" "description">${article.description!""}</@bg.ui.panel>
            </#if>

            <#list [RelationshipType.ACCESSORIES, RelationshipType.MODE] as t>
                <#assign articles=relationships.getAssociations(t)!""/>
                <#if articles?has_content>
                    <@bg.ui.panel messageSource.getMessage("relationship.${t.name()?lower_case}.label", locale) t.name()?lower_case>
                        <table>
                            <#list articles as a>
                                <@bg.ui.tableSplit articles?size 2 a_index>
                                    <td valign="top">
                                        <div class="article-item list">
                                            <div class="image">
                                                <@bg.link.article a><img
                                                        alt="${a.name}"
                                                        title="${a.name}"
                                                        src="<@bg.ui.articleImg a a.previewImageId!"" ImageSize.SMALL/>"
                                                        width="75px" height="75px"/></@bg.link.article>
                                            </div>
                                            <div class="name"><@bg.link.article a>${a.name}</@bg.link.article></div>
                                            <div class="price"><@bg.ui.price a.price.amount/></div>
                                    </td>
                                </@bg.ui.tableSplit>
                            </#list>
                        </table>
                    </@bg.ui.panel>
                </#if>
            </#list>
            </td>
        </tr>
    </table>
</div>

<script type="application/javascript">
    new bg.warehouse.Controller();
</script>