<#-- @ftlvariable name="group" type="billiongoods.server.warehouse.Group" -->

<#include "/core.ftl">

<div style="padding: 10px; border: 1px solid gray;">
    <form action="/maintain/group" method="post">
        <table style="width: 200px">
            <tr>
                <td><label for="n">Имя: </label></td>
                <td>
                <@bg.ui.input path="form.name"/>
                </td>
            </tr>

            <tr>
                <td></td>
                <td>
                <@bg.ui.input path="form.id" fieldType="hidden"/>
                <#if group??>
                    <button id="add" name="action" value="update" type="submit">Изменить</button>
                    <button id="remove" name="action" value="remove" type="submit">Удалить</button>
                <#else>
                    <button id="add" name="action" value="create" type="submit">Создать</button>
                </#if>
                </td>
            </tr>
        </table>
    </form>
</div>

<#if group??>
<div style="padding-top: 20px">
    <table>
        <#if group.descriptions?size != 0>
            <#list group.descriptions as a>
                <tr>
                    <td>a.code</td>
                    <td width="100%">a.name</td>
                    <td>
                        <button type="button">Удалить</button>
                    </td>
                </tr>
            </#list>
        <#else>
            <tr>
                <td>В этой группе нет ни одного элемента</td>
            </tr>
        </#if>
    </table>
</div>
</#if>