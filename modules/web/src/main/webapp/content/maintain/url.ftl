<#-- @ftlvariable name="url" type="String" -->
<#-- @ftlvariable name="params" type="String" -->
<#-- @ftlvariable name="response" type="String" -->

<form action="/maintain/service/url" method="get">
    <div>URL: <input style="width: 100%" name="url" value="${url!""}"></div>
    <div>Params: <textarea style="width: 100%" rows="10" name="params">${params!""}</textarea></div>
    <div>
        <button>Получить</button>
    </div>
    <div>

        <textarea rows="50" style="width: 100%">${response!""}</textarea>
    </div>
</form>