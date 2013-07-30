<#-- @ftlvariable name="context.board" type="billiongoods.playground.GameBoard" -->
<#-- @ftlvariable name="context.changes" type="java.util.List<billiongoods.playground.GameMove>" -->
<#assign board=context.board/>
<#assign changes=context.changes/>
<#import "../../utils.ftl" as util>

<p>
    Ход в игре <@util.board board=board/> был передан вам. У вас есть
    <em>${messageSource.formatRemainedTime(board, locale)}</em> чтобы совершить ход. В противном
    случае игра будет прервана и вам засчитано поражение.
</p>

<p>
<#if changes?size==1>Ваш противник завершил свой ход<#else>Ваши противники завершили свои ходы</#if>:
</p>
<ul>
<#list changes as move>
    <li>
        <@util.player move.player/>
        <#switch move.class.simpleName>
            <#case "ExchangeMove">
                обменял(а) ${move.tileIds?size} фишку(-ек)
                <#break>
            <#case "MakeTurn">
                составил(а) словов '<em>${move.word.text}</em>' на ${move.points} очков
                <#break>
            <#case "PassTurn">
                пропустил(а) свой ход
                <#break>
        </#switch>
        <#if move_has_next>;<#else>.</#if>
    </li>
</#list>
</ul>

<p>
    Обратите внимание, что состояние более одной игры могло быть изменено но только одно письмо было отправлено.
</p>