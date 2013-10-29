<#-- @ftlvariable name="coupon" type="billiongoods.server.services.coupon.Coupon" -->

<#include "/core.ftl">

<div style="padding: 10px; border: 1px solid gray;">
    <form action="/maintain/coupon/view" method="get">
        <input name="code" type="text" value=""/>
        <button type="submit">Найти купон</button>
    </form>

<#if coupon??>
    Купон ${coupon.code}
    <#if coupon.couponType.fixed>
        предоставляет право купить за ${coupon.amount?string("0.00")}}руб.
    <#elseif coupon.couponType.price>
        снизить стоимость на ${coupon.amount?string("0.00")}}руб. за
    <#elseif coupon.couponType.percent>
        дает ${coupon.amount?string("0")}% скидку на
    </#if>

    <#if coupon.referenceType.product>
        товар <a
            href="/warehouse/product/${coupon.referenceId}">${messageSource.getProductCode(coupon.referenceId)}</a>.
    <#elseif coupon.referenceType.category>
        <#assign category=catalog.getCategory(coupon.referenceId)/>
        товары категории <@bg.link.categoryLink category>${category.name}</@bg.link.categoryLink>.
    </#if>
</#if>
</div>

