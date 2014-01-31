<#-- @ftlvariable name="analyticsGoogleCode" type="String" -->
<#-- @ftlvariable name="analyticsYandexCode" type="String" -->

<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->

<#include "/core.ftl"/>

<@bg.security.unauthorized "moderator">

    <#if order.orderState.accepted && analyticsYandexCode?has_content>
    <script type="application/javascript">
        $(document).ready(function () {
            var orderParams = {
                order_id: "${order.id}",
                order_price: ${order.amount?string("0.00")},
                currency: "RUR",
                exchange_rate: 1,
                goods: [
                    <#list order.orderItems as i>
                        {
                            id: "${i.product.id}",
                            name: "${i.product.name}",
                            price: ${i.amount?string("0.00")},
                            quantity: ${i.quantity}
                        }<#if i_has_next>,</#if>
                    </#list>
                ]
            };
            yaCounter${analyticsYandexCode}.reachGoal('ORDER', orderParams);
        });
    </script>
    </#if>

    <#if analyticsGoogleCode?has_content>
    <script type="text/javascript">
        $(document).ready(function () {
            ga('require', 'ecommerce', 'ecommerce.js');

            ga('send', 'event', 'order', '${order.orderState.name()?lower_case}', '${order.id}', '${order.amount}');

            ga('ecommerce:addTransaction', {
                'id': '${order.id}',
                'affiliation': '${order.shipment.address.lastName} ${order.shipment.address.firstName}',
                'revenue': '${(order.amount + order.shipment.amount)?string("0.00")}',
                'shipping': '${(order.shipment.amount)?string("0.00")}',
                'currency': 'RUB'
            });

            <#list order.orderItems as i>
                ga('ecommerce:addItem', {
                    'id': '${i.product.id}',                     // Transaction ID. Required.
                    'name': '${i.product.name}',    // Product name. Required.
                    'sku': '${messageSource.getProductCode(i.product)}',                 // SKU/code.
                    'category': '${catalog.getCategory(i.product.categoryId).name}',         // Category or variation.
                    'price': '${i.amount?string("0.00")}',                 // Unit price.
                    'quantity': '${i.quantity}'                   // Quantity.
                });
            </#list>

            ga('ecommerce:send');
        });
    </script>
    </#if>
</@bg.security.unauthorized>