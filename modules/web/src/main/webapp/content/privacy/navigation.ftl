<#include "/core.ftl"/>

<div class="navigation">
    <div class="privacy">
        <ul class="container">
        <#--<#list ["orders", "addresses", "coupons", "wishlist", "subscriptions", "settings", "social"] as i>-->
        <#list ["orders"] as i>
            <li class="ct-item">
                <a href="/privacy/${i}">
                    <@message code="title.privacy.${i}"/>
                </a>
            </li>
        </#list>
        </ul>
    </div>
</div>

<div class="navigation" style="padding-top: 15px">
    <div class="privacy">
        <ul class="container">
        <#--<#list ["orders", "addresses", "coupons", "wishlist", "subscriptions", "settings", "social"] as i>-->
        <#list ["personal", "social", "notifications"] as i>
            <li class="ct-item">
                <a href="/account/settings/${i}">
                    <@message code="title.account.settings.${i}"/>
                </a>
            </li>
        </#list>
        </ul>
    </div>
</div>
