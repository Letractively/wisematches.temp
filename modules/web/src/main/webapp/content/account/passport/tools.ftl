<#include "/core.ftl">

<#macro form action title>
<form action="${action}" method="post">
    <div class="settings">
        <div class="tit">
            <@message code=title/>
        </div>
        <div class="cnt">
            <#nested/>
        </div>
        <div class="question" style="padding-top: 20px">
            <button>Сохранить</button>
            <button type="button" onclick="location.href='/account/passport/view'">Отмена</button>
        </div>
    </div>
</form>
</#macro>