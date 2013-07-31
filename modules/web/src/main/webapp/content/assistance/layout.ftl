<#-- @ftlvariable name="staticContentId" type="java.lang.String" -->
<#-- @ftlvariable name="staticContentModel" type="freemarker.ext.dom.NodeModel" -->
<#include "/core.ftl"/>

<#assign itemsPerLine=2/>

<div class="assistance">
    <div class="tit">
    <@message code="title.assistance.${staticContentId}"/>
    </div>

    <div class="toc">
        <table>
        <#assign tocIndex=0/>
        <#list staticContentModel.items as items>
            <#assign nodes=items?children/>
            <#list nodes as node>
                <#if (tocIndex%itemsPerLine==0)>
                <tr></#if>
                <td>
                    <a href="#${node.@id}"><span class="image"></span>${node.label[0]}</a>
                </td>
                <#if (tocIndex-(itemsPerLine-1))%itemsPerLine==0></tr></#if>
                <#assign tocIndex=tocIndex+1/>
            </#list>

            <#if tocIndex%itemsPerLine!=0>
                <#list tocIndex%itemsPerLine..(itemsPerLine-1) as i>
                    <td></td></#list>
            </#if>
        </#list>
        </table>
    </div>

<#--
    <ul class="toc">
    <#list staticContentModel.items as items>
        <#assign nodes=items?children/>
        <#list nodes as node>
            <li><a href="#${node.@id}"><span class="image"></span>${node.label[0]}</a></li>
        </#list>
    </#list>
    </ul>
-->

    <ul class="cnt">
    <#list staticContentModel.items as items>
        <#assign nodes=items?children/>
        <#list nodes as node>
            <li id="${node.@id}">
                <div class="sec">${node.label[0]}</div>
                <div class="des">${node.description[0]}</div>
            </li>
        </#list>
    </#list>
    </ul>
</div>