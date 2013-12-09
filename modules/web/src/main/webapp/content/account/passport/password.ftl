<#include "/core.ftl">
<#include "tools.ftl">

<@form "/account/passport/password" "title.account.passport.password">
<table>
    <tr>
        <td nowrap="nowrap">
            <label for="password">Пароль:</label>
        </td>
        <td width="100%">
            <@bg.ui.input path="form.password" fieldType="password"/>
        </td>
    </tr>
    <tr>
        <td nowrap="nowrap">
            <label for="confirm">Подтверждение:</label>
        </td>
        <td width="100%">
            <@bg.ui.input path="form.confirm" fieldType="password"/>
        </td>
    </tr>
</table>
</@form>