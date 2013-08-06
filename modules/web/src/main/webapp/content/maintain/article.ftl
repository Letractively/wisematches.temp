<#-- @ftlvariable name="catalog" type="billiongoods.server.warehouse.Catalog" -->
<#-- @ftlvariable name="article" type="billiongoods.server.warehouse.Article" -->
<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="attributes" type="billiongoods.server.warehouse.Attribute[]" -->

<#-- @ftlvariable name="imageResourcesDomain" type="java.lang.String" -->

<#include "/core.ftl">

<script type="text/javascript" src="<@bg.ui.static "ckeditor/ckeditor.js"/>"></script>
<script type="text/javascript" src="<@bg.ui.static "js/jquery.ui.widget-1.10.3.js"/>"></script>
<script type="text/javascript" src="<@bg.ui.static "js/jquery.fileupload-8.6.1.js"/>"></script>

<div style="padding: 10px; border: 1px solid gray;">
<form action="/maintain/article" method="post">

<table style="width: 100%">
<tr>
    <td valign="top"><label for="category">Категория: </label></td>
    <td>
    <@bg.ui.selectCategory "form.category" catalog true/>
    </td>
</tr>
<tr>
<td valign="top"><label for="name">Имя: </label></td>
    <td>
    <@bg.ui.field path="form.name">
        <textarea rows="3" style="width: 100%" name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
    </@bg.ui.field>

    <@bg.ui.input path="form.id" fieldType="hidden">
        <#assign articleId=bg.ui.statusValue!""/>
        <#if bg.ui.statusValue?has_content><a href="/warehouse/article/${bg.ui.statusValue}"
                                              target="_blank">посмотреть</a></#if>
    </@bg.ui.input>
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
    <td><label for="price">Цена: </label></td>
    <td><@bg.ui.input path="form.price"/></td>
</tr>
<tr>
    <td><label for="primordialPrice">Цена до скидки: </label></td>
    <td><@bg.ui.input path="form.primordialPrice"/></td>
</tr>

<#if articleId?has_content>
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

            <#if propertyIds?is_collection && (propertyIds?size >0)>
                <#list 0..(propertyIds?size)-1 as i>
                    <#assign id=propertyIds[i]/>
                    <#assign value=propertyValues[i]!""/>
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

            <#if optionIds?is_collection && (optionIds?size>0)>
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
    <td colspan="2">
        <hr>
    </td>
</tr>
<tr>
    <td valign="top"><label for="viewImages">Другие изображения: </label></td>
    <td>
        <div class="images">
            <@bg.ui.bind path="form.viewImages"/>
            <#assign viewImages=bg.ui.status.actualValue!""/>

            <@bg.ui.bind path="form.enabledImages"/>
            <#assign enabledImages=bg.ui.status.actualValue!""/>

            <@bg.ui.bind path="form.previewImage"/>
            <#assign previewImage=bg.ui.status.actualValue!""/>

            <#if viewImages?is_collection>
                <#list viewImages as i>
                    <div class="image">
                        <img src="<@bg.ui.articleImg article i ImageSize.SMALL/>"/>
                        <input name="enabledImages" type="checkbox" value="${i}"
                               <#if enabledImages?contains(i)>checked="checked"</#if>/>
                        <input name="previewImage" type="radio" value="${i}"
                               <#if i==previewImage>checked="checked"</#if>/>
                    </div>
                </#list>
            </#if>
        </div>

        <div>
            <label for="fileupload">Добавить изображение</label>
            <input id="fileupload" type="file" name="files[]" data-url="/maintain/article/addimg" multiple>
        </div>
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
</#if>

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
    <td colspan="2"><@bg.ui.spring.showErrors "br"/> </td>
</tr>

<tr>
    <td></td>
    <td>
    <#if articleId?has_content>
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

    var recalculatePrice = function (val) {
        var f = parseFloat(val);
        return (f + f * 0.15 + 0.3).toFixed(2); // 0.15 - percents, 0.3 - fixed paypal comission
    };

    $("#supplierPrice").change(function () {
        $("#price").val(recalculatePrice($(this).val()));
    });

    $("#supplierPrimordialPrice").change(function () {
        $("#primordialPrice").val(recalculatePrice($(this).val()));
    });

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

    $(function () {
        $('#fileupload').fileupload({
            dataType: 'json',
            done: function (e, data) {
                var code = data.result.data.code;
                var uri = data.result.data.uri;

                var s = '';
                s += '<div class="image">';
                s += '<img src="${imageResourcesDomain}/' + uri.small + '"/>';
                s += '<input name="enabledImages" type="checkbox" value="' + code + '" checked="checked"/>';
                s += '<input name="previewImage" type="radio" value="' + code + '"/>';
                s += '</div>';

                $(".images").append($(s));
            }
        });
    });
</script>