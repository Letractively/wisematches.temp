<#include "/core.ftl"/>

<div class="catalog">
    <ul class="container">
    <#list catalog.rootCategories as p>
        <li class="ct-item">
            <div class="ct-toc<#if !p.children?has_content> empty</#if>"><span
                    class="image"></span><@bg.link.category p/></div>

            <#if p.children?has_content>
                <div class="ct-list" style="display: none;">
                    <#list p.children as category>
                        <#if category_index%3==0>
                        <div style="float: left"></#if>

                        <ul>
                            <li class="ct-list-tit">
                                <span class="image"></span><@bg.link.category category/></li>

                            <#list category.children as ch>
                                <#if ch_index==8><#break/></#if>
                                <li class="ct-list-cnt">
                                    <span class="image"></span><@bg.link.category ch/></li>
                            </#list>
                            <#if (category.children?size>8)>
                                <li class="ct-list-cnt"><@bg.link.category category "highlight" "все элементы"/></li></#if>
                        </ul>
                        <#if (category_index-2)%3==0 || !category_has_next></div></#if>
                    </#list>
                </div>
            </#if>
        </li>
    </#list>
    </ul>
</div>