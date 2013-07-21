<#-- @ftlvariable name="context.board" type="billiongoods.playground.GameBoard" -->
<#-- @ftlvariable name="context.expirationType" type="billiongoods.playground.scribble.expiration.ScribbleExpirationType" -->
<#import "../../utils.ftl" as util>

<p> Your move time is running out for board <@util.board board=context.board/>.</p>

<p>
    You have
    <strong><em>${messageSource.formatTimeMillis(context.expirationType.remainedTime, locale)}</em></strong>
    to make a turn or game will be terminated and you will be
    defeated.
</p>