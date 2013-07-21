<#-- @ftlvariable name="context" type="billiongoods.playground.dictionary.WordReclaim" -->
<#import "../../utils.ftl" as util>

<p>
    Ваше изменение в словаре было принято. Слово <strong>${context.word}</strong> было
<#if context.resolutionType == "CREATE">добавлено в словарь<#elseif context.resolutionType == "REMOVE">удалено из
    словаря<#else>обновлено</#if>.
</p>
<p>
    Спасибо за помощь в редактирование словаря.
</p>