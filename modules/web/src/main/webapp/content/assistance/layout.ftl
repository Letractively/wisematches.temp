<#-- @ftlvariable name="staticContentId" type="java.lang.String" -->
<#-- @ftlvariable name="staticContentModel" type="freemarker.ext.dom.NodeModel" -->

<div class="assistance">
    <div class="tit">
    ${staticContentModel.label}
    </div>

    <ul class="toc">
    <#list staticContentModel.items as items>
        <#assign nodes=items?children/>
        <#list nodes as node>
            <li><a href="#${node.@id}"><span class="image"></span>${node.label[0]}</a></li>
        </#list>
    </#list>
    </ul>

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