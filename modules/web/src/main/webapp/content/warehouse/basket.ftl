<#-- @ftlvariable name="basket" type="billiongoods.server.services.basket.Basket" -->

<#include "/core.ftl"/>

<table class="basket">
<#if basket?has_content>
    <tr>
        <th></th>
        <th>Наименование</th>
        <th>Количество</th>
        <th>Стоимость</th>
    </tr>
    <#list basket.basketItems as i>
        <#assign article=i.article/>
        <tr class="item">
            <th nowrap="nowrap" align="center">
                <input type="checkbox" name="remove" value="${i.number}"/>
            </th>
            <td width="100%" align="left">
                <div style="display: inline-block; float: left; margin-right: 10px">
                    <img src="<@bg.ui.articleImg article article.previewImageId!"" ImageSize.TINY/>" width="50px"
                         height="50px"/>
                </div>

                <div style="display: inline-block">
                    <@bg.link.article article>${article.name}</@bg.link.article>

                    <ul>
                        <#list i.options as o>
                            <li>${o.attribute.name}: ${o.value}</li>
                        </#list>
                    </ul>
                </div>
            </td>
            <td nowrap="nowrap" align="center">
                <div class="quantity">
                    <button class="q_down bg-ui-button" disabled="disabled" type="button"> -</button>
                    <input class="q_input" name="quantity" value="${i.quantity}">
                    <button class="q_up bg-ui-button" type="button"> +</button>
                </div>
                <div class="controls">
                    <button type="button">Изменить</button>
                </div>
            </td>
            <td nowrap="nowrap" align="left">
                <@bg.ui.price article.price/>
            </td>
        </tr>
    </#list>
    <tr>
        <th align="center">
            <input type="checkbox" name="removeAll"/>
        </th>
        <th align="left" colspan="3" class="controls">
            <button type="button">Удалить выделенные</button>
            <button type="button">Очистить Корзину</button>
        </th>
    </tr>
<#else>
    <tr>
        <td align="center">
            У вас в корзине нет ни одного товара.
        </td>
    </tr>
</#if>
</table>

<div class="">

</div>