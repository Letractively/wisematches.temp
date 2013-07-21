<#-- @ftlvariable name="context.board" type="billiongoods.playground.GameBoard" -->
<#-- @ftlvariable name="context.resolution" type="billiongoods.playground.GameResolution" -->
<#-- @ftlvariable name="context.winners" type="java.util.Collection<billiongoods.core.Personality>" -->
<#import "../../utils.ftl" as util>
<#assign board=context.board/>
<#assign resolution=context.resolution/>
<#assign winners=context.winners/>

<p>A game <@util.board board=board/> has been finished.</p>

<p>
<#switch resolution>
    <#case "RESIGNED">
        <#if board.playerTurn.id == personality.id>
            You have resigned a game.
        <#else>
            The player  <@util.player board.playerTurn/> has
            resigned a game.
        </#if>
        <#break>
    <#case "TIMEOUT">
        <#if board.playerTurn.id == personality.id>
            Your move time run up and the game has been interrupted.
        <#else>
            The player  <@util.player board.playerTurn/>'s move
            time run up and the game has been interrupted.
        </#if>
        <#break>
    <#case "STALEMATE">
        There is no winner. Stalemate.
        <#break>
    <#default>
        The following <#if winners?size == 1>player has<#else>players have</#if> won the game:
        <#list winners as w>
            <@util.player w/><#if w_has_next>, </#if></#list>
</#switch>
</p>

<#if board.rated>
<p>
    Ratings of all players player (except robots) were changed:
</p>
<table>
    <tr>
        <td></td>
        <td align="center"><strong>Points</strong></td>
        <td align="center"><strong>Old Rating</strong></td>
        <td align="center"><strong>New Rating</strong></td>
    </tr>
    <#list board.players as p>
        <#assign hand=board.getPlayerHand(p)/>
        <tr>
            <td><@util.player p/> <#if p.id==personality.id>(<strong>You</strong>)</#if></td>
            <td align="center">${hand.points}</td>
            <td align="center">${hand.oldRating}</td>
            <td align="center">${hand.newRating}</td>
        </tr>
    </#list>
</table>

<p>
    You can read more about rating calculation here: <@util.link href="info/rating"/>.
</p>
<#else>
<p>
    Because game isn't rated your rating and ratings of your opponents was not changed and
    still is ${board.getPlayerHand(personality).newRating?string}.
</p>
</#if>
