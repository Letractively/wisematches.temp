<#-- @ftlvariable name="product" type="billiongoods.server.warehouse.Product" -->
<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="attributes" type="billiongoods.server.warehouse.Attribute[]" -->

<#-- @ftlvariable name="groups" type="billiongoods.server.warehouse.Group[]" -->
<#-- @ftlvariable name="relationships" type="billiongoods.server.warehouse.Relationship[]" -->

<#-- @ftlvariable name="imageResourcesDomain" type="java.lang.String" -->

<#include "/core.ftl">

<script type="text/javascript" src="<@bg.ui.static "ckeditor/ckeditor.js"/>"></script>
<script type="text/javascript" src="<@bg.ui.static "js/jquery.ui.widget-1.10.3.js"/>"></script>
<script type="text/javascript" src="<@bg.ui.static "js/jquery.fileupload-8.6.1.js"/>"></script>

<div class="product-maintain" style="padding: 10px; border: 1px solid gray;">
<form action="/maintain/product" method="post">

<table style="width: 100%">
<#if product??>
    <#if !product.state.active>
    <tr id="inactiveWarning">
        <td colspan="2" align="center" class="${product.state.name()?lower_case}">
            Внимание! Товар не в активном состоянии: ${product.state.name()}
        </td>
    </tr>
    </#if>
<tr>
    <td valign="top"><label for="category">Артикул: </label></td>
    <td>
        <@bg.ui.input path="form.id" fieldType="hidden">
            <#assign productId=bg.ui.statusValue!""/>
            <#if bg.ui.statusValue?has_content><a href="/warehouse/product/${bg.ui.statusValue}"
                                                  target="_blank">${messageSource.getProductCode(product)}</a></#if>
        </@bg.ui.input>
    </td>
</tr>
</#if>
<tr>
    <td valign="top"><label for="category">Категория: </label></td>
    <td>
    <@bg.ui.selectCategory "form.category" catalog false/>
    </td>
</tr>
<tr>
    <td valign="top"><label for="name">Имя: </label></td>
    <td>
    <@bg.ui.field path="form.name">
        <textarea rows="3" style="width: 100%" name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
    </@bg.ui.field>
    </td>
</tr>
<tr>
    <td valign="top"><label for="commentary">Коментарий: </label></td>
    <td>
    <@bg.ui.field path="form.commentary">
        <textarea rows="2" style="width: 100%" name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
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
    <td><label for="supplierReferenceCode">Код поставщика (SKU): </label></td>
    <td><@bg.ui.input path="form.supplierReferenceCode"/></td>
</tr>
<tr>
    <td valign="top"><label for="supplierReferenceId">Страница описания: </label></td>
    <td><@bg.ui.input path="form.supplierReferenceId" size=90>
        <#if product??>
            (<a id="supplierReferenceLink"
                href="${product.supplierInfo.referenceUrl.toExternalForm()}"
                target="_blank">открыть в новом окне</a>)</#if>
    </@bg.ui.input>
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
    <td><label for="storeAvailable">Количество на складе: </label></td>
    <td><@bg.ui.input path="form.storeAvailable"/></td>
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
    <td><label for="weight">Вес: </label></td>
    <td><@bg.ui.input path="form.weight"/></td>
</tr>

