<#-- @ftlvariable name="context.board" type="billiongoods.playground.GameBoard" -->
<#-- @ftlvariable name="context.changes" type="java.util.List<billiongoods.playground.GameMove>" -->
<#assign board=context.board/>
<#assign changes=context.changes/>
<#import "../../utils.ftl" as util>

<p>
    It's your turn again in the game <@util.board board=board/>. You have
    <em>${messageSource.formatRemainedTime(board, locale)}</em> to make a turn or game will be timed out
    and you will be defeated.
</p>

<p>
    Your <#if changes?size==1>opponent has<#else>opponents have</#if> made a move:
</p>
<ul>
<#list changes as move>
    <li>
        <@util.player move.player/> has
        <#switch move.class.simpleName>
            <#case "ExchangeMove">
                exchanged ${move.tileIds?size} tile(s)
                <#break>
            <#case "MakeTurn">
                made word '<em>${move.word.text}</em>' to ${move.points} points
                <#break>
            <#case "PassTurn">
                passed a turn
                <#break>
        </#switch>
        <#if move_has_next>;<#else>.</#if>
    </li>
</#list>
</ul>

<p>
    Please note that more than one game has been updated but only one email has been sent.
</p>