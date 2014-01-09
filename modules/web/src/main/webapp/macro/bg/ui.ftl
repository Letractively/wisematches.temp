<#-- @ftlvariable name="imageResourcesDomain" type="java.lang.String" -->
<#-- @ftlvariable name="staticResourcesDomain" type="java.lang.String" -->

<#setting locale="en">
<#setting url_escaping_charset="UTF-8">

<#include "/core.ftl"/>
<#include "/macro/message.ftl"/>

<#import "/spring.ftl" as spring />

<#macro static p>${staticResourcesDomain}/${p}</#macro>

<#macro priceU v><span class="price"><span class="amount">US$<span
        class="usd v">${v?string("0.00")}</span></span></span></#macro>

<#macro price v c=""><span class="price"><span class="amount"
                                               itemprop="price">${v?string("0.00")}</span><img
        src="<@static "images/${c}ruble.gif"/>"
        class="ruble-img"/><span class="dot" itemprop="priceCurrency">RUB</span></span></#macro>

<#macro panel caption id="" class=id name=id>
<div <#if id?has_content>id="${id}"</#if> class="info-panel<#if class?has_content> ${class}"</#if>>
    <#if name?has_content><a name="${name}"></a></#if>

    <div class="tit">
        <div class="item">${caption}</div>
    </div>

    <div class="cnt">
        <div class="item">
            <#nested/>
        </div>
    </div>
</div>
</#macro>

<#macro productImageUrl imager code size>${imageResourcesDomain}/${imageResolver.resolveURI(imager, code, size)?replace("\\", "/")}</#macro>

<#macro productImage imager code size props={}><img
        src="<@bg.ui.productImageUrl imager code size/>"
        alt="${imager.name}" width="${size.width}px" height="${size.height}px"
    <#list props?keys as k>${k}="${props[k]}"</#list>>
</#macro>

<#macro coupon coupon>
<#--Купон на <#if coupon./>-->
</#macro>

<#macro productsView items type ops={}>
    <#if type =='grid'>
        <@productsViewGrid items ops/>
    </#if>
</#macro>

<#macro discountDiv a>
    <#if a.price.primordialAmount??>
        <#assign percents=((a.price.primordialAmount-a.price.amount)*100)/a.price.primordialAmount/>
        <#if (percents>0)>
        <div class="discount">-${percents?string("0")}%</div>
        </#if>
    </#if>
</#macro>

<#macro coupon coupon>
    <#if !coupon??>
    Неизвестный купон.
    <#else>
    Купон <em>${coupon.code}</em>
        <#if coupon.active>
            <#if coupon.amountType.fixed>
            предоставляет право купить за ${coupon.amount?string("0.00")}}руб.
            <#elseif coupon.amountType.price>
            снизить стоимость на ${coupon.amount?string("0.00")}}руб. за
            <#elseif coupon.amountType.percent>
            дает ${coupon.amount?string("0")}% скидку на
            </#if>

            <#if coupon.referenceType.product>
            товар <a
                    href="/warehouse/product/${coupon.reference}">${messageSource.getProductCode(coupon.reference)}</a>.
            <#elseif coupon.referenceType.category>
                <#assign category=catalog.getCategory(coupon.reference)/>
            товары категории <@bg.link.category category>${category.name}</@bg.link.category>.
            </#if>
        <#else>
        не активен и не может быть использован.
        </#if>
    </#if>
</#macro>

