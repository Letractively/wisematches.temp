<#-- @ftlvariable name="context.proposal" type="billiongoods.playground.propose.PrivateProposal<billiongoods.playground.scribble.ScribbleSettings>" -->
<#import "../../utils.ftl" as util>

<p>
    You have received new challenge from player
    <strong><@util.player context.proposal.initiator/></strong>
<#if context.proposal.commentary?has_content>
<blockquote>${context.proposal.commentary}</blockquote>
</#if>
</p>
<br>
<p>
    If you accept the proposal new game "${context.proposal.settings.title!""}"
    with ${messageSource.formatTimeMinutes(context.proposal.settings.daysPerMove*24*60, locale)} time limit.
</p>
<p>
    The ${messageSource.getMessage("language.${context.proposal.settings.language?lower_case}", locale)} language
    will be used for the game. If you don't know the language we suggest not accept the challenge.
</p>
<br>
<p>
    You can accept or decline the challenge on <@util.link href="playground/scribble/join">playground</@util.link>
    page. Please use appropriate proposal button.
</p>
<p>
    If you don't want receive new challenges from the initiator, you
    can <@util.link href="playground/profile/view?p=${context.proposal.initiator.id}&blacklist=true">add
    him/her to your ignore list</@util.link>. In this case you won't receive new challenges and private messages
    from the player and he/she won't be able join to your games.
</p>