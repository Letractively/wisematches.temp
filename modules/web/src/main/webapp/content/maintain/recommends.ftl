<#-- @ftlvariable name="recommendations" type="billiongoods.server.warehouse.ProductPreview[]" -->

<#include "/core.ftl"/>


<form action="/maintain/recommends/reload" method="post">
    <button value="reload">Перезагрузить рекомендации</button>
</form>

<table width="100%" cellpadding="5">
    <tr>
        <th colspan="2">Продукт</th>
        <th>Действие</th>
    </tr>
<#list recommendations as p>
    <#assign category=catalog.getCategory(p.categoryId)/>
    <tr>
        <td valign="top">
            <@bg.ui.productImage p p.previewImageId!"" ImageSize.TINY/>
        </td>
        <td valign="top" width="100%">
            <@bg.link.product p>${p.name}</@bg.link.product>
            <br>
            <@bg.link.category category>${category.name}</@bg.link.category>
        </td>
        <td valign="top" nowrap="nowrap">
            <button type="button"
                    onclick="bg.warehouse.Maintain.recommend(${p.id}, false)">
                Удалить
            </button>
        </td>
    </tr>
</#list>
</table>