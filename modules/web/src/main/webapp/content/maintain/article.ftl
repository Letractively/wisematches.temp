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
<@bg.ui.input path="form.category" fieldType="hidden"/>

<table style="width: 100%">
<tr>
    <td valign="top"><label for="name">Имя: </label></td>
    <td>
    <@bg.ui.input path="form.name"/>

    <@bg.ui.input path="form.id" fieldType="hidden">
        <#if bg.ui.statusValue?has_content><a href="/warehouse/article/${bg.ui.statusValue}"
                                              target="_blank">посмотреть</a></#if>
    </@bg.ui.input>
    </td>
</tr>
<tr>
    <td valign="top"><label for="category">Категория: </label></td>
    <td>
    <#list category.genealogy.parents as c><a
            href="/maintain/category?id=${c.id}">${c.name}</a><#if c_has_next>
        > </#if></#list>
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
    <td valign="top"><label for="properties">Параметры: </label></td>
    <td>
        <table>
        <@bg.ui.bind path="form.propertyIds"/>
        <#assign propertyIds=bg.ui.status.actualValue!""/>

        <@bg.ui.bind path="form.propertyValues"/>
        <#assign propertyValues=bg.ui.status.actualValue!""/>

        <#if propertyIds?is_collection>
            <#list 0..(propertyIds?size)-1 as i>
                <#assign id=propertyIds[i]/>
                <#assign value=propertyValues[i]/>
                <tr>
                    <td>
                        <input type="hidden" name="propertyIds" value="${id}">
                        <label for="property${id}" class="attribute">${id}</label>
                    </td>
                    <td>
                        <input id="property${id}" name="propertyValues" value="${value}">
                    </td>
                </tr>
            </#list>
        </#if>
        </table>
    </td>
</tr>
<tr>
    <td colspan="2">
        <hr>
    </td>
</tr>
<tr>
    <td valign="top"><label for="options">Опции: </label></td>
    <td>
        <table id="optionsTable">
        <@bg.ui.bind path="form.optionIds"/>
        <#assign optionIds=bg.ui.status.actualValue!""/>

        <@bg.ui.bind path="form.optionValues"/>
        <#assign optionValues=bg.ui.status.actualValue!""/>

        <#if optionIds?is_collection>
            <#list 0..(optionIds?size)-1 as i>
                <#assign id=optionIds[i]/>
                <#assign value=optionValues[i]/>
                <tr>
                    <td>
                        <label for="option${id}" class="attribute">${id}</label>
                        <input name="optionIds" type="hidden" value="${id}"/>
                    </td>
                    <td>
                        <input id="option${id}" name="optionValues" value="${value}"/>
                    </td>
                    <td>
                        <button class="remove" type="button">Удалить</button>
                    </td>
                </tr>
            </#list>
        </#if>
            <tr id="optionControls">
                <td></td>
                <td colspan="2">
                    <button class="add" type="button">добавить</button>
                </td>
            </tr>
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
    <td valign="top"><label for="viewImages">Другие изображения: </label></td>
    <td>
    <@bg.ui.field path="form.viewImages">
        <table id="imagesTable">
            <#if bg.ui.status.actualValue??>
                <#list bg.ui.status.actualValue as i>
                    <tr>
                        <td><input name="viewImages" value="${i}"></td>
                        <td>
                            <button class="remove" type="button">Удалить</button>
                        </td>
                    </tr>
                </#list>
            </#if>
            <tr id="imagesControls">
                <td></td>
                <td>
                    <button class="add" type="button">добавить</button>
                </td>
            </tr>
        </table>
    </@bg.ui.field>
    </td>
</tr>
<tr>
    <td colspan="2">
        <hr>
    </td>
</tr>
<tr>
    <td valign="top"><label for="viewImages">Комплектующие: </label></td>
    <td>
    <@bg.ui.field path="form.accessories">
        <table id="accessoriesTable">
            <#if bg.ui.status.actualValue??>
                <#list bg.ui.status.actualValue as i>
                    <tr>
                        <td><input name="accessories" value="${i}"></td>
                        <td>
                            <button class="remove" type="button">Удалить</button>
                        </td>
                    </tr>
                </#list>
            </#if>
            <tr id="accessoriesControls">
                <td></td>
                <td>
                    <button class="add" type="button">добавить</button>
                </td>
            </tr>
        </table>
    </@bg.ui.field>
    </td>
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

    var attributes = {
    <#list attributes as a>
        '${a.id}': {name: "${a.name}", unit: "${a.unit}"}<#if a_has_next>,</#if>
    </#list>
    };

    var addOption = function () {
        var tr = $("<tr></tr>");

        var select = '<select name="optionIds">';
        $.each(attributes, function (key, value) {
            select += '<option value="' + key + '">' + value.name + ', ' + value.unit;
        });
        select += '</select>';

        var attrs = $("<td></td>").html(select);
        var values = $("<td></td>").html('<input name="optionValues" value=""/>');
        var remove = $("<td></td>").append($('<button type="button">Удалить</button>').click(removeOption));

        tr.append(attrs).append(values).append(remove).insertBefore($("#optionControls"));
    };

    var removeOption = function () {
        $(this).parent().parent().remove();
    };

    var addImage = function () {
        var tr = $("<tr></tr>");
        var values = $("<td></td>").html('<input name="viewImages" value=""/>');
        var remove = $("<td></td>").append($('<button type="button">Удалить</button>').click(removeImage));
        tr.append(values).append(remove).insertBefore($("#imagesControls"));
    };

    var removeImage = function () {
        $(this).parent().parent().remove();
    };

    var addAccessory = function () {
        var tr = $("<tr></tr>");
        var values = $("<td></td>").html('<input name="accessories" value=""/>');
        var remove = $("<td></td>").append($('<button type="button">Удалить</button>').click(removeAccessory));
        tr.append(values).append(remove).insertBefore($("#accessoriesControls"));
    };

    var removeAccessory = function () {
        $(this).parent().parent().remove();
    };

    $("#optionsTable").find("button.add").click(addOption);
    $("#optionsTable").find("button.remove").click(removeOption);

    $("#imagesTable").find("button.add").click(addImage);
    $("#imagesTable").find("button.remove").click(removeImage);

    $("#accessoriesTable").find("button.add").click(addAccessory);
    $("#accessoriesTable").find("button.remove").click(removeAccessory);

    $(".attribute").each(function (i, v) {
        var id = $(this).text();
        var attr = attributes[id];
        $(this).html(attr.name + ", " + attr.unit);
    });
</script>