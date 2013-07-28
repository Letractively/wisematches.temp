<#-- @ftlvariable name="attributes" type="billiongoods.server.warehouse.Attribute[]" -->

<div style="padding: 10px; border: 1px solid gray;">
    <form action="/maintain/attributes/add">
        <table style="width: 200px">
            <tr>
                <td><label for="n">Имя: </label></td>
                <td><input name="n" id="n"></td>
            </tr>
            <tr>
                <td><label for="u">Единицы измерения: </label></td>
                <td><input name="u" id="u"></td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <button id="add" type="submit">Добавить новый</button>
                </td>
            </tr>
        </table>
    </form>
</div>

<div>
<#list attributes as a>
    <div>
        <span>${a.name}</span>
        <span> (${a.unit})</span>
    </div>
</#list>
</div>
