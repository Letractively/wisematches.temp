<#-- @ftlvariable name="context.board" type="billiongoods.playground.GameBoard" -->
<#assign board=context.board/>

<#import "../../utils.ftl" as util>

<p>
    Игра <@util.board board=board/>, которую вы ожидаете, началась.
</p>

<p>
    К игре
<#if board.players?size == 2>присоединился один игрок:
<#else>присоединилось ${board.players?size-1?string} игроков:
</#if>
<#list board.players as hand>
    <#if hand.id != personality.id>
        <@util.player personality=hand showRating=true/><#if hand_has_next>,</#if>
    </#if>
</#list>
    с которыми вы будите состязаться. Будьте вежливы и приятного времяпрепровождения.
</p>

<p>
<#if board.playerTurn.id == personality.id>
    <strong>Ход в этой игре передан вам</strong>. У вас есть
    <em>${messageSource.formatRemainedTime(board, locale)}</em> для выполнения хода или игра
    будет прервана и вам будет засчитан проигрыш.
<#else>
    Ход был передан игроку <@util.player board.playerTurn/>.
</#if>
</p>