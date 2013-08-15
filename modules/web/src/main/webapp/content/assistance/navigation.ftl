<#include "/core.ftl"/>

<#assign pages=["about", "shipping", "warranty", "payment", "customs", "contacts"]/>

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