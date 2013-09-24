<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="attributes" type="billiongoods.server.warehouse.Attribute[]" -->

<#include "/core.ftl">

<#macro attr a>
${a.name}<#if a.unit?has_content>, <strong>${a.unit}</strong></#if> <span class="sample">(${a.attributeType}
    )</span></#macro>

<div style="padding: 10px; border: 1px solid gray;">
    <form action="/maintain/category" method="post">

        <table>
            <tr>
                <td valign="top">
                    <table style="width: 100%">
                    <#if category??>
                        <tr>
                            <td valign="top"><label for="id">Номер: </label></td>
                            <td>
                                <@bg.ui.input path="form.id" fieldType="hidden">
                                        ${bg.ui.status.value!""}
                                </@bg.ui.input>
                            </td>
                        </tr>
                    </#if>
                        <tr>
                            <td valign="top"><label for="name">Имя: </label></td>
                            <td>
                            <@bg.ui.input path="form.name"/>
                            </td>
                        </tr>

                        <tr>
                            <td valign="top"><label for="position">Положение: </label></td>
                            <td>
                            <@bg.ui.input path="form.position"/>
                            </td>
                        </tr>

                        <tr>
                            <td valign="top"><label for="parent">Родитель: </label></td>
                            <td>
                            <@bg.ui.selectCategory "form.parent" catalog true/>
                            </td>
                        </tr>

                        <tr>
                            <td valign="top"><label for="description">Описание: </label></td>
                            <td>
                            <@bg.ui.field path="form.description">
                                <textarea rows="5" style="width: 100%"
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
                            <#if category??>
                                <button id="add" type="submit">Изменить</button>
                            <#else>
                                <button id="add" type="submit">Создать</button>
                            </#if>
                            </td>
                        </tr>
                    </table>
                </td>
                <td valign="top" style="padding-left: 10px">
                <#if category??>
                    <div id="categoryParameters">
                        <#list category.parameters as p>
                            <#assign a=p.attribute/>
                            <div>
                                <div class="parameter-name">
                                    <input name="parameter" value="${a.id}" type="hidden"/>
                                    <label><@attr a/></label>
                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                    <button type="button">Добавить</button>
                                </div>

                                <div class="parameter-values">
                                    <ul>
                                        <#if p.values??>
                                            <#list p.values as v>
                                                <li>${v}</li>
                                            </#list>
                                        </#if>
                                    </ul>
                                </div>
                            </div>
                        </#list>
                    </div>
                    <button id="addAttribute" type="button">Добавить атрибут</button>
                </#if>
                </td>
            </tr>
        </table>
    </form>
</div>

<#if category??>
<div id="attributesList" style="display: none">
    <ul>
        <#list attributes as a>
            <li>
                <input id="attribute_${a.id}" type="checkbox" value="${a.id}">
                <label for="attribute_${a.id}"><@attr a/></label>
            </li>
        </#list>
    </ul>
</div>

<div id="attributeValue" style="display: none; white-space: nowrap">
    <form>
        <input name="categoryId" value="${category.id}" type="hidden">
        <input name="attributeId" type="hidden">
        <label>
            <input name="value">
        </label>
        <button type="button">Добавить</button>
    </form>
</div>

<script type="text/javascript">
    var attributeValueDialog = $("#attributeValue");

    $("#categoryParameters").find("button").click(function () {
        var el = $(this).parent().parent();
        attributeValueDialog.find("input[name=attributeId]").val(el.find('input').val());
        attributeValueDialog.modal();
    });

    attributeValueDialog.find("button").click(function () {
        var serializeObject = attributeValueDialog.find('form').serializeObject();
        $.post("/maintain/category/parameterAddValue.ajax", JSON.stringify(serializeObject))
                .done(function (response) {
                    if (response.success) {
                        bg.ui.unlock(null, "Атрибут добавлен", false);
                    } else {
                        bg.ui.unlock(null, response.message, true);
                    }
                    bg.util.url.reload();
                })
                .fail(function (jqXHR, textStatus, errorThrown) {
                    bg.ui.unlock(null, "По техническим причинам сообщение не может быть отправлено в данный момент. " +
                            "Пожалуйста, попробуйте отправить сообщение позже.", true);
                });
    });

    $("#attributesList").find("input").change(function () {
        var v = $(this);
        if (v.prop('checked')) {
            attributeValueDialog.find('input[name=attributeId]').val(v.val());
            var serializeObject = attributeValueDialog.find('form').serializeObject();
            $.post("/maintain/category/parameterAdd.ajax", JSON.stringify(serializeObject))
                    .done(function (response) {
                        if (response.success) {
                            bg.ui.unlock(null, "Атрибут добавлен", false);
                        } else {
                            bg.ui.unlock(null, response.message, true);
                        }
                    })
                    .fail(function (jqXHR, textStatus, errorThrown) {
                        bg.ui.unlock(null, "По техническим причинам сообщение не может быть отправлено в данный момент. " +
                                "Пожалуйста, попробуйте отправить сообщение позже.", true);
                    });
        } else {

        }
        $.modal.close();
        bg.util.url.reload();
    });

    $("#addAttribute").click(function () {
        var al = $("#attributesList");

        al.find("input").prop("checked", false);
        $("#categoryParameters").find("input").each(function (i, v) {
            var id = $(v).val();
            $("#attribute_" + id).prop("checked", true);
        });
        al.modal();
    });
</script>
</#if>