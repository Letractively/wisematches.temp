<#-- @ftlvariable name="recommendations" type="billiongoods.server.warehouse.ProductPreview[]" -->

<#include "/core.ftl"/>

<#if recommendations?has_content>
<div class="recommends">
    <div class="title">
        Рекомендуемые
    </div>

    <div class="container">
        <#list recommendations as p>
            <ul>
                <li class="pic">
                    <@bg.link.product p><@bg.ui.productImage p p.previewImageId!"" ImageSize.TINY/></@bg.link.product>
                </li>
                <li class="name">
                    <@bg.link.product p>${p.name}</@bg.link.product>
                </li>
            </ul>
        </#list>
    </div>
</div>
</#if>