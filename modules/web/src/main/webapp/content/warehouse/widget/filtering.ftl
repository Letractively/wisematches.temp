<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="filter" type="billiongoods.server.warehouse.ProductFilter" -->
<#-- @ftlvariable name="filtering" type="billiongoods.server.warehouse.FilteringAbility" -->
<#-- @ftlvariable name="pageableForm" type="billiongoods.server.web.servlet.mvc.warehouse.form.PageableForm" -->

<#include "/core.ftl"/>

<#assign itemsCount=0/>

<#macro categoryPrameter a v n>
    <#assign count=filtering.getValue(a, v)/>
<li class="item <#if count=0>disabled</#if>">
    <input id="parameter_${a.id}_${v}" type="checkbox" name="${a.id}" value="${v}"
           <#if count=0>disabled="disabled"
           <#elseif filter?? && filter.isAllowed(a, v)>checked="checked"
           </#if> />
    <label for="parameter_${a.id}_${v}">${n} (${count})</label>
</li>
</#macro>

<#macro categoryPramaters category>
    <#list category.parameters as p>
        <#assign a=p.attribute/>
        <#if (a.attributeType=AttributeType.ENUM) && (p.values?size>0)>
            <#assign itemsCount=itemsCount+1/>
        <div class="property">
            <div class="name">
            ${a.name}<#if a.unit?has_content>, ${a.unit}</#if>
            </div>

            <ul class="items">
                <#list p.values as v>
                    <@categoryPrameter a v v/>
                </#list>
                    <@categoryPrameter a "" "все остальные"/>
            </ul>
        </div>
        </#if>
    </#list>
</#macro>

<#if category?? && filtering?? && pageableForm??>
<div id="productsFilterForm" class="filtering">
    <#list category.genealogy.parents as c>
        <@categoryPramaters c/>
    </#list>

    <@categoryPramaters category/>
</div>

    <#if (itemsCount>0)>
    <script type="text/javascript">
        new bg.warehouse.Filter('<@bg.ui.tableNavigationParams pageableForm "filter" ""/>');
    </script>
    <#else>
    <style type="text/css">
        #productsFilterForm {
            display: none;
        }
    </style>
    </#if>
</#if>

