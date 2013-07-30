<#-- @ftlvariable name="context.board" type="billiongoods.playground.GameBoard" -->
<#-- @ftlvariable name="context.resolution" type="billiongoods.playground.GameResolution" -->
<#-- @ftlvariable name="context.winners" type="java.util.Collection<billiongoods.core.Personality>" -->
<#import "../../utils.ftl" as util>
<#assign board=context.board/>
<#assign resolution=context.resolution/>
<#assign winners=context.winners/>

<p> Игра <@util.board board=board/> была завершена</p>

<p>
<#switch resolution>
    <#case "RESIGNED">
        <#if board.playerTurn.id == personality.id>
            Игра была прервана вами.
        <#else>
            Игрок <@util.player board.playerTurn/> прервал
            игру.
        </#if>
        <#break>
    <#case "TIMEOUT">
        <#if board.playerTurn.id == personality.id>
            Время вашего хода истекло и игра была прервана.
        <#else>
            Время хода игрока <@util.player board.playerTurn/>
            истекло и игра была прервана.
        </#if>
        <#break>
    <#case "STALEMATE">
        В игре нет победителя. Ничья.
        <#break>
    <#default>
        Победитель/победители в игре:
        <#list winners as w>
            <@util.player w/><#if w_has_next>, </#if></#list>
</#switch>
</p>

<#if board.rated>
<p>
    Рейтинги всех игроков (за исключением роботов) были пересчитаны:
</p>
<table>
    <tr>
        <td></td>
        <td align="center"><strong>Очки</strong></td>
        <td align="center"><strong>Старый Рейтинг</strong></td>
        <td align="center"><strong>Новый Рейтинг</strong></td>
    </tr>
    <#list board.players as p>
        <#assign hand=board.getPlayerHand(p)/>
        <tr>
            <td><@util.player p/> <#if p.id==personality.id>(<strong>Вы</strong>)</#if></td>
            <td align="center">${hand.points}</td>
            <td align="center">${hand.oldRating}</td>
            <td align="center">${hand.newRating}</td>
        </tr>
    </#list>
</table>
<p>
    Вы можете узнать больше о способе расчета рейтингов здесь: <@util.link href="info/rating"/>.
</p>
<#else>
<p>
    Данная игра не влияет на рейтинг и ваш рейтинг не изменился
    и равен ${board.getPlayerHand(personality).newRating?string}.
</p>
</#if>
