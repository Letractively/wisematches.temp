<#-- @ftlvariable name="context" type="billiongoods.playground.tourney.regular.Tourney" -->
<#import "../../utils.ftl" as util>

<p>
    Следующий, ${context.number?string}${messageSource.getNumeralEnding(context.number, locale)}
    BillionGoods турнир
    начинает через ${messageSource.formatRemainedTime(context.scheduledDate, locale)}
    (${messageSource.formatDate(context.scheduledDate, locale)}).
</p>

<p>
    По нашим сведениям вы еще не подписались на участие в этом турнире. Вы можете сделать это в любое время
    на странице всех ваших активных турниров: <@util.link href="playground/tourney"/>.
</p>