<#if productId?has_content>
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
    <td valign="top"><label for="viewImages">Состоит в группе: </label></td>
    <td>
        <table id="groupsTable">
            <#if groups??>
                <#list groups as g>
                    <tr class="group">
                        <td>
                            <input type="hidden" name="participatedGroups" value="${g.id}">
                            <a href="/maintain/group?id=${g.id}" target="_blank">#${g.id} ${g.name}</a>
                        </td>
                        <td>
                            <button class="remove" type="button">Удалить</button>
                        </td>
                    </tr>
                </#list>
            </#if>
            <tr id="groupsControls">
                <td></td>
                <td>
                    <button class="add" type="button">Добавить в группу</button>
                </td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td valign="top"><label for="viewImages">Связана с группами: </label></td>
    <td>
        <table id="relationshipsTable">
            <#if relationships??>
                <#list relationships as r>
                    <tr class="relationship">
                        <td>
                            <input name="relationshipTypes" type="hidden" value="${r.type.name()}">
                            <@message code="relationship.${r.type.name()?lower_case}.label"/>
                        </td>
                        <td>
                            <input name="relationshipGroups" type="hidden" value="${r.group.id}">
                            <a href="/maintain/group?id=${r.group.id}"
                               target="_blank">#${r.group.id} ${r.group.name}</a>
                        </td>
                        <td>
                            <button class="remove" type="button">Удалить</button>
                        </td>
                    </tr>
                </#list>
            </#if>
            <tr id="relationshipsControls">
                <td></td>
                <td></td>
                <td>
                    <button class="add" type="button">Связать с группой</button>
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
                        <@bg.ui.productImage product i ImageSize.SMALL/>
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
            <input id="fileupload" type="file" name="files[]" data-url="/maintain/product/addimg" multiple>
        </div>
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
    <td colspan="2">
        <label for="productState">Состояние: </label>
    <@bg.ui.bind "form.productState"/>
        <select id="productState" name="${bg.ui.status.expression}">
        <#list ProductState.values() as s>
            <option value="${s.name()}" <#if bg.ui.actualValue=s>selected="selected"</#if>>${s.name()}</option>
        </#list>
        </select>

    <#if productId?has_content>
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

    var addGroup = function () {
        var tr = $("<tr></tr>");
        var values = $("<td></td>").html('<input name="participatedGroups" value=""/>');
        var remove = $("<td></td>").append($('<button class="remove" type="button">Удалить</button>').click(removeGroup));
        tr.append(values).append(remove).insertBefore($("#groupsControls"));
    };

    var removeGroup = function () {
        $(this).parent().parent().remove();
    };

    var addRelationship = function () {
        var tr = $("<tr></tr>");

        var select = '<select name="relationshipTypes">';
    <#list RelationshipType.values() as t>
        select += '<option value="${t.name()}"> <@message code="relationship.${t.name()?lower_case}.label"/>';
    </#list>
        select += '</select>';

        var attrs = $("<td></td>").html(select);
        var values = $("<td></td>").html('<input name="relationshipGroups" value=""/>');
        var remove = $("<td></td>").append($('<button type="button">Удалить</button>').click(removeRelationship));

        tr.append(attrs).append(values).append(remove).insertBefore($("#relationshipsControls"));
    };

    var removeRelationship = function () {
        $(this).parent().parent().remove();
    };

    var recalculatePrice = function (val) {
        var v = parseFloat(val);
        return (v + v * 0.2) * 35 + 10;
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

    $("#groupsTable").find("button.add").click(addGroup);
    $("#groupsTable").find("button.remove").click(removeGroup);

    $("#relationshipsTable").find("button.add").click(addRelationship);
    $("#relationshipsTable").find("button.remove").click(removeRelationship);

    $(".attribute").each(function (i, v) {
        var id = $(this).text();
        var attr = attributes[id];
        $(this).html(attr.name + ", " + attr.unit);
    });

    $("#supplierReferenceId").change(function () {
        $("#supplierReferenceLink").attr('href', 'http://www.banggood.com/' + $(this).val());
    });

    <#if product??>
    $("#active").click(function () {
        var val = $(this).val() === 'true';
        bg.ui.lock(null, 'Влючение товара. Пожалуйста, подождите...');
        $.post("/maintain/product/activate.ajax?id=${product.id}&a=" + val)
                .done(function (response) {
                    if (response.success) {
                        bg.ui.unlock(null, val ? "Товар успешно включен" : "Товар успешно выключен", false);
                    } else {
                        bg.ui.unlock(null, response.message, true);
                    }
                })
                .fail(function (jqXHR, textStatus, errorThrown) {
                    bg.ui.unlock(null, "Товар не может быть включен по техническим причинам", true);
                });
        if (val) {
            $("#inactiveWarning").hide();
        } else {
            $("#inactiveWarning").show();
        }
        $(this).text(val ? "Отключить" : "Включить").val(!val);
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
    </#if>
</script>