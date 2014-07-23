<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->
<#-- @ftlvariable name="parcel" type="billiongoods.server.services.payment.Parcel" -->

<#include "/core.ftl"/>

<span class="status">
<#if parcel?has_content>
    <#switch parcel.state>
        <#case ParcelState.PROCESSING>
            Обработка. Ожидаемая дата
        отправки ${messageSource.getExceptedDeliveryDate(order.getItems(parcel), locale)}
            <#break>
        <#case ParcelState.SUSPENDED>
            Товара нет в наличие.
            <#if parcel.expectedResume?has_content>
                Ожидаемая дата
            отправки ${messageSource.formatDate(parcel.expectedResume, locale)}
            </#if>
            <#break>
        <#case ParcelState.SHIPPING>
            Отправление посылки: <#if parcel.chinaMailTracking?has_content><@bg.tracking.china parcel.chinaMailTracking/></#if>
            <#break>
        <#case ParcelState.SHIPPED>
            <#if parcel.internationalTracking?has_content>
                Посылка отправлена: <@bg.tracking.international parcel.internationalTracking/>
            <#else>
                Отправлена без номера отслеживания
            </#if>
            <#break>
        <#case ParcelState.CANCELLED>
            Доставка посылки отменена.
            <#break>
        <#case ParcelState.CLOSED>
            Посылка была
        доставлена ${messageSource.formatDate(parcel.timeline.finished, locale)}
            <#break>
    </#switch>
</#if>
</span>