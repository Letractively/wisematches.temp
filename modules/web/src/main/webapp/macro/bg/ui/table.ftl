<#include "/macro/message.ftl"/>

<#macro dtinit>
<script type="text/javascript">
    wm.ui.dataTable = function (selector, opts) {
        var $2 = $(selector);
        var dataTableLanguage = {
            "oLanguage": {
                "sProcessing": "<@message code="datatable.processing.label"/>",
                "sLengthMenu": "<@message code="datatable.menu.label"/>",
                "sZeroRecords": "<@message code="datatable.zero.records.label"/>",
                "sInfo": "_START_ - _END_ <@message code="datatable.of.label"/> _TOTAL_",
                "sInfoEmpty": "<@message code="datatable.info.empty.label"/>",
                "sLoadingRecords": "<@message code="datatable.loading.records.label"/>",
                "oPaginate": {
                    "sFirst": "&nbsp;",
                    "sPrevious": "&nbsp",
                    "sNext": "&nbsp",
                    "sLast": "&nbsp"
                }
            }
        };
        return $2.dataTable($.extend(true, dataTableLanguage, opts));
    };
</script>
</#macro>

<#macro header class="" align="left">
<div class="data-table-header ui-widget-header ui-corner-top ${class}" style="text-align: ${align}"><#nested/></div>
</#macro>

<#macro toolbar class="" align="right">
<div class="data-table-toolbar ui-widget-content ${class}">
    <div class="ui-state-hover" style="text-align: ${align}"><#nested/></div>
</div>
</#macro>

<#macro content class="" wrap=false>
    <#if wrap>
    <div class="data-table-content ${class}">
    <div class="ui-widget-content"></#if><#nested/><#if wrap></div></div></#if>
</#macro>

<#macro statusbar class="" align="right">
<div class="data-table-bottom ${class}">
    <div class="ui-widget-content" style="text-align: ${align}"><#nested/></div>
</div>
</#macro>

<#macro footer class="">
<div class="data-table-footer ui-corner-bottom ${class}" style="padding: 5px"><#nested/></div>
</#macro>
