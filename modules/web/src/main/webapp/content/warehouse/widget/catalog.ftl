<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#include "/core.ftl"/>

<#if category??>
    <#assign items=category.children/>
<#else>
    <#assign items=catalog.rootCategories/>
</#if>

<div class="catalog">
    <div class="ct-tit">
    <#if !category??>
        Общий каталог
    <#else>
        <#list category.genealogy.parents as p>
            <div class="ct-tit-item"
                 style="padding-left: ${p_index * 4}px; background-position: ${p_index * 4}px 0">${p.name}</div>
        </#list>
    </#if>
    </div>

    <ul class="ct-toc">
    <#list items as category>
        <li class="ct-toc-item">
            <span class="ct-toc-tit"><@bg.link.categoryLink category/></span>

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