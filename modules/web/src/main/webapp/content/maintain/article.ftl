<#include "/core.ftl">

<script type="text/javascript" src="<@bg.ui.static "ckeditor/ckeditor.js"/>"></script>


<script>
    window.onload = function () {
        CKEDITOR.replace('description');
    };
</script>