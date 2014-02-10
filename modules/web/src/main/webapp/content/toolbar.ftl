<#-- @ftlvariable name="basketQuantity" type="java.lang.Integer" -->
<#-- @ftlvariable name="pageableForm" type="billiongoods.server.web.servlet.mvc.warehouse.form.ProductsPageableForm" -->

<#include "/core.ftl"/>

<div style=" position: relative">
    <div style="float: right;">
        <ul>
            <li class="tb-item-search">
                <form class="global-search" name="site-search" action="/warehouse/search" method="get"
                      role="search"
                      accept-charset="utf-8">
                <span id="searchCatalog" class="search-sprite">
                    <span id="searchCatName" class="search-cat-name">Все разделы</span>
                    <span class="search-cat-arrow"></span>

                <#assign selectedCategory=""/>
                <#if pageableForm?? && pageableForm.category?has_content>
                    <#assign selectedCategory=catalog.getCategory(pageableForm.category)!""/>
                </#if>

                    <select id="searchCatalog" class="search-catalog" name="category" title="Искать в">
                        <option value="0" selected="selected">Все разделы</option>
                    <#list catalog.rootCategories as c>
                        <option value="${c.id}" <#if c=selectedCategory>selected="selected"</#if>>${c.name}</option>
                        <#if c.children?has_content>
                            <#list c.children as sc>
                                <option value="${sc.id}"
                                        <#if sc=selectedCategory>selected="selected"</#if>>
                                    &nbsp;&nbsp;${sc.name}</option>
                            </#list>
                        </#if>
                    </#list>
                    </select>
                </span>

                    <div class="search-input">
                        <input type="text" id="searchInputField" title="Искать"
                               value="<#if pageableForm?? && pageableForm.query?has_content>${pageableForm.query?replace("\"", "&quot;")}</#if>"
                               name="query"
                               autocomplete="off">
                    </div>

                    <div id="searchAction" class="search-button">
                        <button class="bg-ui-button" type="submit">Искать</button>
                    </div>
                </form>
            </li>

            <li class="bg-ui-button tb-item-cart tb-separator-left tb-separator-right">
                <a href="/warehouse/basket">
                    <span class="image"></span>
                    <span style="margin-right: 30px">Корзина</span>

                <#if basketQuantity??>
                    <div id="basketQuantity">${basketQuantity!"0"}</div>
                </#if>
                </a>
            </li>
        </ul>
    </div>

    <div>
        <ul>
            <li class="bg-ui-button tb-item-catalog tb-separator-left tb-separator-right">
                <a href="/">Основной каталог</a>

                <div id="globalCatalog" style="display: none">
                <#include "warehouse/widget/catalog.ftl"/>
                </div>
            </li>

            <li class="bg-ui-button tb-item-arrivals tb-separator-right" style="margin-left: -4px">
                <a href="/warehouse/arrivals">Новые поступления</a>
            </li>
        </ul>
    </div>
</div>