<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#include "/core.ftl"/>

<#if category??>
    <#assign items=category.children/>
<#else>
    <#assign items=catalog.rootCategories/>
</#if>


<div class="navigation catalog">
    <ul class="ct-toc">
    <#if !category??>
        Общий каталог
    <#else>
        <#list category.genealogy.parents as p>
            <#assign padding=15 + p_index * 10/>
            <div class="ct-tit-item"
                 style="margin-left: ${padding}px; background-position: ${padding}px 0">
                <#if p_index != 0><span class="image"></span></#if>
                <#if p == category>
                    <span class="active">${p.name}</span>
                <#else>
                    <@bg.link.categoryLink p/>
                </#if>
            </div>
        </#list>
    </#if>

    <#list items as category>
        <li class="ct-toc-item">
            <span class="ct-toc-tit" style="padding-left: ${padding + 10}px"><@bg.link.categoryLink category/></span>

            <div class="ct-toc-cnt" style="display: none">
                <#list category.children as ch>
                    <#if ch_index%3==0>
                    <div style="float: left"></#if>

                    <ul>
                        <li class="ct-title"><@bg.link.categoryLink ch/></li>
                        <#list ch.children as cch>
                            <li class="ct-title"><@bg.link.categoryLink cch/></li>
                        </#list>
                    </ul>
                    <#if ch_index%3==0></div></#if>
                </#list>
            </div>
        </li>
    </#list>
    </ul>
</div>