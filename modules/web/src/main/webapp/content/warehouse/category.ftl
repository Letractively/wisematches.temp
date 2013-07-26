<#-- @ftlvariable name="category" type="billiongoods.server.warehouse.Category" -->
<#-- @ftlvariable name="articles" type="billiongoods.server.warehouse.ArticleDescription[]" -->
<#-- @ftlvariable name="itemsTableForm" type="billiongoods.server.web.servlet.mvc.warehouse.form.ItemsTableForm" -->

<#include "/core.ftl">

<div class="articles">
    <div class="table-view">
        <div class="table-pages">
            <div class="table-position">
                Показано ${itemsTableForm.page + 1} - ${itemsTableForm.page + articles?size}
                из ${itemsTableForm.totalCount} элементов
            </div>

            <div class="table-controls">
            <#assign pagesCount=(itemsTableForm.totalCount/itemsTableForm.count)?ceiling/>
                Total pages: ${pagesCount}
                Select page: ${itemsTableForm.page}
            </div>
        </div>

        <div class="table-filters"></div>

        <div class="table-content">
            <div class="grid">
            <#list articles as a>
                <div class="article">
                    <div class="image">
                        <@bg.link.article a><@bg.ui.articleImg a a.previewImageId ImageType.PREVIEW 150/></@bg.link.article>
                    </div>
                    <div class="name"><@bg.link.article a>${a.name}</@bg.link.article></div>
                    <div class="price"><@bg.ui.price a.price/></div>
                </div>
            </#list>
            </div>
        </div>

        <div class="table-pages"></div>
    </div>
</div>
