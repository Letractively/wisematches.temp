<#-- @ftlvariable name="timeZones" type="billiongoods.server.services.timezone.TimeZoneEntry[]" -->
<#include "/core.ftl">

<form action="/account/settings/modify" method="post">
    <div class="settings">
        <div class="tit">
            Изменение персональных данных
        </div>
        <div class="cnt">
            <table>
                <tr>
                    <td>
                        <label for="username">
                            Имя и фамилия:
                        </label>
                    </td>
                    <td>
                    <@bg.ui.input path="form.username"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="timeZone">Часовой пояс:</label>
                    </td>
                    <td>
                    <@bg.ui.field "form.timeZone">
                        <select id="timeZone" name="timeZone">
                            <#list timeZones as z>
                                <option <#if z.id=bg.ui.status.value>selected="selected" </#if>
                                        value="${z.id}">${z.displayName}</option>
                            </#list>
                        </select>
                    </@bg.ui.field>
                    </td>
                </tr>
            </table>
        </div>
        <div class="question">
            <button>Сохранить</button>
            <button type="button">Отмена</button>
        </div>
    </div>
</form>