<#-- @ftlvariable name="pageableForm" type="billiongoods.server.web.servlet.mvc.warehouse.form.ProductsPageableForm" -->
<#-- @ftlvariable name="orders" type="billiongoods.server.services.payment.Order[]" -->

<#include "/core.ftl"/>

<div class="orders">
    <div class="table-view">
        <div class="table-pages">
            <div class="table-position">
            <#if (pageableForm.filteredCount > 0)>
                Показано ${(pageableForm.page-1)*pageableForm.count+1}
                - ${(pageableForm.page-1)*pageableForm.count + orders?size}
                из ${pageableForm.filteredCount} элементов
            </#if>
            </div>

            <div class="table-controls">
            <@bg.ui.tableNavigation pageableForm/>
            </div>
        </div>

        <div class="table-content">
            <table>
                <thead>
                <tr>
                    <th>Товар</th>
                    <th>Количество</th>
                    <th>Стоимость</th>
                    <th>Действия</th>
                </tr>
                </thead>

                <tbody>
                <#if orders?has_content>
                    <#list orders as o>
                    <tr class="order ${o.state.code}">
                        <td valign="top">
                            Заказ: <a href="/privacy/order?id=${o.id}">#${o.id}</a>
                        <span class="sample">
                            от ${messageSource.formatDate(o.timeline.created, locale)}
                        </span>
                            <br>
                            <span
                                    class="status"><@message code="order.status.${o.state.code}.label"/></span>
                            <#if !o.state.finished && o.internationalTracking?has_content>
                                <#list o.internationalTracking as t><@bg.tracking.international t/><#if t_has_next>,
                                </#if></#list>
                            </#if>
                        </td>
                        <td valign="top">
                        ${o.productsCount}
                        </td>
                        <td valign="top">
                            <@bg.ui.price o.grandTotal "b"/>
                        </td>
                        <td valign="top" nowrap="nowrap">
                            <form action="/privacy/order" method="post">
                                <input type="hidden" name="orderId" value="${o.id}"/>

                                <#if o.state==OrderState.SHIPPED>
                                    <button name="action" value="confirm">Подтвердить получение</button>

                                <#elseif OrderStateUnion.BILLING.contains(o.state)>
                                    <button name="action" value="checkout">Оплатить</button>
                                    <button name="action" value="remove"
                                            onclick="return confirm('Вы уверены что хотите удалить данный заказ?')">
                                        Удалить
                                    </button>
                                <#else>
                                    &nbsp;
                                </#if>
                            </form>
                        </td>
                    </tr>
                        <#list o.items as i>
                            <#assign p=i.product/>
                        <tr>
                            <td>
                                <div class="preview">
                                    <div class="item">
                                        <@bg.ui.productImage p p.previewImageId!"", ImageSize.TINY, {"style": "float: left; padding-right: 5px"}/>
                                        <@bg.link.product p>${messageSource.getProductCode(p)} ${p.name}</@bg.link.product>
                                    </div>
                                </div>
                            </td>
                            <td valign="top" nowrap="nowrap">
                            ${i.quantity}
                            </td>
                            <td valign="top" nowrap="nowrap">
                                <@bg.ui.price i.amount "b"/>
                            </td>
                            <td valign="top" nowrap="nowrap" style="padding: 5px">
                            <#--<button type="button">Добавить в корзину</button>-->
                            </td>
                        </tr>
                        </#list>
                    </#list>
                <#else>
                <tr>
                    <td colspan="5" align="center" style="padding: 10px">
                        У вас пока нет ни одного заказа. Посмотрите наши <a href="/warehouse/arrivals">последние
                        поступления</a>.
                    </td>
                </tr>
                </#if>
                </tbody>
            </table>
        </div>

        <div class="table-pages">
            <div class="table-position">
                &nbsp;
            </div>

            <div class="table-controls">
            <@bg.ui.tableNavigation pageableForm/>
            </div>
        </div>
    </div>
</div>

<script type="application/javascript">
    function closeOrder(id) {
        var order = new bg.warehouse.Order();
        order.confirmReceived(id, null, function (approved) {
            if (approved) {
                window.location.reload();
            }
        });
    }
</script>