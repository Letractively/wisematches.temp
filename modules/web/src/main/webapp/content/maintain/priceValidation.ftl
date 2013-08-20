<#-- @ftlvariable name="active" type="boolean" -->


<#if active>
Price validation is in proggress. Nothing to do here.
<#else>
<form action="/maintain/service/validatePrices" method="post">
    <button>Запустить проверку</button>
</form>
</#if>