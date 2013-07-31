<#-- @ftlvariable name="catalog" type="billiongoods.server.warehouse.Catalog" -->
<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="attributes" type="billiongoods.server.warehouse.Attribute[]" -->

<#include "/core.ftl">

<#macro option category level selected>
<option <#if category.id?string==selected>selected="selected"</#if> value="${category.id}"><#list 0..level as i>
    -- </#list>${category.name} /${category.id}</option>
    <#list category.children as c>
        <@option c level+1 selected/>
    </#list>
</#macro>

<div style="padding: 10px; border: 1px solid gray;">
    <form action="/maintain/category" method="post">
    <@bg.ui.input path="form.id" fieldType="hidden"/>

        <table>
            <tr>
                <td valign="top">
                    <table style="width: 100%">
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
                            <@bg.ui.field path="form.parent">
                                <select name="parent">
                                    <option value="">ROOT</option>
                                    <#list catalog.rootCategories as c>
                                        <@option c 0 bg.ui.statusValue/>
                                    </#list>
                                </select>
                            </@bg.ui.field>
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
