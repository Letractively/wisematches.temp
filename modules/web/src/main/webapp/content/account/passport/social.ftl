<#-- @ftlvariable name="socialProviders" type="java.lang.String[]" -->
<#-- @ftlvariable name="connections" type="java.util.Map<String, org.springframework.social.connect.Connection<?>[]>" -->
<#include "/core.ftl">
<#include "tools.ftl">

<@form "/account/passport/social" "title.account.passport.social" false>
<div class="social">
    <table>
        <thead>
        <tr>
            <th>Сервис</th>
            <th width="100%">Имя</th>
            <th>Действия</th>
        </tr>
        </thead>

        <tbody>
            <#assign count=0/>
            <#list socialProviders as p>
                <#list connections[p] as c>
                    <#assign count=count+1/>
                <tr>
                    <td nowrap="nowrap">
                        <a class="social-view-link" href="${c.profileUrl}"><i
                                class="social-logo social-icon-${p}"></i></a>
                    </td>
                    <td width="100%">
                        <a href="${c.profileUrl}">${c.displayName}</a>
                    </td>
                    <td>
                        <button name="connectionKey" value="${c.key.providerId}|${c.key.providerUserId}">Удалить
                        </button>
                    </td>
                </tr>
                </#list>
            </#list>

            <#if count==0>
            <td colspan="3">
                У вас нет ни одного подключенного профиля социальных сетей. Что бы иметь возможность входить
                в магазин с помощью вашего социального профиля, пожалуйста, добавьте к вашему аккаунту.
            </td>
            </#if>
        </tbody>
    </table>
</div>

<div style="padding-top: 20px">
    <div class="social-signin">
        <h2 style="margin: 0; padding: 0">Добавить профиль</h2>
        <#list socialProviders as p>
            <a class="social-signin-link" href="/account/social/start?provider=${p}"><i
                    class="social-icon-24 social-icon-${p}"></i></a>
        </#list>
    </div>
</div>
</@form>
