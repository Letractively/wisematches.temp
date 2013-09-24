<#-- @ftlvariable name="attribute" type="billiongoods.server.warehouse.Attribute" -->
<#-- @ftlvariable name="attributes" type="billiongoods.server.warehouse.Attribute[]" -->

<#include "/core.ftl">

<div style="padding: 10px; border: 1px solid gray;">
    <form action="/maintain/attribute" method="post">
    <@bg.ui.input path="form.id" fieldType="hidden"/>

        <table style="width: 200px">
            <tr>
                <td valign="top"><label for="n">Имя: </label></td>
                <td>
                <@bg.ui.input path="form.name"/>
                </td>
            </tr>
            <tr>
                <td valign="top"><label for="u">Единицы измерения: </label></td>
                <td>
                <@bg.ui.input path="form.unit"/>
                </td>
            </tr>
            <tr>
                <td valign="top"><label for="u">Описание: </label></td>
                <td>
                <@bg.ui.field path="form.description">
                    <textarea rows="3" style="width: 100%"
                              name="${bg.ui.status.expression}">${bg.ui.statusValue}</textarea>
                </@bg.ui.field>
                </td>
            </tr>
            <tr>
                <td valign="top"><label for="u">Тип: </label></td>
                <td>
                <@bg.ui.bind "form.attributeType"/>
                    <select id="attributeType" name="${bg.ui.status.expression}" style="width: 100%">
                    <#list AttributeType.values() as t>
                        <option value="${t.name()}"
                                <#if bg.ui.actualValue=t>selected="selected"</#if>>${t.name()}</option>
                    </#list>
                    </select>
                </td>
            </tr>

            <tr>
                <td colspan="2"><@bg.ui.spring.showErrors "br"/> </td>
            </tr>

            <tr>
                <td></td>
                <td>
                <#if attribute??>
                    <button id="add" type="submit">Изменить</button>
                <#else>
                    <button id="add" type="submit">Создать</button>
                </#if>
                </td>
            </tr>
        </table>
    </form>
</div>

<div style="padding-top: 20px">
<#list attributes as a>
    <span style="white-space: nowrap"><a
            href="/maintain/attribute?id=${a.id}"
            ><strong>${a.name}</strong><#if a.unit?has_content>, ${a.unit}</#if></a></span><#if a_has_next>; </#if>
</#list>
</div>
