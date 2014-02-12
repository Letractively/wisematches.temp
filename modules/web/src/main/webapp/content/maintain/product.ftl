<#-- @ftlvariable name="form" type="billiongoods.server.web.servlet.mvc.maintain.form.ProductForm" -->
<#-- @ftlvariable name="attributes" type="billiongoods.server.warehouse.Attribute[]" -->
<#-- @ftlvariable name="priceConverter" type="billiongoods.server.services.price.PriceConverter" -->

<#-- @ftlvariable name="imageResourcesDomain" type="java.lang.String" -->

<#include "/core.ftl">

<script type="text/javascript" src="<@bg.ui.static "ckeditor/ckeditor.js"/>"></script>
<script type="text/javascript" src="<@bg.ui.static "js/jquery.ui.widget-1.10.3.js"/>"></script>
<script type="text/javascript" src="<@bg.ui.static "js/jquery.fileupload-8.6.1.js"/>"></script>

<div class="product-maintain" style="padding: 10px; border: 1px solid gray;">

<form action="/maintain/product" method="post">
<table style="width: 100%">
<#if form.id?has_content>
    <#if !form.productState.active>
    <tr id="inactiveWarning">
        <td colspan="2" align="center" class="${form.productState.name()?lower_case}">
            Внимание! Товар не в активном состоянии: ${form.productState.name()}
        </td>
    </tr>
    </#if>
<tr>
    <td valign="top"><label for="id">Артикул: </label></td>
    <td>
        <@bg.ui.input path="form.id" fieldType="hidden">
            <a href="/warehouse/product/${bg.ui.statusValue}"
               target="_blank">${messageSource.getProductCode(bg.ui.actualValue)}</a>
        </@bg.ui.input>
    </td>
</tr>
</#if>
<tr>
    <td valign="top"><label for="categoryId">Категория: </label></td>
    <td>
        <div id="categoryDiv">
        <#if form.id??>
            <div>
                <#assign category=catalog.getCategory(form.categoryId)/>
                <#list category.genealogy.parents as c>
                    <a href="/maintain/category?id=${c.id}" target="_blank">${c.name}</a> ->
                </#list>
                <a href="/maintain/category?id=${category.id}" target="_blank">${category.name}</a>

                <a href="#" onclick="showCategoryEditor(); return false;">(изменить)</a>
            </div>
        </#if>

            <div <#if form.id??>style="display: none"</#if>>
            <@bg.ui.selectCategory "form.categoryId" catalog false>
                <#if bg.ui.statusValue?has_content>(<a href="/maintain/category?id=${bg.ui.statusValue}"
                                                       target="_blank">открыть в новом</a>)</#if>
            </@bg.ui.selectCategory>
            </div>
        </div>
    </td>
</tr>
<tr>
    <td valign="top"><label for="name">Имя: </label></td>
    <td>
    <@bg.ui.field path="form.name">
        <textarea id="${bg.ui.status.expression}" rows="4" style="width: 100%"
                  name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
    </@bg.ui.field>
    </td>
</tr>
<tr>
    <td valign="top"><label for="name">URL имя: </label></td>
    <td>
    <@bg.ui.field path="form.symbolic">
        <textarea id="${bg.ui.status.expression}" rows="2" style="width: 100%;" readonly="readonly"
                  name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
    </@bg.ui.field>
    </td>
</tr>
<tr>
    <td valign="top"><label for="commentary">Коментарий: </label></td>
    <td>
    <@bg.ui.field path="form.commentary">
        <textarea id="${bg.ui.status.expression}" rows="2" style="width: 100%"
                  name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
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
    <td><label for="supplierPrimordialPrice">Цена поставщика до скидки: </label></td>
    <td><@bg.ui.input path="form.supplierPrimordialPrice"/></td>
</tr>
<tr>
    <td><label for="supplierReferenceCode">Код поставщика (SKU): </label></td>
    <td><@bg.ui.input path="form.supplierReferenceCode"/></td>
</tr>
<tr>
    <td valign="top"><label for="supplierReferenceId">Страница описания: </label></td>
    <td>
    <@bg.ui.input path="form.supplierReferenceId" size=90>
        <#if bg.ui.statusValue?has_content>
            (<a id="supplierReferenceLink"
                href="http://www.banggood.com${bg.ui.statusValue}"
                target="_blank">открыть в новом окне</a>)</#if>
    </@bg.ui.input>
    </td>
