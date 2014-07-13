<#-- @ftlvariable name="orders" type="billiongoods.server.services.payment.Order[]" -->
<#-- @ftlvariable name="orderState" type="billiongoods.server.services.payment.OrderState" -->
<#-- @ftlvariable name="ordersSummary" type="billiongoods.server.services.payment.OrdersSummary" -->

<#include "/core.ftl"/>

<style type="text/css">
    .orders .states .selected td {
        font-weight: bold;
        background: #808080;
    }

    .orders .cnt th {
        font-weight: bold;
        white-space: nowrap;
        border-bottom: 1px solid #808080;
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
        <table border="0" cellpadding="5">
            <tr>
                <th>Номер</th>
                <th>Получатель</th>
                <th>Товаров</th>
                <th>Сумма</th>
                <th>Дата Создания/Изменения</th>
                <th>BangGood</th>
                <th>Номер отслеживания</th>
            </tr>
        <#list orders as o>
            <tr>
                <td><a href="/maintain/order/view?id=${o.id}&type=id">${o.id}</a></td>
                <td>${o.shipment.address.fullName}</td>
                <td nowrap="nowrap">${o.productsCount}</td>
                <td nowrap="nowrap"><@bg.ui.price o.amount + o.shipment.amount - o.discount "b"/></td>
                <td nowrap="nowrap">
                ${messageSource.formatDate(o.created, locale)} ${messageSource.formatTime(o.created, locale)} <br>
                ${messageSource.formatDate(o.timestamp, locale)} ${messageSource.formatTime(o.timestamp, locale)}
                </td>
                <td><@bg.tracking.system o.referenceTracking/></td>
                <td>
                    <#if o.shipment.type=ShipmentType.FREE>
                        без номера
                    <#else>
                        <#if !o.internationalTracking?has_content>еще не
                            назначен<#else><@bg.tracking.international o.internationalTracking/></#if>
                    </#if>
                </td>
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
