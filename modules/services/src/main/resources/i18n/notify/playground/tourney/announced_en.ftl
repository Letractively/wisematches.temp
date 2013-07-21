<#-- @ftlvariable name="context" type="billiongoods.playground.tourney.regular.Tourney" -->
<#import "../../utils.ftl" as util>

<p>
    Next ${context.number?string}${messageSource.getNumeralEnding(context.number, locale)}
    BillionGoods tourney is going to be
    started in ${messageSource.formatRemainedTime(context.scheduledDate, locale)}
    (${messageSource.formatDate(context.scheduledDate, locale)}).
</p>

<p>
    We found that you are not subscribed yet to the tourney. You can do that at any time
    on main tourneys page: <@util.link href="playground/tourney"/>.
</p>