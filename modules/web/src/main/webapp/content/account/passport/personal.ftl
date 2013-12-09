<#-- @ftlvariable name="timeZones" type="billiongoods.server.services.timezone.TimeZoneEntry[]" -->
<#include "/core.ftl">
<#include "tools.ftl">

<@form "/account/passport/personal" "title.account.passport.personal">
    <@bg.ui.input path="form.language" fieldType="hidden"/>
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
                        <option <#if z.id==bg.ui.status.value>selected="selected"</#if>
                                value="${z.id}">${z.displayName}</option>
                    </#list>
                </select>
            </@bg.ui.field>
        </td>
    </tr>
</table>
</@form>