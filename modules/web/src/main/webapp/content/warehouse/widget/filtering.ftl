<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="filter" type="billiongoods.server.warehouse.ProductFilter" -->
<#-- @ftlvariable name="filtering" type="billiongoods.server.warehouse.FilteringAbility" -->
<#-- @ftlvariable name="pageableForm" type="billiongoods.server.web.servlet.mvc.warehouse.form.PageableForm" -->

<#include "/core.ftl"/>

<#if filtering?? && pageableForm??>
<div id="productsFilterForm" class="filtering">
    <#list filtering.attributes as a>
        <div class="property">
            <div class="name">
            ${a.name}<#if a.unit?has_content>, ${a.unit}</#if>
            </div>

            <ul class="items">
                <#assign emptyCount=0/>
                <#list filtering.getFilteringItems(a) as i>
                    <#if i.name?has_content>
                        <li class="item">
                            <input id="filter_${a.id}_${i_index}" type="checkbox" name="${a.id}" value="${i.name}"
                                   <#if filter?? && filter.isAllowed(a, i.name)>checked="checked"</#if> />
                            <label for="filter_${a.id}_${i_index}">
                            ${i.name}
                            </label>
                        </li>
                    <#else>
                        <#assign emptyCount=i.count/>
                    </#if>
                </#list>
                <#if (emptyCount>0)>
                    <li class="item">
                        <input id="filter_${a.id}" type="checkbox" name="${a.id}" value=""
                               <#if filter?? && filter.isAllowed(a, "")>checked="checked"</#if>/>
                        <label for="filter_${a.id}" class="sample">все остальные</label>
                    </li>
                </#if>
            </ul>
        </div>
    </#list>

    <div style="text-align: right">
        <button id="categoryFilterAction" type="button" class="bg-ui-button"
                onclick="new bg.warehouse.Filter().applyFilter('<@bg.ui.tableNavigationParams pageableForm "filter" ""/>')">
            Применить
        </button>
    </div>
</div>
</#if>