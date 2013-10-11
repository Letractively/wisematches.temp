<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="filter" type="billiongoods.server.warehouse.ProductFilter" -->
<#-- @ftlvariable name="filtering" type="billiongoods.server.warehouse.Filtering" -->
<#-- @ftlvariable name="pageableForm" type="billiongoods.server.web.servlet.mvc.warehouse.form.PageableForm" -->

<#include "/core.ftl"/>

<#--================ Filtering based on data defined for products ======================-->
<#macro attributeValueString a v c>
<li class="item">
    <input id="parameter_${a.id}_${v}" type="checkbox" name="${a.id}" value="${v}"
           <#if filter?? && filter.getValue(a)?? && filter.getValue(a).isAllowed(v)>checked="checked"</#if> />
    <label for="parameter_${a.id}_${v}">
        <#if v?has_content>${v}<#else><em>неизвестно</em></#if> (${c})
    </label>
</li>
</#macro>

<#macro categoryAttributeEnum a item>
    <#list item.values as v>
        <#if v?has_content>
            <@attributeValueString a v item.getValueCount(v)/>
        </#if>
    </#list>
    <#assign cnt=item.getValueCount("")/>
    <#if (cnt>0)>
        <@attributeValueString a "" cnt/>
    </#if>
</#macro>

<#--
<#macro categoryAttributeEnum a item>
    <#assign totalCount=0/>
    <#list item.values as v>
        <#assign localCount=item.getValueCount(v)/>
        <#assign totalCount=totalCount+localCount/>
        <@attributeValueString a v localCount/>
    </#list>
    <#if (totalCount<filtering.totalCount)>
        <@attributeValueString a "" filtering.totalCount-totalCount/>
    </#if>
</#macro>
-->

<#macro categoryAttributeBoolean a item>
<li class="item">
    <input id="parameter_${a.id}_yes" type="radio" name="${a.id}" value="true"
           <#if filter?? && filter.getValue(a)?? && filter.getValue(a).isAllowed(true)>checked="checked"</#if>/>
    <label for="parameter_${a.id}_yes">Да</label>
</li>
<li class="item">
    <input id="parameter_${a.id}_no" type="radio" name="${a.id}" value="false"
           <#if filter?? && filter.getValue(a)?? && filter.getValue(a).isAllowed(false)>checked="checked"</#if>/>
    <label for="parameter_${a.id}_no">Нет</label>
</li>
<li class="item">
    <input id="parameter_${a.id}" type="radio" name="${a.id}" value=""
           <#if !filter?? || !filter.getValue(a)??>checked="checked"</#if>/>
    <label for="parameter_${a.id}">Неважно</label>
</li>
</#macro>

<#if category?? && filtering?? && pageableForm??>
<div id="productsFilterForm" class="filtering">
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

    <#list filtering.filteringItems as i>
        <#if !i.empty>
            <#assign a = i.attribute/>
            <div class="property">
                <div class="name">
                ${a.name}<#if a.unit?has_content>, ${a.unit}</#if>
                </div>

                <ul class="items">
                    <#switch a.attributeType>
                        <#case AttributeType.STRING><@categoryAttributeEnum a i/><#break>
                        <#case AttributeType.BOOLEAN><@categoryAttributeBoolean a i/><#break>
                    </#switch>
                </ul>
            </div>
        </#if>
    </#list>
</div>

<script type="text/javascript">
    new bg.warehouse.Filter(${minTotalPrice}, ${maxTotalPrice}, ${minPrice}, ${maxPrice},
            '<@bg.ui.tableNavigationParams pageableForm "filter" ""/>');
</script>
</#if>
