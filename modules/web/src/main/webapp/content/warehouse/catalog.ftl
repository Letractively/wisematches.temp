<#-- @ftlvariable name="showcase" type="billiongoods.server.services.showcase.Showcase" -->
<#-- @ftlvariable name="showcaseCache" type="java.util.Map<billiongoods.server.services.showcase.ShowcaseItem, billiongoods.server.warehouse.ProductPreview[]>" -->

<#include "/core.ftl">

<#if showcase??>
<div class="showcase">
    <#list showcase.showcaseGroups as g>
        <div class="showcase-group">
            <div class="info-panel">
                <div class="tit<#if (g.showcaseItems?size>1)> grouped</#if>">
                    <#list g.showcaseItems as i>
                        <div id="st_${g_index}_${i_index}"
                             class="item <#if (g.showcaseItems?size > 1 && i_index==0)>active</#if>">
                        ${i.name}
                        </div>
                    </#list>

                    <div class="more" style="float: right">
                        <#list g.showcaseItems as i>
                            <a id="sl_${g_index}_${i_index}" href="${i.moreInfoUri}"
                               <#if i_index !=0>style="display: none;" </#if>>Показать все</a>
                        </#list>
                    </div>
                </div>

                <div class="cnt table-content">
                    <#list g.showcaseItems as i>
                        <div id="sc_${g_index}_${i_index}" class="item"
                             <#if i_index !=0>style="display: none;" </#if>>
                            <@bg.ui.productsViewGrid showcaseCache.get(i)/>
                        </div>
                    </#list>
                </div>
            </div>
        </div>
    </#list>
</div>

<script type="text/javascript">
    $(".showcase-group").each(function (i, v) {
        $(v).find(".tit.grouped .item").click(function () {
            $(v).find(".tit .item").removeClass('active');

            var attr = $(this).addClass('active').attr('id').substr(3);

            $(v).find(".cnt .item").hide();
            $(v).find("#sc_" + attr).show();

            $(v).find(".more a").hide();
            $(v).find("#sl_" + attr).show();
        });
    });
</script>
<#else>
<div style="text-align: center;"><h1>
    Кагалог продуктов еще не загружен. Пожалуйста, обновите страницу.
</h1></div>
</#if>