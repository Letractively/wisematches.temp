<#-- @ftlvariable name="showcase" type="billiongoods.server.services.showcase.Showcase" -->
<#-- @ftlvariable name="showcaseCache" type="java.util.Map<ShowcaseItem, ArticleDescription[]>" -->

<#include "/core.ftl">

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
            </div>

            <div class="cnt table-content">
                <#list g.showcaseItems as i>
                    <div id="sc_${g_index}_${i_index}" class="item"
                         <#if i_index !=0>style="display: none;" </#if>>
                        <@bg.ui.articlesViewGrid showcaseCache.get(i) {"showCategory":true}/>
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
            $(v).find(".cnt #sc_" + attr).show();
        });
    });
</script>