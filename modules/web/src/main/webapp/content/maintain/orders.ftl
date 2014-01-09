<#-- @ftlvariable name="orders" type="billiongoods.server.services.payment.Order[]" -->
<#-- @ftlvariable name="orderState" type="billiongoods.server.services.payment.OrderState" -->
<#-- @ftlvariable name="ordersSummary" type="billiongoods.server.services.payment.OrdersSummary" -->

<#include "/core.ftl"/>

<style type="text/css">
    .orders .states .selected td {
        font-weight: bold;
        background: #808080;
    }
</style>

<div class="orders">
    <div class="states">
        <table>
        <#list [OrderState.ACCEPTED, OrderState.PROCESSING, OrderState.SUSPENDED, OrderState.SHIPPING, OrderState.SHIPPED, OrderState.BILLING, OrderState.CLOSED] as s>
            <#assign selected=(s==orderState)/>
            <tr <#if selected>class="selected"</#if>>
                <td>
                    <a href="/maintain/order?state=${s.name()}">
                        <@message code="order.status.${s.name()?lower_case}.label"/>
                    </a>
                </td>
                <td style="padding-left: 5px">
                    <a href="/maintain/order?state=${s.name()}">
                    ${ordersSummary.getOrdersCount(s)}
                    </a>
                </td>
            </tr>
        </#list>
        </table>
    </div>

    <div class="search">
        <input name="id" value="">
        <button type="button" value="id">По инденификатору (ID)</button>
        <button type="button" value="token">По PayPal коду (TOKEN)</button>
        <button type="button" value="ref">По BG коду (BG order #)</button>
    </div>

    <div class="cnt">
        <table>
            <tr>
                <th>Номер</th>
                <th>Последнее изменение</th>
                <th>Доставка</th>
                <th>BangGood</th>
                <th>Почта Китая</th>
                <th>Почта России</th>
            </tr>
        <#list orders as o>
            <tr>
                <td><a href="/maintain/order/view?id=${o.id}&type=id">${o.id}</a></td>
                <td>${messageSource.formatDate(o.timestamp, locale)} ${messageSource.formatTime(o.timestamp, locale)}</td>
                <td>${o.shipment.type!""}</td>
                <td><@bg.tracking.system o.referenceTracking/></td>
                <td><@bg.tracking.china o.chinaMailTracking/></td>
                <td><@bg.tracking.international o.internationalTracking/></td>
            </tr>
        </#list>
        </table>
    </div>
</div>

<script type="application/javascript">
    var search = $(".orders .search");
    search.find("button").click(function () {
        var id = search.find("input").val();
        var type = $(this).val();
        bg.util.url.redirect("/maintain/order/view?id=" + id + "&type=" + type);
    });
</script>