<#macro productItem product layout ops={}>
<div class="product-item ${layout} ${product.state.name()?lower_case}"
     itemscope itemtype="http://schema.org/Product">
    <div class="image small">
        <@bg.link.product product>
            <@bg.ui.productImage product product.previewImageId!"" ImageSize.SMALL {"title":"${product.name}", "itemprop": "image"}/>
        </@bg.link.product>
        <@discountDiv product/>
    </div>
    <#assign outStock=(product.stockInfo.stockState=StockState.SOLD_OUT || product.stockInfo.stockState=StockState.OUT_STOCK)/>
    <#if outStock><#assign color="g"/><#else><#assign color=""/></#if>
    <div class="name<#if outStock> sample</#if>"
         itemprop="name"><@bg.link.product product>${product.name}</@bg.link.product></div>
    <div <#if outStock>class="outstock"</#if> itemprop="offers" itemscope
         itemtype="http://schema.org/Offer"><@bg.ui.price product.price.amount color/></div>
    <#if ops["showCategory"]?? && ops["showCategory"]>
        <div class="category">раздел <@bg.link.category catalog.getCategory(product.categoryId)/></div>
    </#if>
    <#nested>
</div>
</#macro>

<#macro productsViewGrid products ops={}>
<table>
    <#list products as a>
        <@bg.ui.tableSplit products?size 4 a_index>
            <td valign="top" align="center" width="25%">
                <@productItem a 'grid' ops/>
            </td>
        </@bg.ui.tableSplit>
    </#list>
</table>
</#macro>

<#macro productsTable products pageableForm ops={}>
<div class="products">
    <div class="table-view">
        <div class="table-pages">
            <div class="table-position">
                <#if (pageableForm.filteredCount > 0)>
                    Показано ${(pageableForm.page-1)*pageableForm.count+1}
                    - ${(pageableForm.page-1)*pageableForm.count + products?size}
                    из ${pageableForm.filteredCount} элементов
                </#if>
            </div>

            <div class="table-controls">
                <@bg.ui.tableNavigation pageableForm/>
            </div>
        </div>

        <div class="table-filter">
            <div class="ipp">
                <strong>На странице: </strong>
                <ul>
                    <#list [12,24,36] as i>
                        <li class="bg-ui-button<#if pageableForm.count==i> selected</#if>">
                            <a href="?<@tableNavigationParams pageableForm "count" i/>">${i}</a>
                        </li>
                    </#list>
                </ul>
            </div>

            <#if pageableForm.sort?has_content>
                <div class="sort">
                    <strong><label for="tableSorting">Сортировать по: </label></strong>
                    <select id="tableSorting" name="sort">
                        <#list SortingType.values() as s>
                            <!-- RELEVANCE only if search-->
                            <#if pageableForm.query?has_content || s != SortingType.RELEVANCE>
                                <option value="?<@tableNavigationParams pageableForm "sort" s.getCode()/>"
                                        <#if s.getCode()==pageableForm.sort>selected="selected"</#if>><@message code="product.sort.type.${s.getCode()}"/></option>
                            </#if>
                        </#list>
                    </select>
                </div>
            </#if>
        </div>

        <div class="table-content">
            <#if products?has_content>
                <@productsView products 'grid' ops/>
            <#else>
                <div style="text-align: center">
                    <#if pageableForm.query?has_content>
                        В этой категории нет ни одного товара с ключевым словом <strong>${pageableForm.query}</strong>.
                        Попробуйте изменить либо убрать ключевое слово из поиска.
                    <#elseif pageableForm.filter?has_content>
                        В этой категории нет ни одного товара, соответствующего выбранным параметрам фильтрации.
                        Попробуйте уменьшить количество ограничений либо изменить их набор.
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
                <@tableNavigation pageableForm/>
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

