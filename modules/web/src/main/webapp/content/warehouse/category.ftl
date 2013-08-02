<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="articles" type="billiongoods.server.warehouse.ArticleDescription[]" -->
<#-- @ftlvariable name="itemsTableForm" type="billiongoods.server.web.servlet.mvc.warehouse.form.ItemsTableForm" -->

<#include "/core.ftl">

<#macro pageLink page>
<li class="bg-ui-button<#if page==itemsTableForm.page> selected</#if>">
    <a href="?page=${page}&count=${itemsTableForm.count}&sort=${itemsTableForm.sort}"><#nested/></a>
</li>
</#macro>

<#macro tableNavigation itemsTableForm>
    <#assign page=itemsTableForm.page/>
    <#if (itemsTableForm.count > 0)>
        <#assign count=(itemsTableForm.totalCount/itemsTableForm.count)?ceiling/>
    <ul>
        <#if (page > 1)>
            <@pageLink page-1>Предыдущая</@pageLink>
        </#if>

        <#if (page - 3> 0)>
            <@pageLink 1>1</@pageLink>
            <li style="display: inline-block; vertical-align: top">...</li>
        </#if>

        <#list (page-2)..(page-1) as i>
            <#if (i>0)>
                <@pageLink i>${i}</@pageLink>
            </#if>
        </#list>


        <@pageLink page>${page}</@pageLink>

        <#list (page+1)..(page+2) as i>
            <#if (i<=count)>
                <@pageLink i>${i}</@pageLink>
            </#if>
        </#list>

        <#if (page+2<count)>
            <li style="display: inline-block; vertical-align: top">...</li>

            <@pageLink count>${count}</@pageLink>
        </#if>

        <#if (page < count)>
            <@pageLink page+1>Следующая</@pageLink>
        </#if>
    </ul>
    </#if>
</#macro>

<div class="articles">
    <div class="table-view">
        <div class="table-pages">
            <div class="table-position">
                Показано ${itemsTableForm.page + 1} - ${itemsTableForm.page + articles?size}
                из ${itemsTableForm.totalCount} элементов
            </div>

            <div class="table-controls">
            <@tableNavigation itemsTableForm/>
            </div>
        </div>

        <div class="table-filter">
            <div class="ipp">
                <strong>На странице: </strong>
                <ul>
                <#list [12,24,36] as i>
                    <li class="bg-ui-button<#if itemsTableForm.count==i> selected</#if>">
                        <a href="?page=${itemsTableForm.page}&count=${i}&sort=${itemsTableForm.sort}">${i}</a>
                    </li>
                </#list>
                </ul>
            </div>

            <div class="sort">
                <strong>Сортировать по: </strong>
                <select id="sort" name="sort">
                <#list ItemSortType.values() as s>
                    <option value="${s.getName()}"
                            <#if s.getName()==itemsTableForm.sort>selected="selected"</#if>><@message code="article.sort.type.${s.getName()}"/></option>
                </#list>
                </select>
            </div>
        </div>

        <div class="table-content">
        <#if articles?has_content>
            <table>
                <#list articles as a>
                    <#if a_index%4==0>
                    <tr></#if>
                    <td>
                        <div class="article-item grid">
                            <div class="image">
                                <@bg.link.article a><img
                                        alt="${a.name}"
                                        title="${a.name}"
                                        src="<@bg.ui.image a a.previewImageId!"" ImageSize.SMALL/>"
                                        width="150px" height="150px"/></@bg.link.article>
                            </div>
                            <div class="name"><@bg.link.article a>${a.name}</@bg.link.article></div>
                            <div class="price"><@bg.ui.price a.price/></div>
                        </div>
                    </td>
                    <#if (a_index-3)%4==0></tr></#if>
                </#list>
            </table>
        <#else>
            <div style="text-align: center">
                В этой категории еще нет ни одного товара но мы работает над их добавлением.
            </div>
        </#if>
        </div>

        <div class="table-pages">
            <div class="table-position">
                &nbsp;
            </div>

            <div class="table-controls">
            <@tableNavigation itemsTableForm/>
            </div>
        </div>
    </div>
</div>
