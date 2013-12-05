<#-- @ftlvariable name="timeZones" type="billiongoods.server.services.timezone.TimeZoneEntry[]" -->

<select name="timezone">
<#list timeZones as z>
    <option value="${z.id}">${z.displayName}</option>
</#list>
</select>

