<#include "/core.ftl"/>

<div class="navigation">
    <div class="privacy">
        <ul class="container">
        <#list ["orders", "addresses", "coupons", "wishlist", "subscriptions", "settings", "social"] as i>
            <li class="ct-item">
                <a href="/privacy/${i}">
                    <@message code="title.privacy.${i}"/>
                </a>
            </li>
        </#list>
        </ul>
    </div>
</div>
