<#-- @ftlvariable name="imageResourcesDomain" type="java.lang.String" -->
<#-- @ftlvariable name="staticResourcesDomain" type="java.lang.String" -->
<#-- @ftlvariable name="itemsTableForm" type="billiongoods.server.web.servlet.mvc.warehouse.form.ItemsTableForm" -->

<#setting locale="en">

<#include "/core.ftl"/>
<#include "/macro/message.ftl"/>

<#import "/spring.ftl" as spring />


<#macro static p>${staticResourcesDomain}/${p}</#macro>


<#macro usd v><span class="usd">US$<span class="usd v">${v?string("0.00")}</span></span></#macro>

<#macro rub v c="g"><span class="rub v">${v?string("0.00")}</span><img
        src="<@static "images/${c}ruble.gif"/>"
        class="ruble-img"/><span
        class="dot">руб.</span></#macro>

<#macro price v c="g" r=exchangeManager.exchangeRate><@usd v/> <span class="rub">(~<@rub v*r c/>)</span></#macro>


<#macro panel caption id="" class=id name=id>
<div <#if id?has_content>id="${id}"</#if> class="info-panel<#if class?has_content> ${class}"</#if>>
    <#if name?has_content><a name="${name}"></a></#if>

    <div class="tit">${caption}</div>

    <div class="cnt"><#nested/></div>
</div>
</#macro>

<#macro categoriesTree >

</#macro>



<#macro articlesView items type ops={}>
    <#if type =='grid'>
        <@articlesViewGrid items ops/>
    </#if>
</#macro>

<#macro articlesViewGrid articles ops={}>
<table>
    <#list articles as a>
        <@bg.ui.tableSplit articles?size 4 a_index>
            <td valign="top" width="25%">
                <div class="article-item grid<#if !a.active> inactive</#if>">
                    <div class="image">
                        <@bg.link.article a><img
                                alt="${a.name}"
                                title="${a.name}"
                                src="<@articleImg a a.previewImageId!"" ImageSize.SMALL/>"
                                width="150px" height="150px"/></@bg.link.article>
                    </div>
                    <div class="name"><@bg.link.article a>${a.name}</@bg.link.article></div>
                    <div class="price"><@bg.ui.price a.price/></div>
                    <#if ops["showCategory"]?? && ops["showCategory"]>
                        <div class="category">раздел <@bg.link.categoryLink catalog.getCategory(a.categoryId)/></div>
                    </#if>
                </div>
            </td>
        </@bg.ui.tableSplit>
    </#list>
</table>
</#macro>


<#macro articleImg article code type>${imageResourcesDomain}/${imageResolver.resolveURI(article, code, type)?replace("\\", "/")}</#macro>

<#macro articlesTable articles itemsTableForm>
<div class="articles">
    <div class="table-view">
        <div class="table-pages">
            <div class="table-position">
                <#if (itemsTableForm.totalCount > 0)>
                    Показано ${itemsTableForm.page + 1} - ${itemsTableForm.page + articles?size}
                    из ${itemsTableForm.totalCount} элементов
                <#else>
                    В этой категории нет ни одного элемента <#if itemsTableForm.query?has_content>по ключевому
                    слову <strong>${itemsTableForm.query}</strong></#if>
                </#if>
            </div>

            <div class="table-controls">
                <@bg.ui.tableNavigation itemsTableForm/>
            </div>
        </div>

        <div class="table-filter">
            <input id="tableFormPage" type="hidden" value="${itemsTableForm.page}"/>
            <input id="tableFormCount" type="hidden" value="${itemsTableForm.count}"/>
            <input id="tableFormSort" type="hidden" value="${itemsTableForm.sort}"/>

            <div class="query">
                <input id="tableQuery" name="query" value="${itemsTableForm.query!""}"/>
                <button id="tableQueryButton" type="button">Искать</button>
            </div>

            <div class="ipp">
                <strong>На странице: </strong>
                <ul>
                    <#list [12,24,36] as i>
                        <li class="bg-ui-button<#if itemsTableForm.count==i> selected</#if>">
                            <a href="?<@tableNavigationParams itemsTableForm.page i itemsTableForm.sort itemsTableForm.query!""/>">${i}</a>
                        </li>
                    </#list>
                </ul>
            </div>

            <#if itemsTableForm.sort?has_content>
                <div class="sort">
                    <strong>Сортировать по: </strong>
                    <select id="tableSorting" name="sort">
                        <#list ItemSortType.values() as s>
                            <option value="${s.getName()}"
                                    <#if s.getName()==itemsTableForm.sort>selected="selected"</#if>><@message code="article.sort.type.${s.getName()}"/></option>
                        </#list>
                    </select>
                </div>
            </#if>
        </div>

        <div class="table-content">
            <#if articles?has_content>
                <@articlesView articles 'grid'/>
            <#else>
                <div style="text-align: center">
                    <#if itemsTableForm.query?has_content>
                        В этой категории нет ни одного товара с ключевым словом <strong>${itemsTableForm.query}</strong>.
                        Попробуйте изменить либо убрать ключевое слово из поиска.
                    <#else>
                        В этой категории еще нет ни одного товара но мы работает над их добавлением. Попробуйте зайте
                        завтра, у
                        нас очень редко бывают пустые категории.
                    </#if>
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
</#macro>