</tr>
<tr>
    <td valign="top">
        <label for="supplierReferenceId">Информация поставщика: </label>
    </td>
    <td>
        <div id="supplierInfo">
            <span class="action">
                <span><a href="#" onclick="loadSupplierDescription(); return false;">загрузить информацию</a></span>
                <span class="progress" style="display: none"></span>
            </span>
            <span class="data"></span>
        </div>
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
    <td><@bg.ui.input path="form.storeAvailable">
        <button id="notAvailable" type="button">Нет в наличии</button>
        <button id="available" type="button">В наличии</button></@bg.ui.input></td>
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

<#if form.categoryId??>
    <#assign category=catalog.getCategory(form.categoryId)/>
<tr>
    <td colspan="2">
        <hr>
    </td>
</tr>
<tr>
    <td valign="top"><label for="properties">Параметры: </label></td>
    <td>
        <div id="productParameters">
            <table>
                <#list category.parameters as p>
                    <#assign attr=p.attribute/>
                    <#assign value=form.getProperty(attr)!""/>
                    <tr>
                        <td>
                            <input type="hidden" name="propertyIds" value="${attr.id}">
                            <label for="property${attr.id}" class="attribute">
                                <a href="/maintain/attribute?id=${attr.id}">${attr.name}<#if attr.unit?has_content>,
                                ${attr.unit}</#if></a>
                            </label>
                        </td>
                        <#if attr.attributeType == AttributeType.STRING>
                            <td>
                                <select id="property${attr.id}" name="propertyValues" style="width: 100%">
                                    <option value="">-- нет значения --</option>
                                    <#list p.values as v>
                                        <option value="${v}"<#if v==value> selected="selected"</#if>>${v}</option>
                                    </#list>
                                </select>
                            </td>
                            <td>
                                <button type="button">Добавить</button>
                            </td>
                        <#elseif attr.attributeType == AttributeType.BOOLEAN>
                            <td>
                                <select id="property${attr.id}" name="propertyValues" style="width: 100%">
                                    <option value="">-- нет значения --</option>
                                    <option value="false" <#if value="false">selected="selected"</#if>>нет</option>
                                    <option value="true" <#if value="true">selected="selected"</#if>>да</option>
                                </select>
                            </td>
                            <td></td>
                        <#else>
                            <td>
                                <input id="property${attr.id}" name="propertyValues" value="${value}"
                                       style="width: 100%">
                            </td>
                            <td></td>
                        </#if>
                    </tr>
                </#list>
            </table>
        </div>
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
                        <td width="100%">
                            <input style="width: 100%" id="option${id}" name="optionValues" value="${value}"/>
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
            <@bg.ui.bind path="form.participatedNames"/>
            <#assign participatedNames=bg.ui.status.actualValue!""/>

            <@bg.ui.bind path="form.participatedGroups"/>
            <#assign participatedGroups=bg.ui.status.actualValue!""/>

            <#if participatedGroups?has_content>
                <#list participatedGroups as g>
                    <#assign name=participatedNames[g_index]!""/>
                    <tr class="group">
                        <td>
                            <input type="hidden" name="participatedGroups" value="${g}">
                            <a href="/maintain/group?id=${g}" target="_blank">#${g} ${name}</a>
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
            <@bg.ui.bind path="form.relationshipNames"/>
            <#assign relationshipNames=bg.ui.status.actualValue!""/>

            <@bg.ui.bind path="form.relationshipGroups"/>
            <#assign relationshipGroups=bg.ui.status.actualValue!""/>

            <@bg.ui.bind path="form.relationshipTypes"/>
            <#assign relationshipTypes=bg.ui.status.actualValue!""/>

            <#if relationshipGroups?has_content>
                <#list relationshipGroups as r>
                    <#assign name=relationshipNames[r_index]!""/>
                    <#assign type=relationshipTypes[r_index]!""/>

                    <tr class="relationship">
                        <td>
                            <input name="relationshipTypes" type="hidden" value="${type.name()}">
                            <@message code="relationship.${type.name()?lower_case}.label"/>
                        </td>
                        <td>
                            <input name="relationshipGroups" type="hidden" value="${r}">
                            <a href="/maintain/group?id=${r}" target="_blank">#${r} ${name}</a>
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
                        <@bg.ui.productImage form i ImageSize.SMALL/>
                        <label>
                            <input name="enabledImages" type="checkbox" value="${i}"
                                   <#if enabledImages?contains(i)>checked="checked"</#if>/>
                        </label>
                        <label>
                            <input name="previewImage" type="radio" value="${i}"
                                   <#if i==previewImage>checked="checked"</#if>/>
                        </label>
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
        <label>
            <textarea style="width: 100%; min-height: 400px"
                      name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
        </label>
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
    <#list ProductState.values() as s>
        <button class="bg-ui-button<#if !bg.ui.actualValue?is_number && bg.ui.actualValue=s> selected</#if>"
                type="submit"
                name="${bg.ui.status.expression}" value="${s.name()}">${s.name()}</button>
    </#list>
    </td>
