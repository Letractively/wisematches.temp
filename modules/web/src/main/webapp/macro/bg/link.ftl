<#include "/core.ftl"/>

<#macro categoryLink category class="" name=""><a <#if class?has_content>class="${class}"</#if>
                                                  href="/warehouse/category/${category.id}"><#if name?has_content>${name}<#else>${category.name}</#if></a></#macro>

<#macro assist page><a href="/assistance/${page}"><@message code="title.assistance.${page}"/></a></#macro>