<#macro tableSplit cells columns index tag="tr" style="">
    <#if index%columns==0><${tag}<#if style?has_content> style="${style}"</#if>></#if>
    <#nested/>
    <#if (index-(columns-1))%columns==0>
    </${tag}>
    <#else>
        <#if cells-1==index>
            <#list index%columns..(columns-2) as i>
            <td></td></#list>
        </${tag}>
        </#if>
    </#if>
</#macro>

<#macro tableNavigation itemsTableForm>
    <#assign page=itemsTableForm.page/>
    <#if (itemsTableForm.count > 0)>
        <#assign pagesCount=(itemsTableForm.totalCount/itemsTableForm.count)?ceiling/>
        <#if (pagesCount > 1)>
        <ul>
            <#if (page > 1)>
                <@tableNavigationPageLink itemsTableForm page-1>Предыдущая</@tableNavigationPageLink>
            </#if>

            <#if (page - 3> 0)>
                <@tableNavigationPageLink itemsTableForm 1>1</@tableNavigationPageLink>
                <li style="display: inline-block; vertical-align: top">...</li>
            </#if>

            <#list (page-2)..(page-1) as i>
                <#if (i>0)>
                    <@tableNavigationPageLink itemsTableForm i>${i}</@tableNavigationPageLink>
                </#if>
            </#list>


            <@tableNavigationPageLink itemsTableForm page>${page}</@tableNavigationPageLink>

            <#list (page+1)..(page+2) as i>
                <#if (i<=pagesCount)>
                    <@tableNavigationPageLink itemsTableForm i>${i}</@tableNavigationPageLink>
                </#if>
            </#list>

            <#if (page+2<pagesCount)>
                <li style="display: inline-block; vertical-align: top">...</li>

                <@tableNavigationPageLink itemsTableForm pagesCount>${pagesCount}</@tableNavigationPageLink>
            </#if>

            <#if (page < pagesCount)>
                <@tableNavigationPageLink itemsTableForm page+1>Следующая</@tableNavigationPageLink>
            </#if>
        </ul>
        </#if>
    </#if>
</#macro>

<#macro tableNavigationParams page count sort query>page=${page}&count=${count}&sort=${sort}<#if query?has_content>&query=${query}</#if></#macro>

<#macro tableNavigationPageLink itemsTableForm page>
<li class="bg-ui-button<#if page==itemsTableForm.page> selected</#if>">
    <a href="?<@tableNavigationParams page itemsTableForm.count itemsTableForm.sort itemsTableForm.query!""/>"><#nested/></a>
</li>
</#macro>

<#macro whereabouts>
<div class="title">
    <a href="/">Домашняя страница</a> >
    <#nested/>
</div>
</#macro>

<#macro bind path>
    <@spring.bind path/>

    <#assign status=spring.status>
    <#assign statusValue=spring.stringStatusValue>
    <#if status.actualValue??><#assign actualValue=status.actualValue></#if>
</#macro>

<#macro field path id="" class="">
    <@bind path/>

<div <#if id?has_content>id="${id}"</#if>
     class="<#if spring.status.error>field-error<#else>field-ok</#if><#if class?has_content> ${class}</#if>">

    <#nested >

    <#list status.errorMessages as msg>
        <div class="ui-state-error-text error-msg">${msg}</div>
    </#list>
</div>
</#macro>

<#macro input path attributes="" fieldType="text" size=30 value="">
    <@field path=path>

    <input type="${fieldType}" id="${status.expression}" name="${status.expression}" size="${size}"
        <#if fieldType=='checkbox'><#if status.value?has_content && status.value=='true'>checked="checked"</#if>
           value="true"<#else>
           value="<#if fieldType!="password"><#if status.value?has_content>${status.value}<#else><@message code=value/></#if></#if>"</#if> ${attributes}/>
        <#nested>
    </@field>
</#macro>

<#macro categoryOption category level selected>
<option <#if category.id?string==selected>selected="selected"</#if> value="${category.id}"><#list 0..level as i>
    -- </#list>${category.name} /${category.id}</option>
    <#list category.children as c>
        <@categoryOption c level+1 selected/>
    </#list>
</#macro>

<#macro selectCategory path catalog root=false>
    <@bg.ui.field path>
    <select name="${status.expression}">
        <#if root>
            <option value="">ROOT</option></#if>
        <#list catalog.rootCategories as c>
            <@categoryOption c 0 status.value!""/>
        </#list>
    </select>
    </@bg.ui.field>
</#macro>