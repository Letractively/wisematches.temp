<#include "/core.ftl"/>

<#macro categoryLink category class="" name=""><a <#if class?has_content>class="${class}"</#if>
                                                  href="/warehouse/category/${category.id}"><#if name?has_content>${name}<#else>${category.name}</#if></a></#macro>

<#macro assist page><a href="/assistance/${page}"><@message code="title.assistance.${page}"/></a></#macro>

<#macro article desc><a itemprop="url" href="/warehouse/article/${desc.id}"><#nested/></a></#macro>

<#macro tracking sym><a
        href="http://gdeposylka.ru/${sym}?tos=accept&apikey=418832.b3a52a082d&country=RU">${sym}</a></#macro>