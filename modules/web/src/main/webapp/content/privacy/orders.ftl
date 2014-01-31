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
    <#--

        <div class="table-filter">
            <div class="ipp">
                <strong>На странице: </strong>
                <ul>
                <#list [12,24,36] as i>
                    <li class="bg-ui-button<#if pageableForm.count==i> selected</#if>">
                        <a href="?<@bg.ui.tableNavigationParams pageableForm "count" i/>">${i}</a>
                    </li>
                </#list>
                </ul>
            </div>
        <#if pageableForm.sort?has_content>
            <div class="sort">
                <strong><label for="tableSorting">Сортировать по: </label></strong>
                <select id="tableSorting" name="sort">
                    <#list SortingType.values() as s>
                        <#if pageableForm.query?has_content || s != SortingType.RELEVANCE>
                            <option value="?<@bg.ui.tableNavigationParams pageableForm "sort" s.getCode()/>"
                                    <#if s.getCode()==pageableForm.sort>selected="selected"</#if>><@message code="product.sort.type.${s.getCode()}"/></option>
                        </#if>
                    </#list>
                </select>
            </div>
        </#if>
        </div>
    -->

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
                    <tr class="order ${o.orderState.code}">
                        <td valign="top">
                            Заказ: <a href="/privacy/order?id=${o.id}">#${o.id}</a>
                        <span class="sample">
                            от ${messageSource.formatDate(o.created, locale)}
                        </span>
                            <br>
                            Состояние: <span
                                class="status"><@message code="order.status.${o.orderState.code}.label"/></span>
                            <#if !o.orderState.finalState && o.internationalTracking?has_content>
                                <@bg.tracking.international o.internationalTracking/>
                            </#if>
                        </td>
                        <td valign="top">
                        ${o.itemsCount}
                        </td>
                        <td valign="top">
                            <@bg.ui.price o.amount + o.shipment.amount - o.discount "b"/>
                        </td>
                        <td valign="top" nowrap="nowrap">
                            <#switch o.orderState>
                                <#case OrderState.SHIPPED>
                                    <button type="button" onclick="closeOrder('${o.id}');">Подтвердить получение
                                    </button>
                                    <#break>
                                <#case OrderState.BILLING>
                                    <form action="/privacy/order" method="post">
                                        <input type="hidden" name="orderId" value="${o.id}"/>

                                        <button name="action" value="checkout">Оплатить</button>
                                        <button name="action" value="remove"
                                                onclick="return confirm('Вы уверены что хотите удалить данный заказ?')">
                                            Удалить
                                        </button>
                                    </form>
                                    <#break>
                            </#switch>
                        </td>
                    </tr>
                        <#list o.orderItems as i>
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