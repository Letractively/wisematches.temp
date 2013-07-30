<#-- @ftlvariable name="context.board" type="billiongoods.playground.GameBoard" -->
<#-- @ftlvariable name="context.expirationType" type="billiongoods.playground.propose.ProposalExpirationType" -->
<#import "../../utils.ftl" as util>

<p>У вас есть вызов которые будет автоматически отклонен через
    <strong><em>${messageSource.formatTimeMillis(context.expirationType.remainedTime, locale)}</em></strong>.</p>
<br>
<#include "initiated_ru.ftl"/>