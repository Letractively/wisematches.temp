<#-- @ftlvariable name="result" type="boolean" -->
<#-- @ftlvariable name="error" type="java.lang.String" -->
<#-- @ftlvariable name="attributes" type="billiongoods.server.warehouse.Attribute[]" -->

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
            <@bg.ui.selectCategory path="form.category" catalog=catalog root=false/>
            </td>
        </tr>
        <tr>
            <td>
                <label for="file">Изображения: </label>
            </td>
            <td>
                <input type="file" name="images">
            </td>
        </tr>
        <tr>
            <td>
                <label for="file">Описание: </label>
            </td>
            <td>
                <input type="file" name="description">
            </td>
        </tr>
        <tr>
            <td>
                <label for="group">Состоит в группе:</label>
            </td>
            <td>
            <@bg.ui.input "form.participatedGroups"/>
            </td>
        </tr>
    <#if attributes??>
        <tr>
            <td>
                <label>Аттритубы:</label>
            </td>
            <td>
                <table>
                    <#list attributes as a>
                        <tr>
                            <td>
                                <label>${a.name}</label>
                                <input type="hidden" name="propertyIds" value="${a.id}">
                            </td>
                            <td>
                                <input name="propertyValues" value="">
                            </td>
                        </tr>
                    </#list>
                </table>
            </td>
        </tr>
    </#if>
        <tr>
            <td colspan="2">
                <hr>
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

<script type="application/javascript">
    $("select[name=category]").change(function () {
        bg.util.url.redirect("/maintain/article/import?c=" + $(this).val());
    });
</script>