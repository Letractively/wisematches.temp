<#-- @ftlvariable name="orders" type="billiongoods.server.services.payment.Order[]" -->
<#-- @ftlvariable name="orderState" type="billiongoods.server.services.payment.OrderState" -->

<#include "/core.ftl"/>

<div class="orders">
    <div class="states">
    <#list [OrderState.ACCEPTED, OrderState.PROCESSING, OrderState.SHIPPING, OrderState.NEW, OrderState.BILLING, OrderState.REJECTED] as o>
        <div class="state">
            <input id="status${o.name()}" name="status" type="radio" value="${o.name()}"
                   <#if o==orderState>checked="checked"</#if>>
            <label for="status${o.name()}"><@message code="order.status.${o.name()?lower_case}.label"/></label>
        </div>
    </#list>
    </div>

    <div class="search">
        <input name="id" value="">
        <button type="button" value="id">По Номеру</button>
        <button type="button" value="token">По Коду</button>
        <button type="button" value="ref">По Внешнему</button>
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
                <td>${o.shipmentType!""}</td>
                <td>${o.referenceTracking!""}</td>
                <td>${o.chinaMailTracking!""}</td>
                <td>${o.internationalTracking!""}</td>
            </tr>
        </#list>
        </table>
    </div>
</div>

<script type="application/javascript">
    var states = $(".orders .state");
    states.find("input").change(function () {
        var state = states.find("input:checked").val();
        bg.util.url.redirect("/maintain/order?state=" + state);
    });

    var search = $(".orders .search");
    search.find("button").click(function () {
        var id = search.find("input").val();
        var type = $(this).val();
        bg.util.url.redirect("/maintain/order/view?id=" + id + "&type=" + type);
    });
</script>