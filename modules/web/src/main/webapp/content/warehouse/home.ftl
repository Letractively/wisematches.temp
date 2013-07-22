<table>
    <tr>
        <td rowspan="2" style="width: 10px; white-space: nowrap">
            <ul>
            <#list catalog.catalogItems as c>
                <li>${c.name} - ${c.id}</li>
            </#list>
            </ul>
        </td>

        <td>
            Section 1 > Section 2
        </td>
    </tr>

    <tr>
        <td>
            Woh, it's big table here!
        </td>
    </tr>
</table>