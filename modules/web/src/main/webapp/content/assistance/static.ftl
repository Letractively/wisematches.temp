<#-- @ftlvariable name="staticContentId" type="java.lang.String" -->
<#-- @ftlvariable name="staticContentModel" type="freemarker.ext.dom.NodeModel" -->
<#-- @ftlvariable name="staticContentOrder" type="java.lang.Boolean" -->

<#global reverse=staticContentOrder/>
<#global level=0/>

<div id="info-${staticContentId}">
    <div id="info-${staticContentId}-header" class="info-header">
    <#if staticContentModel.label[0]??>
        <div id="info-${staticContentId}-label" class="info-label">${staticContentModel.label}</div>
    </#if>

    <#if staticContentModel.description[0]??>
        <div id="info-${staticContentId}-description" class="info-description">${staticContentModel.description}</div>
    </#if>
    </div>

    <div id="info-${staticContentId}-items" class="info-items">
    <#visit staticContentModel.items>
    </div>
</div>

<#macro items>
    <#assign level=level+1/>
    <#if .node?children??>
    <ol>
        <#assign nodes=.node?children/>
        <#if reverse><#assign nodes=nodes?reverse/><#global reverse=false/></#if>
        <#list nodes as item>
            <li><#visit item></li>
        </#list>
    </ol>
    </#if>
    <#assign level=level-1/>
</#macro>

<#macro item>
<div class="info-item info-${staticContentId}-item info-item-level${level}">
    <#if .node.image[0]??>
        <div class="info-image info-${staticContentId}-image">
            <img src="${.node.image}"/>
        </div>
    </#if>
    <#if .node.label[0]??>
        <div class="info-label info-${staticContentId}-label">${.node.label}</div>
    </#if>
    <#if .node.description[0]??>
        <div class="info-description info-${staticContentId}-description">${.node.description}</div>
    </#if>
    <#if .node.links[0]??>
        <div class="info-links info-${staticContentId}-links">${.node.links}</div>
    </#if>

    <#if .node.items[0]??>
        <#visit .node.items[0]>
    </#if>
    <div class="space-line"></div>
</div>
</#macro>
