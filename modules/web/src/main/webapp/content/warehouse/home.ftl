<table cellpadding="0" cellspacing="0">
    <tr>
        <td rowspan="2" style="width: 210px;">
            <ul style="border: 1px solid #ebebeb">
            <#list catalog.catalogItems as c>
                <li>${c.name} - ${c.id}</li>
            </#list>
            </ul>
        </td>

        <td>
            <div style="margin-left: 5px; border: 1px solid #ebebeb">
                Section 1 > Section 2
            </div>
        </td>
    </tr>

    <tr>
        <td>
            <div style="margin-left: 5px; border: 1px solid #ebebeb">
                Woh, it's big table here!
            </div>
        </td>
    </tr>
</table>