<#-- @ftlvariable name="analyticsYandexCode" type="java.lang.String" -->
<#-- @ftlvariable name="order" type="billiongoods.server.services.payment.Order" -->

<#include "/core.ftl"/>

<#if analyticsYandexCode?has_content>
<script type="application/javascript">
    window.onload = function () {
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
    };
</script>
</#if>
