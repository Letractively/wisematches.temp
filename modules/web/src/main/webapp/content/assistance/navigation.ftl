<#include "/core.ftl"/>

<#assign pages=["about", "advantage", "shipping", "warranty", "payment", "paypal"]/>

<div class="navigation assistance">
    <div class="title">Центр Поддержки</div>

    <div class="list">
        <ul>
        <#list pages as p>
            <li><span class="image"></span><@bg.link.assist p/></li>
        </#list>
        </ul>
    </div>
</div>