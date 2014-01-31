<#macro message code args=[]><#if args?size == 0>${springMacroRequestContext.getMessage(code)}<#else>${springMacroRequestContext.getMessage(code, args)}</#if></#macro>

<#macro messageCapFirst code args=[]><#if args?size == 0>${springMacroRequestContext.getMessage(code)?cap_first}<#else>${springMacroRequestContext.getMessage(code, args)?cap_first}</#if></#macro>

<#macro messageCap code args=[]><#if args?size == 0>${springMacroRequestContext.getMessage(code)?capitalize}<#else>${springMacroRequestContext.getMessage(code, args)?capitalize}</#if></#macro>

<#macro messageLower code args=[]><#if args?size == 0>${springMacroRequestContext.getMessage(code)?lower_case}<#else>${springMacroRequestContext.getMessage(code, args)?lower_case}</#if></#macro>
