<#-- @ftlvariable name="context.board" type="billiongoods.playground.GameBoard" -->
<#assign board=context.board/>

<#import "../../utils.ftl" as util>

<p>
    A game <@util.board board=board/> you are waiting for has been started.
</p>

<p>
    There
<#if board.players?size == 2>is one player
<#else>are ${board.players?size-1?string} players
</#if>
    joined to the game:
<#list board.players as hand>
    <#if hand.id != personality.id>
        <@util.player personality=hand showRating=true/><#if hand_has_next>,</#if>
    </#if>
</#list>
    who you will play with. Please be polite and have a fun.
</p>

<p>
<#if board.playerTurn.id == personality.id>
    <strong>It's you turn in the game</strong>. You have
    <em>${messageSource.formatRemainedTime(board, locale)} </em> to make a turn or game will be timed out and
    you will be defeated.
<#else>
    Turn was transmitted to player <@util.player board.playerTurn/>.
</#if>
</p>