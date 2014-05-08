<#-- @ftlvariable name="showcase" type="billiongoods.server.services.showcase.Showcase" -->

<table>
    <tr>
        <th>Section</th>
        <th>Position</th>
        <th>Name</th>
        <th>Category</th>
        <th>Arrival</th>
        <th>SubCategories</th>
        <th>Action</th>
    </tr>

<#list showcase.showcaseGroups as g>
    <#list g.showcaseItems as i>
        <tr id="showcase-${i.section}-${i.position}">
            <td>${i.section}</td>
            <td>${i.position}</td>
            <td>${i.name}</td>
            <td>${i.category}</td>
            <td>${i.arrival?string}</td>
            <td>${i.subCategories?string}</td>
            <td>
                <form action="/maintain/showcase/remove" method="post">
                    <input type="hidden" name="section" value="${i.section}"/>
                    <input type="hidden" name="position" value="${i.position}"/>

                    <button type="button" name="action" value="edit">Изменить</button>
                    <button type="submit" name="action" value="remove">Удалить</button>
                </form>
            </td>
        </tr>
    </#list>
</#list>
</table>

<div style="border-top: 1px solid #808080; border-bottom: 1px solid #808080">
    <form id="showcaseItemForm" action="/maintain/showcase/update" method="post">
        <table style="padding-top: 10px; padding-bottom: 10px; width: auto">
            <tr>
                <th><label for="section">Секция:</label></th>
                <td>
                    <input id="section" name="section">
                </td>
            </tr>
            <tr>
                <th><label for="position">Позиция:</label></th>
                <td>
                    <input id="position" name="position">
                </td>
            </tr>
            <tr>
                <th><label for="name">Наименование:</label></th>
                <td>
                    <input id="name" name="name">
                </td>
            </tr>
            <tr>
                <th><label for="category">Категория:</label></th>
                <td>
                    <input id="category" name="category">
                </td>
            </tr>
            <tr>
                <th><label for="arrival">Только новые:</label></th>
                <td>
                    <input id="arrival" name="arrival" type="checkbox">
                </td>
            </tr>
            <tr>
                <th><label for="subCategories">Включая подкатегории:</label></th>
                <td>
                    <input id="subCategories" name="subCategories" type="checkbox">
                </td>
            </tr>

            <tr>
                <td></td>
                <td>
                    <button type="submit">Изменить</button>
                </td>
            </tr>
        </table>
    </form>
</div>

<form action="/maintain/showcase/reload" method="post">
    <button value="reload">Перезагрузить витрину</button>
</form>

<script type="application/javascript">
    $("button[value='edit']").click(function () {
        var form = $(this).closest('form');

        var section = form.find("input[name='section']").val();
        var position = form.find("input[name='position']").val();

        var editForm = $("#showcaseItemForm").find("table tr");
        var editItem = $("#showcase-" + section + "-" + position).find("td");

        for (var i = 0; i < 6; i++) {
            var val = $(editItem[i]).text();
            var input = $(editForm[i]).find("input");
            if ("checkbox" == input.attr('type')) {
                input.prop('checked', "true" == val);
            } else {
                input.val(val);
            }
        }
    })
</script>