<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="attributes" type="billiongoods.server.warehouse.StoreAttribute[]" -->

<#include "/core.ftl">

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
                <#list attributes as a>
                    <div>
                        <input id="attribute${a.id}" type="checkbox" name="attributes" value="${a.id}"
                               <#if category?? && category.attributes.contains(a)>checked="checked"</#if>>
                        <label for="attribute${a.id}">${a.name}, ${a.unit}</label>
                    </div>
                </#list>
                </td>
            </tr>
        </table>
    </form>
</div>
