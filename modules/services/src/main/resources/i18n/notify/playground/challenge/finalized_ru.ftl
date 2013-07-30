<#-- @ftlvariable name="context.proposal" type="billiongoods.playground.propose.GameProposal" -->
<#-- @ftlvariable name="context.resolution" type="billiongoods.playground.propose.ProposalResolution" -->
<#-- @ftlvariable name="context.player" type="billiongoods.core.Personality" -->
<#import "../../utils.ftl" as util>

<#macro proposal>#${context.proposal.id} "${context.proposal.settings.title!""}"</#macro>

<#switch context.resolution>
    <#case "REPUDIATED">
    <p>Игрок <@util.player context.player/> отозвал свое предложение игры <@proposal/> и оно было отменено.</p>
        <#break>
    <#case "REJECTED">
    <p>Игрок <@util.player context.player/> отказался от вашего предложения <@proposal/> и оно было отменено.</p>
    <p>Если вы не знакомы с этим игроком мы не рекомендуем вам отправлять еще одно предложение.</p>
        <#break>
    <#case "TERMINATED">
    <p>Предложение <@proposal/> не было принято <#if context.player??><@util.player context.player/><#else>одним
        из игроков</#if> и было отменено.</p>
    <p>Мы не рекомендуем вам отправлять еще одно предложение данному игроку.</p>
        <#break>
    <#default>
</#switch>