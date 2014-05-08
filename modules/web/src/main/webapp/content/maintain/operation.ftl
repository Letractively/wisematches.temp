<#include "/core.ftl"/>

<form action="/maintain/sales/operation" method="post">
    Закрыть продажи с:
<@bg.ui.input "form.stopSalesDate"/>
    до
<@bg.ui.input "form.startSalesDate"/>

    <button type="submit" name="action" value="set">Обновить</button>
    <button type="submit" name="action" value="remove">Удалить</button>
</form>

<script type="application/javascript">
    $("#stopSalesDate").datepicker({ "dateFormat": "yy-mm-dd"}); // ISO 8601
    $("#startSalesDate").datepicker({ "dateFormat": "yy-mm-dd"}); // ISO 8601
</script>