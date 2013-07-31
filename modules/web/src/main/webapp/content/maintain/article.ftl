<#-- @ftlvariable name="article" type="billiongoods.server.warehouse.Article" -->
<#-- @ftlvariable name="catalog" type="billiongoods.server.warehouse.Catalog" -->
<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="attributes" type="billiongoods.server.warehouse.Attribute[]" -->

<#include "/core.ftl">

<script type="text/javascript" src="<@bg.ui.static "ckeditor/ckeditor.js"/>"></script>

<#macro option category level selected>
<option <#if category.id?string==selected>selected="selected"</#if> value="${category.id}"><#list 0..level as i>
    -- </#list>${category.name} /${category.id}</option>
    <#list category.children as c>
        <@option c level+1 selected/>
    </#list>
</#macro>

<div style="padding: 10px; border: 1px solid gray;">
    <form action="/maintain/article" method="post">
    <@bg.ui.input path="form.id" fieldType="hidden"/>
    <@bg.ui.input path="form.category" fieldType="hidden"/>

        <table style="width: 100%">
            <tr>
                <td valign="top"><label for="name">Имя: </label></td>
                <td>
                <@bg.ui.input path="form.name"/>
                    (<#list category.genealogy.parents as c><a
                        href="/maintain/category?id=${c.id}">${c.name}</a><#if c_has_next>
                    > </#if></#list>)
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <hr>
                </td>
            </tr>
            <tr>
                <td><label for="price">Цена: </label></td>
                <td><@bg.ui.input path="form.price"/></td>
            </tr>
            <tr>
                <td><label for="primordialPrice">Цена до скидки: </label></td>
                <td><@bg.ui.input path="form.primordialPrice"/></td>
            </tr>
            <tr>
                <td colspan="2">
                    <hr>
                </td>
            </tr>
            <tr>
                <td><label for="properties">Параметры: </label></td>
                <td>
                    <table>
                    <#list category.genealogy.parents as c>
                        <#list c.attributes as a>
                            <tr>
                                <td>
                                    <input type="hidden" name="propertyIds" value="${a.id}">
                                    <label for="attribute${a.id}">${a.name}, ${a.unit}</label>
                                </td>
                                <td>
                                    <input name="propertyValues" value="">
                                </td>
                            </tr>
                        </#list>
                    </#list>
                    </table>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <hr>
                </td>
            </tr>
            <tr>
                <td><label for="restockDate">Дата поставки: </label></td>
                <td><@bg.ui.input path="form.restockDate"/></td>
            </tr>
            <tr>
                <td><label for="previewImage">Основное изображение: </label></td>
                <td><@bg.ui.input path="form.previewImage"/></td>
            </tr>
            <tr>
                <td colspan="2">
                    <hr>
                </td>
            </tr>
            <tr>
                <td><label for="supplierPrice">Цена поставщика: </label></td>
                <td><@bg.ui.input path="form.supplierPrice"/></td>
            </tr>
            <tr>
                <td><label for="supplierPrimordialPrice">Цена поставщика до скодки: </label></td>
                <td><@bg.ui.input path="form.supplierPrimordialPrice"/></td>
            </tr>
            <tr>
                <td><label for="supplierReferenceCode">Код поставщика: </label></td>
                <td><@bg.ui.input path="form.supplierReferenceCode"/></td>
            </tr>
            <tr>
                <td><label for="supplierReferenceId">Идентификатор поставщика: </label></td>
                <td><@bg.ui.input path="form.supplierReferenceId"/></td>
            </tr>
            <tr>
                <td colspan="2">
                    <hr>
                </td>
            </tr>

            <tr>
                <td colspan="2">
                <@bg.ui.field path="form.description">
                    <textarea style="width: 100%; min-height: 400px"
                              name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
                </@bg.ui.field>
                </td>
            </tr>

            <tr>
                <td></td>
                <td>
                <#if article??>
                    <button id="add" type="submit">Изменить</button>
                <#else>
                    <button id="add" type="submit">Создать</button>
                </#if>
                </td>
            </tr>
        </table>
    </form>
</div>

<script>
    window.onload = function () {
        CKEDITOR.replace('description');
    };
</script>