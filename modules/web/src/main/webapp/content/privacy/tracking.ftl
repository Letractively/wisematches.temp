<#-- @ftlvariable name="tracking" type="billiongoods.server.web.servlet.mvc.privacy.form.ProductTrackingView[]" -->

<#include "/core.ftl"/>

<div class="tracking">
    <table>
        <thead>
        <tr>
            <th></th>
            <th width="100%">Продукт</th>
            <th>Добавлен</th>
            <th nowrap="nowrap">Текущее состояние</th>
            <th></th>
        </tr>
        </thead>

        <tbody>
        <#list tracking as t>
            <#assign product=t.product/>
        <tr class="item">
            <td style="margin-right: 10px">
                <@bg.ui.productImage product product.previewImageId!"" ImageSize.TINY/>
            </td>
            <td align="left">
                <@bg.link.product product>${messageSource.getProductCode(product)}</@bg.link.product>
                <@bg.link.product product>${product.name}</@bg.link.product>
            </td>
            <td nowrap="nowrap">
            ${messageSource.formatDate(t.registered, locale)}
            </td>
            <td nowrap="nowrap">
                <#if product.state=ProductState.DISCONTINUED || product.state=ProductState.REMOVED>
                    товар снят с продаж
                <#else>
                    <#if t.trackingType = "DESCRIPTION">
                        <#if product.state=ProductState.ACTIVE>
                            описание добавлено
                        <#else>
                            описание еще не добавлено
                        </#if>
                    <#elseif t.trackingType = "AVAILABILITY">
                        <#assign stockState=product.stockInfo.stockState/>
                        <#if stockState=StockState.IN_STOCK>
                            доступ для заказа
                        <#elseif stockState=StockState.LIMITED_NUMBER>
                            осталось ${product.stockInfo.leftovers}
                        <#else>
                            нет в наличии
                        </#if>
                    <#else>
                        неизвестно
                    </#if>
                </#if>
            </td>

            <td align="right" nowrap="nowrap">
                <button type="button" onclick="tracking.remove(this, '${t.product.id}', '${t.trackingType.name()}');">
                    Удалить
                </button>
            </td>
        </tr>
        </#list>
        </tbody>
    </table>
</div>

<script type="text/javascript">
    var tracking = new bg.privacy.Tracking();
</script>
