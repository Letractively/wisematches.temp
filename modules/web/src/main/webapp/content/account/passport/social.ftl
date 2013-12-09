<#-- @ftlvariable name="providers" type="java.lang.String[]" -->
<#-- @ftlvariable name="connections" type="java.util.Map<String, org.springframework.social.connect.Connection<?>>" -->
<div class="settings">
    <div class="tit">
        Социальные профили
    </div>

    <div class="cnt">
    <#list providers as p>
        <#assign c=connections[p]>
        <div>
        ${p} -> ${c}
        </div>
    </#list>
    </div>
</div>
