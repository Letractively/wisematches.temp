<#-- @ftlvariable name="context.proposal" type="billiongoods.playground.propose.GameProposal" -->
<#-- @ftlvariable name="context.resolution" type="billiongoods.playground.propose.ProposalResolution" -->
<#-- @ftlvariable name="context.player" type="billiongoods.core.Personality" -->
<#import "../../utils.ftl" as util>

<#macro proposal>challenge #${context.proposal.id} "${context.proposal.settings.title!""}"</#macro>

<#switch context.resolution>
    <#case "REPUDIATED">
    <p>Player <@util.player context.player/> has repudiated his/her <@proposal/> and it was cancelled.</p>
        <#break>
    <#case "REJECTED">
    <p>Player <@util.player context.player/> has rejected <@proposal/> and it was cancelled.</p>
    <p>If you don't know the player well we don't recommend you send new challenge.</p>
        <#break>
    <#case "TERMINATED">
    <p>The <@proposal/> wasn't accepted by <#if context.player??><@util.player context.player/><#else>some
        opponents</#if>, was expired and was terminated.</p>
    <p>We don't recommend you send new challenge to players who didn't accept this one.</p>
        <#break>
    <#default>
</#switch>