<#macro tableNavigation pageableForm>
    <#assign page=pageableForm.page/>
    <#if (pageableForm.count > 0)>
        <#assign pagesCount=(pageableForm.filteredCount/pageableForm.count)?ceiling/>
        <#if (pagesCount > 1)>
        <ul>
            <#if (page > 1)>
                <@tableNavigationPageLink pageableForm page-1>Предыдущая</@tableNavigationPageLink>
            </#if>

            <#if (page - 3> 0)>
                <@tableNavigationPageLink pageableForm 1>1</@tableNavigationPageLink>
                <#if page-3 != 1>
                    <li style="display: inline-block; vertical-align: top">...</li>
                </#if>
            </#if>

            <#list (page-2)..(page-1) as i>
                <#if (i>0)>
                    <@tableNavigationPageLink pageableForm i>${i}</@tableNavigationPageLink>
                </#if>
            </#list>


            <@tableNavigationPageLink pageableForm page>${page}</@tableNavigationPageLink>

            <#list (page+1)..(page+2) as i>
                <#if (i<=pagesCount)>
                    <@tableNavigationPageLink pageableForm i>${i}</@tableNavigationPageLink>
                </#if>
            </#list>

            <#if (page+2<pagesCount)>
                <#if page+3 != pagesCount>
                    <li style="display: inline-block; vertical-align: top">...</li>
                </#if>

                <@tableNavigationPageLink pageableForm pagesCount>${pagesCount}</@tableNavigationPageLink>
            </#if>

            <#if (page < pagesCount)>
                <@tableNavigationPageLink pageableForm page+1>Следующая</@tableNavigationPageLink>
            </#if>
        </ul>
        </#if>
    </#if>
</#macro>

<#macro tableNavigationPageLink pageableForm page>
<li class="bg-ui-button<#if page==pageableForm.page> selected</#if>">
    <a href="?<@tableNavigationParams pageableForm "page" page/>"><#nested/></a>
</li>
</#macro>

<#macro tableNavigationParams pageableForm name value>page=<#if name="page">${value}<#else>${pageableForm.page}</#if>&count=<#if name="count">${value}<#else>${pageableForm.count}</#if>&sort=<#if name="sort">${value}<#else>${pageableForm.sort!""}</#if><#if name="query">&query=${value?url}<#elseif pageableForm.query?has_content>&query=${pageableForm.query?url}</#if><#if name="filter">&filter=${value}<#elseif pageableForm.filter?has_content>&filter=${pageableForm.filter?url}</#if><#if pageableForm.category?has_content>&category=${pageableForm.category}</#if></#macro>

<#macro whereabouts showDepartment=true>
<div class="title">
    <a href="/">Домашняя страница</a>
    <#if department??>
        <#if showDepartment>
            > <a href="/${department.code}"><@message code="title.${department.code}"/></a>
        </#if>
        <#if section?has_content>
            > <a href="/${department.code}/${section}"><@message code="title.${department.code}.${section}"/></a>
        </#if>
    </#if>
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

<#macro enum path values attributes="">
    <@bg.ui.field path>
    <select id="${bg.ui.status.expression}" name="${bg.ui.status.expression}">
        <#list values as v>
            <option value="${v.name()}"
                    <#if bg.ui.actualValue=v>selected="selected"</#if>><@message code="enum.${v.name()?lower_case}.label"/></option>
        </#list>
    </select>
    </@bg.ui.field>
</#macro>

<#macro input path attributes="" fieldType="text" size=30 value="">
    <@bg.ui.field path>
    <input type="${fieldType}" id="${bg.ui.status.expression}" name="${status.expression}" size="${size}"
        <#if fieldType=='checkbox'><#if status.value?has_content && status.value=='true'>checked="checked"</#if>
           value="true"<#else>
           value="<#if fieldType!="password"><#if status.value?has_content>${status.value}<#else><@message code=value/></#if></#if>"</#if> ${attributes}/>
        <#if fieldType=='checkbox'>
        <input id="${bg.ui.status.expression}Hidden" type="hidden" name="${status.expression}" value="false"/>
        </#if>
        <#nested>
    </@bg.ui.field>
</#macro>

<#macro categoryOption category level selected>
<option <#if category.id==selected>selected="selected"</#if> value="${category.id}"><#list 0..level as i>
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
            <@categoryOption c 0 status.actualValue!0/>
        </#list>
    </select>
        <#nested/>
    </@bg.ui.field>
</#macro>

<#macro widget caption class="" style="">
<div class="widget<#if class?has_content> ${class}</#if>" <#if style?has_content>style="${style}"</#if>>
    <#if caption?has_content>
        <div class="title">${caption}</div>
    </#if>

    <div class="content">
        <#nested/>
    </div>
</div>
</#macro>