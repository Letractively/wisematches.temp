<#include "/core.ftl"/>

<div class="navigation">
    <div class="privacy">
        <ul class="container">
        <#--<#list ["coupons", "wishlist"] as i>-->
        <#list ["orders", "address", "tracking"] as i>
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
        <#list ["view", "social"] as i>
            <li class="ct-item">
                <a href="/account/passport/${i}">
                    <@message code="title.account.passport.${i}"/>
                </a>
            </li>
        </#list>
        </ul>
    </div>
</div>