</tr>
</table>
</form>
</div>

<#if form.id?has_content>
<div id="attributeValue" style="display: none; white-space: nowrap">
    <form>
        <input name="categoryId" value="${category.id}" type="hidden">
        <input name="attributeId" type="hidden">
        <label>
            <input name="value" style="width: 100%">
        </label>
    </form>
</div>
</#if>

<script type="application/javascript">
window.onload = function () {
    CKEDITOR.replace('description');
};

var colors = {
    'black': 'Черный',
    'blue': 'Синий',
    'green': 'Зеленый',
    'grey': 'Серый',
    'orange': 'Оранжевый',
    'light green': 'Светло-зеленый',
    'dark green': 'Темно-зеленый',
    'pink': 'Розовый',
    'purple': 'Пурпурный',
    'dark grey': 'Темно-серый',
    'light grey': 'Светло-серый',
    'apricot pink': 'Абрикосово-розовый',
    'grayish blue': 'Серовато-Синий',
    'peacock blue': 'Переливчатый-синий',
    'light pink': 'Светло-розовый',
    'dark blue': 'Темно-синий',
    'red': 'Красный',
    'rose red': 'Бордовый',
    'white': 'Белый',
    'yellow': 'Желтый',
    'peach': 'Персиковый',
    'silver': 'Серебряный',
    'gold': 'Золотой',
    'brown': 'Коричневый',
    'sky blue': 'Небесно-голубой',
    'olive green': 'Оливково-зеленый',
    'light orange': 'Небесно-голубой',
    'dark brown': 'Темно-коричневый',
    'grass blue': 'Бирюзовый',
    'light blue': 'Голубой',
    'light brown': 'Светло-коричневый',
    'golden': 'Золотой',
    'dark purple': 'Темно-пурпурный',
    'peach red': 'Красный персик',
    'beige': 'Бежевый'
};

var attributes = {
<#list attributes as a>
    '${a.id}': {name: "${a.name}", unit: "${a.unit}"}<#if a_has_next>,</#if>
</#list>
};

<#if form.id?has_content>
var showCategoryEditor = function () {
    $("#categoryDiv>div").toggle();
};

