<#-- @ftlvariable name="groups" type="billiongoods.server.warehouse.Group[]" -->
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
                        <tr>
                            <td valign="top"><label for="id">Номер: </label></td>
                            <td>
                            <@bg.ui.input path="form.id" fieldType="hidden">${bg.ui.statusValue}</@bg.ui.input>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top"><label for="name">Имя: </label></td>
                            <td>
                            <@bg.ui.input path="form.name"/>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top"><label for="name">URL имя: </label></td>
                            <td>
                            <@bg.ui.field path="form.symbolic">
                                <textarea id="${bg.ui.status.expression}" rows="2" style="width: 100%;"
                                          readonly="readonly"
                                          name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
                            </@bg.ui.field>
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
                            <#if form.id??>
                                <button id="add" type="submit">Изменить</button>
                            <#else>
                                <button id="add" type="submit">Создать</button>
                            </#if>
                            </td>
                        </tr>
                    </table>
                </td>
                <td valign="top" style="padding-left: 10px">
                <#list attributes as a>
                    <@bg.ui.bind path="form.attributes"/>
                    <#assign enabledAttributes=bg.ui.status.actualValue!""/>

                    <div>
                        <input id="attribute${a.id}" type="checkbox" name="attributes" value="${a.id}"
                               <#if enabledAttributes?is_sequence && enabledAttributes?seq_contains(a.id)>checked="checked"</#if>>
                        <label for="attribute${a.id}">${a.name}, ${a.unit}</label>
                    </div>
                </#list>
                </td>
            </tr>
        <#if groups??>
            <tr>
                <td colspan="2">
                    <#list groups as g>
                        <a href="/maintain/group?id=${g.id}" target="_blank">#${g.id} ${g.name}</a>
                    </#list>
                </td>
            </tr>
        </#if>
        </table>
    </form>
</div>

<script type="application/javascript">
    $("#name").change(function () {
        $.post("/maintain/category/symbolic.ajax?name=" + $(this).val())
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
</script>