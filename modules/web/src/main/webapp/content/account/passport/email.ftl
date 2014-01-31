<#include "/core.ftl">
<#include "tools.ftl">

<@form "/account/passport/email" "title.account.passport.email">
<table>
    <tr>
        <td nowrap="nowrap">
            <label for="email">
                Адрес электронной почты:
            </label>
        </td>
        <td width="100%">
            <@bg.ui.input path="form.email"/>
        </td>
    </tr>
</table>
</@form>