var loadSupplierDescription = function () {
    var siEl = $("#supplierInfo");
    var dataEl = siEl.find(".data");
    var actionEl = siEl.find(".action span");

    dataEl.html('');
    actionEl.toggle();
    $.post("/maintain/product/loadSupplierInfo.ajax?id=${form.id}")
            .done(function (response) {
                if (response.success) {
                    var data = response.data;

                    var info = "";

                    info += "<table>";
                    if (data.price != null) {
                        info += "  <tr><td><label>Цена:</label></td><td>" + data.price.amount + " (" + data.price.primordialAmount + ")</td></tr>";
                    }

                    if (data.stockInfo != null) {
                        info += "  <tr><td><label>Доступность:</label></td><td>";

                        if (data.stockInfo.leftovers != null) {
                            info += "осталось " + data.stockInfo.leftovers;
                        } else if (data.stockInfo.restockDate != null) {
                            info += "дата поставки " + data.stockInfo.restockDate;
                        } else {
                            info += "доступен";
                        }
                        info += "</td></tr>";
                    }

                    $.each(data.parameters, function (key, value) {
                        info += "  <tr>";
                        info += "    <td><label>" + key + "</label></td>";

                        var vals = '';
                        $.each(value, function (i, v) {
                            var items = v.split('+');
                            $.each(items, function (i, v) {
                                var cv = colors[v.toLowerCase()];
                                vals += cv == undefined ? v : cv;
                                if (i != items.length - 1) {
                                    vals += '+';
                                }
                            });
                            if (i != value.length - 1) {
                                vals += ';';
                            }
                        });
                        info += "    <td>" + vals + "</td>";
                        info += "  </tr>";
                    });
                    info += "</table>";
                    dataEl.html(info);
                    actionEl.toggle();
                    bg.ui.unlock(actionEl);
                } else {
                    actionEl.toggle();
                    bg.ui.unlock(actionEl, response.message, true);
                }
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                actionEl.toggle();
                bg.ui.unlock(actionEl, "По техническим причинам сообщение не может быть отправлено в данный момент. " +
                        "Пожалуйста, попробуйте отправить сообщение позже.", true);
            });
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
</#if>

var recalculatePrice = function (val) {
    var v = parseFloat(val);
    return ${priceConverter.formula("v", "Math.round", MarkupType.REGULAR)};
};

$("#supplierPrice").change(function () {
    $("#price").val(recalculatePrice($(this).val()));
});

$("#supplierPrimordialPrice").change(function () {
    $("#primordialPrice").val(recalculatePrice($(this).val()));
});

var optionsTable = $("#optionsTable");
optionsTable.find("button.add").click(addOption);
optionsTable.find("button.remove").click(removeOption);

var imagesTable = $("#imagesTable");
imagesTable.find("button.add").click(addImage);
imagesTable.find("button.remove").click(removeImage);

var groupsTable = $("#groupsTable");
groupsTable.find("button.add").click(addGroup);
groupsTable.find("button.remove").click(removeGroup);

var relationshipsTable = $("#relationshipsTable");
relationshipsTable.find("button.add").click(addRelationship);
relationshipsTable.find("button.remove").click(removeRelationship);

$("#supplierReferenceId").change(function () {
    $("#supplierReferenceLink").attr('href', 'http://www.banggood.com' + $(this).val());
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

var attributeValueDialog = $("#attributeValue");

var addNewAttributeValue = function (attrId) {
    bg.ui.lock(null, "Добавление...");
    var serializeObject = attributeValueDialog.find('form').serializeObject();
    $.post("/maintain/category/parameterAddValue.ajax", JSON.stringify(serializeObject))
            .done(function (response) {
                if (response.success) {
                    bg.ui.unlock(null, "Атрибут добавлен", false);
                    var value = serializeObject['value'];
                    $("#productParameters").find("#property" + attrId).append("<option value='" + value + "'>" + value + "</option>").val(value);
                } else {
                    bg.ui.unlock(null, response.message, true);
                }
                attributeValueDialog.dialog("close");
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                bg.ui.unlock(null, "По техническим причинам сообщение не может быть отправлено в данный момент. " +
                        "Пожалуйста, попробуйте отправить сообщение позже.", true);
                attributeValueDialog.dialog("close");
            });
};

$("#productParameters").find("button").click(function () {
    var el = $(this).parent().parent();
    var attrId = el.find('input').val();
    attributeValueDialog.find("input[name=attributeId]").val(attrId);
    attributeValueDialog.dialog({
        title: 'Добавление нового значения',
        height: 'auto',
        width: 350,
        modal: true,
        buttons: {
            "Добавить": function () {
                addNewAttributeValue(attrId);
            },
            "Отменить": function () {
                $(this).dialog("close");
            }
        }
    });
});

$(".image img").click(function () {
    $(this).parent().find("input[name=previewImage]").prop('checked', 'checked');
});

$("#restockDate").datepicker({ "dateFormat": "yy.mm.dd"});

$("#name").change(function () {
    $.post("/maintain/product/symbolic.ajax?name=" + $(this).val())
            .done(function (response) {
                if (response.success) {
                    $("#symbolic").val(response.data);
                }
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                bg.ui.unlock(null, "По техническим причинам сообщение не может быть отправлено в данный момент. " +
                        "Пожалуйста, попробуйте отправить сообщение позже.", true);
            });
});

$("#notAvailable").click(function () {
    $("#storeAvailable").val("0");
});

$("#available").click(function () {
    $("#storeAvailable").val("");
});
</script>