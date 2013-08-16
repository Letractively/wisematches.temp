<#-- @ftlvariable name="basketQuantity" type="java.lang.Integer" -->

<#include "/core.ftl"/>

<div style="float: right">
    <ul>
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