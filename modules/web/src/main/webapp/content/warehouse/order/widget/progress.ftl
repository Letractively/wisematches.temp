<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->

<#include "/core.ftl"/>

<div class="steps">
<#assign steps=[OrderState.BILLING, OrderState.ACCEPTED, OrderState.PROCESSING, OrderState.SHIPPED, OrderState.CLOSED]/>

<#assign alignedState=order.state/>
<#if alignedState == OrderState.SHIPPING || alignedState == OrderState.SUSPENDED>
    <#assign alignedState=OrderState.PROCESSING/>
<#elseif alignedState == OrderState.FAILED || alignedState == OrderState.CANCELLED>
    <#assign alignedState=OrderState.CLOSED/>
</#if>

<#assign currentIndex=steps?seq_index_of(alignedState)/>
    <table width="100%" cellpadding="0" cellspacing="0">
        <tr>
        <#list steps as s>
            <#assign index=s_index/>
            <td class="<#if index==0>first<#elseif index==(steps?size-1)>last</#if> <#if (index==currentIndex)>current</#if> <#if (index==currentIndex-1)>previous</#if> <#if (currentIndex>index)>done</#if>"
                width="${100/(steps?size)}%">
                <@message code="order.steps.${s.code}.label"/>
            </td>
        </#list>
        </tr>
    </table>
</div>
