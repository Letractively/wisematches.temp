<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="filter" type="billiongoods.server.warehouse.ProductFilter" -->
<#-- @ftlvariable name="filtering" type="billiongoods.server.warehouse.FilteringAbility" -->
<#-- @ftlvariable name="pageableForm" type="billiongoods.server.web.servlet.mvc.warehouse.form.PageableForm" -->

<#include "/core.ftl"/>

<#assign itemsCount=0/>

<#--================ Filtering based on data defined for products ======================-->
<#macro categoryParameterValue a s>
    <#if (s.count>0)>
        <#assign itemsCount=itemsCount+1/>
    <li class="item">
        <input id="parameter_${a.id}_${s.name}" type="checkbox" name="${a.id}" value="${s.name}"
               <#if filter?? && filter.isAllowed(a, s.name)>checked="checked"</#if> />
        <label for="parameter_${a.id}_${s.name}">
            <#if s.name?has_content>${s.name}<#else><em>неизвестно</em></#if> (${s.count})
        </label>
    </li>
    </#if>
</#macro>

<#macro categoryPramaters category>
    <#list category.parameters as p>
        <#assign a=p.attribute/>
        <#if (p.values?size>0)>
            <#assign summary=filtering.getFilteringItems(a)/>

            <#if (a.attributeType=AttributeType.STRING)>
            <div class="property">
                <div class="name">
                ${a.name}<#if a.unit?has_content>, ${a.unit}</#if>
                </div>

                <ul class="items">
                    <#assign unknownSummary=""/>
                    <#list summary as s>
                    <#if s.name?has_content><@categoryParameterValue a s/><#else><#assign unknownSummary=s/></#if>
                </#list>
                    <#if unknownSummary?has_content>
                    <@categoryParameterValue a unknownSummary/>
                </#if>
                </ul>
            </div>
            </#if>
        </#if>
    </#list>
</#macro>

<#if category?? && filtering?? && pageableForm??>
    <#assign minTotalPrice=(filtering.minPrice/10)?floor*10>
    <#assign maxTotalPrice=(filtering.maxPrice/10)?ceiling*10>

    <#assign minPrice=minTotalPrice>
    <#assign maxPrice=maxTotalPrice>
    <#if filter?? && filter.minPrice??>
        <#assign minPrice=(filter.minPrice/10)?floor*10>
    </#if>

    <#if filter?? && filter.maxPrice??>
        <#assign maxPrice=(filter.maxPrice/10)?ceiling*10>
    </#if>

<div id="productsFilterForm" class="filtering">
    <div class="property">
        <div class="name">
            Цена
        </div>

        <ul class="ui-slider-price">
            <li>
                <label>
                    <input id="minPriceFilter" name="minPrice" value="${minPrice}">
                </label>
                —
                <label>
                    <input id="maxPriceFilter" name="maxPrice" value="${maxPrice}">
                </label>
                руб.
            </li>

            <li>
                <div id="priceSlide">
                    <span class="ui-slider-min">${minTotalPrice}</span>
                    <span class="ui-slider-med">${minTotalPrice + (maxTotalPrice-minTotalPrice)/2?round}</span>
                    <span class="ui-slider-max">${maxTotalPrice}</span>
                </div>
            </li>
        </ul>
    </div>

    <#list category.genealogy.parents as c>
        <@categoryPramaters c/>
    </#list>

    <@categoryPramaters category/>
</div>

<script type="text/javascript">
    new bg.warehouse.Filter(${minTotalPrice}, ${maxTotalPrice}, ${minPrice}, ${maxPrice},
            '<@bg.ui.tableNavigationParams pageableForm "filter" ""/>');
</script>
</#if>
