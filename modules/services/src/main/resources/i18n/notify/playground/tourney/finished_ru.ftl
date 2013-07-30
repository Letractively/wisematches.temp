<#-- @ftlvariable name="context.tourney" type="billiongoods.playground.tourney.regular.Tourney" -->
<#-- @ftlvariable name="context.division" type="billiongoods.playground.tourney.regular.TourneyDivision" -->
<#-- @ftlvariable name="context.place" type="billiongoods.playground.tourney.TourneyPlace" -->
<#import "../../utils.ftl" as util>

<p>
    Поздравляем, вы
    выйграли ${context.tourney.number?string}${gameMessageSource.getNumeralEnding(context.tourney.number, locale)}
    Турнир BillionGoods.
</p>

<p>
    После всех препятствий вы
    заняли ${gameMessageSource.getMessage("tourney.place." + context.place.place + ".label", locale)}
    в категории
${gameMessageSource.getMessage("tourney.section." + context.division.section.name()?lower_case + ".label", locale)}.
</p>

<p>
    Обращаем ваше внимание, что соответсвующая запись была добавлена в список ваших
    наград: <@util.link href="playground/profile/awards?p=${principal.id}"/>.
</p>

<p>
    Пожалуйста, не забудьте подписаться на участи в следующем турнире
    BillionGoods: <@util.link href="playground/tourney"/>.
</p>