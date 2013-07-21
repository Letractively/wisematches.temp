<#-- @ftlvariable name="context" type="billiongoods.playground.dictionary.WordReclaim" -->
<#import "../../utils.ftl" as util>

<p>
    Пожалуйста, обратите внимание, что выш запрос на изменение словаря для слова <strong>${context.word}</strong> было
    отклонено.
</p>
<#if context.commentary?has_content>
<p>
    Причина отклонения: ${context.commentary}
</p>
</#if>