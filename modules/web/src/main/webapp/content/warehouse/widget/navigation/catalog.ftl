<#include "/core.ftl"/>

<div class="navigation catalog">
    <div class="ct-tit">
        Каталог товаров
    </div>

    <ul class="container">
    <#list catalog.rootCategories as p>
        <li class="ct-item">
            <div class="ct-toc"><span class="image"></span><@bg.link.categoryLink p/></div>

            <div class="ct-list" style="display: none">
                <#list p.children as category>
                    <#if category_index%3==0>
                    <div style="float: left"></#if>

                    <ul>
                        <li class="ct-list-tit">
                            <span class="image"></span><@bg.link.categoryLink category/></li>

                        <#list category.children as ch>
                            <li class="ct-list-cnt">
                                <span class="image"></span><@bg.link.categoryLink ch/></li>
                        </#list>
                    </ul>
                    <#if (category_index-2)%3==0></div></#if>
                </#list>
            </div>
        </li>
    </#list>
    </ul>
</div>

<script type="application/javascript">
    $(".ct-item").hover(function () {
        var $2 = $(this);
        $2.find(".ct-toc").addClass("hover");
        $2.find(".ct-list").show();
    }, function () {
        var $2 = $(this);
        $2.find(".ct-toc").removeClass("hover");
        $2.find(".ct-list").hide();
    });
</script>