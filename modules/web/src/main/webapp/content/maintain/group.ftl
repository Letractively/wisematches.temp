<#-- @ftlvariable name="group" type="billiongoods.server.warehouse.Group" -->
<#-- @ftlvariable name="groups" type="billiongoods.server.warehouse.Group[]" -->

<#include "/core.ftl">

<table cellspacing="5">
    <tr>
        <td valign="top" style="padding: 10px; border: 1px solid gray;">
            <div>
                <form action="/maintain/group" method="post">
                    <table style="width: 200px">
                    <#if group??>
                        <tr>
                            <td><label for="n">Номер: </label></td>
                            <td>
                            ${group.id}
                            </td>
                        </tr>
                    </#if>

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
                                <button id="search" name="action" value="search" type="submit">Найти</button>
                            </#if>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
        </td>
    <#if groups??>
        <td valign="top" style="padding: 10px; border: 1px solid gray;">
            <div>
                <#list groups as g>
                    <div>
                        <a href="/maintain/group?id=${g.id}">${g.name} (${g.descriptions?size})</a>
                    </div>
                </#list>
            </div>
        </td>
    </#if>
    </tr>
</table>

<#if group??>
<div style="padding-top: 20px">
    <table>
        <#if group.descriptions?size != 0>
            <#list group.descriptions as a>
                <tr>
                    <td><@bg.link.article a>${messageSource.getArticleCode(a)}</@bg.link.article></td>
                    <td width="100%">${a.name}</td>
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