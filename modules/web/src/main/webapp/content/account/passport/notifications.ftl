<#include "/core.ftl">

<div class="settings">
    <form action="/account/passport/notifications" method="post">
        <div class="tit">
            Настройка уведомлений
        </div>

        <div class="cnt">
            <table>
            <#list OrderState.values() as s>
                <tr>
                    <td valign="top">
                        <input style="margin-top: 3px" id="notifyState${s.code}" type="checkbox"
                               name="notifyStates"
                               value="${s.name()}">
                    </td>
                    <td valign="top">
                        <label for="notifyState${s.code}"><@message code="order.status.${s.code}.label"/></label>
                        <br>
                        <span class="sample"><@message code="order.status.${s.code}.description"/></span>
                    </td>
                </tr>
            </#list>
            </table>
        </div>

        <div class="question" style="margin: 0; border-left: none; border-right: none; border-bottom: none">
            <button class="bg-ui-button">Сохранить изменения</button>
        </div>
    </form>
</div>
