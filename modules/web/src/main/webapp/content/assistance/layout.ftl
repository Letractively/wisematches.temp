<#-- @ftlvariable name="staticContentId" type="java.lang.String" -->
<#-- @ftlvariable name="staticContentModel" type="freemarker.ext.dom.NodeModel" -->
<#include "/core.ftl"/>

<#assign itemsPerLine=2/>

<div class="header">
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

<div id="supportQuestionForm" class="question" style="display: none;">
    <form>
        <div class="email">
            <div class="label"><label for="email">Ваша электронная почта:</label></div>
            <div class="input"><input id="email" type="text" name="email"/></div>
        </div>

        <div class="name">
            <div class="label"><label for="name">Ваше имя:</label></div>
            <div class="input"><input id="name" type="text" name="name"/></div>
        </div>

        <div class="message">
            <div class="label"><label for="message">Ваше сообщение:</label></div>
            <div class="input"><textarea id="message" name="message" rows="10"></textarea></div>
        </div>

        <div class="button">
            <button class="bg-ui-button" type="button">Отправить</button>
        </div>
    </form>
</div>

<script type="application/javascript">
    new bg.assistance.SupportForm();
</script>