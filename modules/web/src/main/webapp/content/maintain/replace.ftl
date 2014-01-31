<#-- @ftlvariable name="updatedCount" type="int" -->

<#include "/core.ftl"/>

<#if updatedCount??>
<div align="center" style="font-size: 18px">
    Обновлено ${updatedCount} описаний.
</div>
</#if>

<form action="/maintain/product/replace" method="post">
    Заменить текст во всех описаниях с:
<@bg.ui.input "form.from"/>
    на
<@bg.ui.input "form.to"/>
    <button type="submit">Выполнить</button>
</form>