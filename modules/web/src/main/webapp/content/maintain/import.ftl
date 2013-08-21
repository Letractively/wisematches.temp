<#-- @ftlvariable name="result" type="boolean" -->
<#-- @ftlvariable name="error" type="java.lang.String" -->

<#include "/core.ftl">

<#if result??>
<div>
    <#if result>
        Сделано
    <#else>
        Ошибка: ${error}
    </#if>
</div>
</#if>

<form action="/maintain/article/import" method="post" enctype="multipart/form-data">
    <table style="width: 100%">
        <tr>
            <td valign="top">
                <label for="category">Категория: </label>
            </td>
            <td>
                <select name="category">
                <#list catalog.rootCategories as c>
                    <@bg.ui.categoryOption c 0 ""/>
                </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td>
                <label for="file">Файл: </label>
            </td>
            <td>
                <input type="file" name="file">
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <button type="submit">Импортировать</button>
            </td>
        </tr>
    </table>
</form>