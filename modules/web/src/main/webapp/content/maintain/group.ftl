<#-- @ftlvariable name="group" type="billiongoods.server.warehouse.Group" -->
<#-- @ftlvariable name="groups" type="billiongoods.server.warehouse.Group[]" -->

<#include "/core.ftl">

<table cellspacing="5">
    <tr>
        <td valign="top" style="padding: 10px; border: 1px solid gray;">
            <div>
                <form action="/maintain/group" method="post">
                    <table style="width: 200px">
                    <#if group??>
                        <tr>
                            <td><label>Номер: </label></td>
                            <td>
                            ${group.id}
                            </td>
                        </tr>
                    </#if>

                        <tr>
                            <td><label for="name">Имя: </label></td>
                            <td>
                            <@bg.ui.input path="form.name"/>
                            </td>
                        </tr>

                        <tr>
                            <td><label for="type">Тип: </label></td>
                            <td>
                            <@bg.ui.enum "form.type" GroupType.values()/>
                            </td>
                        </tr>

                        <tr>
                            <td><label for="categoryId">Категория: </label></td>
                            <td>
                            <@bg.ui.selectCategory "form.categoryId" catalog true/>
                            </td>
                        </tr>

                        <tr>
                            <td></td>
                            <td>
                            <@bg.ui.input path="form.id" fieldType="hidden"/>
                            <#if group??>
                                <button id="add" name="action" value="update" type="submit">Изменить</button>
                                <button id="remove" name="action" value="remove" type="submit">Удалить</button>
                            <#else>
                                <button id="add" name="action" value="create" type="submit">Создать</button>
                                <button id="search" name="action" value="search" type="submit">Найти</button>
                            </#if>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
        </td>
    <#if groups??>
        <td valign="top" style="padding: 10px; border: 1px solid gray;">
            <div>
                <#list groups as g>
                    <div>
                        <a href="/maintain/group?id=${g.id}">${g.name} (${g.productPreviews?size})</a>
                    </div>
                </#list>
            </div>
        </td>
    </#if>
    </tr>
</table>

<#if group??>
<div class="items" style="padding-top: 20px">
    <table>
        <#if group.productPreviews?size != 0>
            <tr>
                <td colspan="4">
                    Продуктов в группе: ${group.productPreviews?size}
                    <hr>
                </td>
            </tr>
            <#list group.productPreviews as a>
                <#assign active=a.state == ProductState.ACTIVE || a.state == ProductState.PROMOTED/>
                <tr class="item">
                    <td>
                        <@bg.link.product a>
                            <span <#if !active>style="text-decoration: line-through"</#if>>
                            ${messageSource.getProductCode(a)}
                            </span>
                        </@bg.link.product>
                    </td>
                    <td width="100%">${a.name}</td>
                    <td>
                        <span <#if active>class="sample"</#if>>(${a.state})</span>
                    </td>
                    <td>
                        <button type="button" name="productId" value="${a.id}">Удалить</button>
                    </td>
                </tr>
            </#list>
        <#else>
            <tr>
                <td>В этой группе нет ни одного элемента</td>
            </tr>
        </#if>
    </table>
</div>

<script type="text/javascript">
    $(".items button").click(function () {
        var btn = $(this);
        bg.ui.lock(null, 'Обработка запроса. Пожалуйста, подождите...');
        $.post("/maintain/group/relationship.ajax?action=remove", JSON.stringify({"productId": btn.val(), "groupId": "${group.id}"}))
                .done(function (response) {
                    if (response.success) {
                        $(btn).closest('tr').remove();
                        bg.ui.unlock(null, "Товар удален", false);
                    } else {
                        bg.ui.unlock(null, response.message, true);
                    }
                })
                .fail(function (jqXHR, textStatus, errorThrown) {
                    bg.ui.unlock(null, "В связи с внутренней ошибкой мы не смогли обработать ваш запрос. Если проблема " +
                            "не исчезла, пожалуйста, свяжитесь с нами.", true);
                });
    });
</script>
</#if>