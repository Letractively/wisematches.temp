<#include "/core.ftl"/>

<#macro category cat class="" name=""><a <#if class?has_content>class="${class}"</#if>
                                         href="/warehouse/category/${cat.symbolicUri}"><#if name?has_content>${name}<#else>${cat.name}</#if></a></#macro>

<#macro assist page><a href="/assistance/${page}"><@message code="title.assistance.${page}"/></a></#macro>

<#macro product desc><a class="sample" itemprop="url"
                        href="/warehouse/product/${desc.symbolicUri}"><#nested/></a></#macro>