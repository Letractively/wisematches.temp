/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */
CKEDITOR.editorConfig = function (config) {
    config.language = 'ru';
    config.allowedContent = true;

    config.extraPlugins = 'youtube';

    config.toolbar = [
        {name: 'clipboard', items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo']},
        {name: 'insert', items: ['CreatePlaceholder', 'Image', 'Flash', 'Youtube', 'Table', 'HorizontalRule', 'PageBreak', 'InsertPre']},
        {name: 'links', items: ['Link', 'Unlink', 'Anchor']},
        {name: 'others', items: ['ShowBlocks', '-', 'Source', '-', 'Maximize']},
        '/',
        {name: 'basicstyles', items: ['Styles', 'Format', 'Font', 'FontSize']},
        {name: 'paragraph', items: ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv']},
        '/',
        {name: 'tools', items: ['Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat']},
        {name: 'align', items: ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock']},
        {name: 'colors', items: ['TextColor', 'BGColor']}
    ];

    config.format_tags = 'p;h1;h2;h3;pre';
};
