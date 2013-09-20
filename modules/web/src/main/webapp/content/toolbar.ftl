<#-- @ftlvariable name="basketQuantity" type="java.lang.Integer" -->
<#-- @ftlvariable name="pageableForm" type="billiongoods.server.web.servlet.mvc.warehouse.form.PageableForm" -->

<#include "/core.ftl"/>

<div style="float: right">
    <ul>
        <li class="bg-ui-search">
            <form action="/warehouse/search">
                <input name="query"
                       value="<#if pageableForm?? && pageableForm.query?has_content>${pageableForm.query}</#if>">
                <select name="category">
                    <option value="0">Все Категории</option>
                <#if pageableForm?? && pageableForm.category?has_content>
                    <#assign rootCategory=catalog.getCategory(pageableForm.category).genealogy.root/>
                    <#list catalog.rootCategories as c>
                        <option value="${c.id}"
                                <#if c=rootCategory>selected="selected"</#if>>${c.name}</option>
                    </#list>
                <#else>
                    <#list catalog.rootCategories as c>
                        <option value="${c.id}">${c.name}</option>
                    </#list>
                </#if>
                </select>
                <button class="bg-ui-button" type="submit">Искать</button>
            </form>
        </li>

        <li class="bg-ui-button tb-item-cart tb-separator-left tb-separator-right">
            <a href="/warehouse/basket">
                <span class="image"></span>
                <span style="margin-right: 30px">Корзина</span>

            <#if basketQuantity??>
                <div id="basketQuantity">${basketQuantity}</div>
            </#if>
            </a>
        </li>
    </ul>
</div>

<div>
    <ul>
        <li class="bg-ui-button tb-item-catalog tb-separator-left tb-separator-right">
            <a href="/">Домашняя страница</a>

        <li class="bg-ui-button tb-item-arrivals tb-separator-right"><a href="/warehouse/arrivals">Новые поступления</a>
    </ul>
</div>