<#-- @ftlvariable name="context" type="billiongoods.playground.dictionary.WordReclaim" -->
<#import "../../utils.ftl" as util>

<p>
    Your change suggestion has been accepted. Word <strong>${context.word}</strong> has been
<#if context.resolutionType == "CREATE">added to the dictionary<#elseif context.resolutionType == "REMOVE">removed from
    the
    dictionary<#else>updated</#if>.
</p>
<p>
    Thanks a lot for help with make our dictionary up to date.
</p>