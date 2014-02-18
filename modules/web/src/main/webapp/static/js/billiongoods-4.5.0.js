/*
 * Copyright (c) 2013, BillionGoods.
 */
bg = {};
bg.util = {};

STATE = {
    DEFAULT: {
        class: 'ui-state-highlight'
    },
    INFO: {
        class: 'ui-state-active'
    },
    ERROR: {
        class: 'ui-state-error'
    }
};

bg.util.url = new function () {
    this.reload = function () {
        window.location.reload();
    };

    this.redirect = function (url) {
        window.location = url;
    };

    this.remove = function (sourceUrl, parameterName) {
        if ((sourceUrl == null) || (sourceUrl.length == 0)) sourceUrl = document.location.href;
        var split = sourceUrl.split("#");
        var urlParts = split[0].split("?");
        var newQueryString = "";
        if (urlParts.length > 1) {
            var parameters = urlParts[1].split("&");
            for (var i = 0; (i < parameters.length); i++) {
                var parameterParts = parameters[i].split("=");
                if (parameterParts[0] != parameterName) {
                    if (newQueryString == "")
                        newQueryString = "?";
                    else
                        newQueryString += "&";
                    newQueryString += parameterParts[0] + "=" + parameterParts[1];
                }
            }
        }
        return urlParts[0] + newQueryString + (split[1] != undefined ? "#" + split[1] : "");
    };

    this.extend = function (sourceUrl, parameterName, parameterValue, replaceDuplicates) {
        if ((sourceUrl == null) || (sourceUrl.length == 0)) sourceUrl = document.location.href;

        var split = sourceUrl.split("#");
        var urlParts = split[0].split("?");
        var newQueryString = "";
        if (urlParts.length > 1) {
            var parameters = urlParts[1].split("&");
            for (var i = 0; (i < parameters.length); i++) {
                var parameterParts = parameters[i].split("=");
                if (!(replaceDuplicates && parameterParts[0] == parameterName)) {
                    if (newQueryString == "")
                        newQueryString = "?";
                    else
                        newQueryString += "&";
                    newQueryString += parameterParts[0] + "=" + parameterParts[1];
                }
            }
        }
        if (newQueryString == "")
            newQueryString = "?";
        else
            newQueryString += "&";
        newQueryString += parameterName + "=" + parameterValue;
        return urlParts[0] + newQueryString + (split[1] != undefined ? "#" + split[1] : "");
    };
};

bg.ui = new function () {
    var statusWidgetPane;
    var alertsWidgetPane;
    var activeWindows = true;

    $.blockUI.defaults.message = null;

    $.blockUI.defaults.css = {
        padding: 0,
        margin: 0,
        width: '30%',
        top: '40%',
        left: '35%',
        textAlign: 'center',
        'border-width': '3px'
    };

    $.blockUI.defaults.overlayCSS = {
        opacity: 0.2,
        cursor: 'wait',
        backgroundColor: '#DFEFFC'
    };

    $.ajaxSetup({
        type: 'post',
        dataType: 'json',
        contentType: 'application/json'
    });

    var alertTemplate = function (title, message) {
        var e;
        e = ['<div>', '<div class="content">', '<h2>' + title + '</h2>', '<p>' + message + '</p>', '</div>', '<span class="icon"></span>', '<span class="close"></span>', '</div>'].join("");
        return e;
    };

    var messageTemplate = function (title, message) {
        if (title == null || title == undefined) {
            return '<div style="padding: 10px 24px;">' + message + '</div><div class="closeButton"><a href="#"><div class="wm-icon-close"/></a></div>';
        } else {
            var e;
            e = ['<div>', '<div class="content">', '<h2>' + title + '</h2>', '<p>' + message + '</p>', '</div>', '<span class="icon"></span>', '<span class="close"></span>', '</div>'].join("");
            return e;
        }
    };

    var statusTemplate = function (title, message) {
        return '<div><div class="content">' + message + '</div></div>';
    };

    var showStatus = function (message, severity, stick) {
        statusWidgetPane.empty();

        if (stick == undefined) {
            stick = false;
        }

        var opts = {
            classes: [ severity.class, "ui-corner-all shadow"],
            template: statusTemplate,
            autoHide: !stick,
            autoHideDelay: 10000
        };
        if (stick) {
            opts = $.extend(opts, {onClick: function () {
            }, onHover: function () {
            }});
        }
        statusWidgetPane.freeow(null, message, opts);
    };

    var clearStatus = function () {
        var freeow = statusWidgetPane.children().data("freeow");
        if (freeow != null) {
            freeow.hide();
        } else {
            statusWidgetPane.empty();
        }
    };

    this.lock = function (element, message) {
        if (element != null && element != undefined) {
            element.block({message: null});
        } else {
            $.blockUI({message: null});
        }
        if (message != null && message != undefined) {
            showStatus(message, STATE.DEFAULT, true);
        }
    };

    this.unlock = function (element, message, error) {
        if (error == null || error == undefined) {
            error = false;
        }

        if (element != null && element != undefined) {
            element.unblock();
        } else {
            $.unblockUI();
        }

        if (message == null || message == undefined) {
            clearStatus();
        } else {
            showStatus(message, error ? STATE.ERROR : STATE.INFO, false);
        }
    };

    this.message = function (element, title, message, error) {
        var v = {
            message: messageTemplate(title, message),
            blockMsgClass: 'ui-corner-all shadow' + (error ? ' ui-state-error' : ' ui-state-default'),
            draggable: false
        };

        if (element != undefined && element != null) {
            element.block(v);
        } else {
            $.blockUI(v);
        }

        var processClose = function () {
            if (element != undefined && element != null) {
                element.unblock();
            } else {
                $.unblockUI();
            }
        };
        $('.closeButton').click(processClose);
        $('.blockOverlay').click(processClose);
    };

    this.confirm = function (title, msg, approvedAction) {
        $('<div></div>').html(msg).dialog({
            title: title,
            draggable: false,
            modal: true,
            resizable: false,
            width: 400,
            buttons: [
                {
                    text: 'Да',
                    click: function () {
                        $(this).dialog("close");
                        approvedAction(true);
                    }
                },
                {
                    text: 'Отмена',
                    click: function () {
                        $(this).dialog("close");
                        approvedAction(false);
                    }
                }
            ]
        });
    };


    this.notification = function (title, message, type, error) {
        alertsWidgetPane.freeow(title, message, {
            classes: [ error ? "ui-state-error" : "ui-state-highlight", "ui-corner-all", "shadow", type],
            showStyle: {opacity: .95},
            template: alertTemplate,
            autoHideDelay: 10000
        });

        if (!activeWindows) {
            $(window).stopTime('attention-timer');
            var documentTitle = document.title;
            $(window).everyTime(500, 'attention-timer', function (i) {
                if (i % 2 == 0) {
                    document.title = "*** " + title + " ***";
                } else {
                    document.title = documentTitle;
                }
            });
        }
    };

    this.refreshImage = function (element) {
        var el = $(element);
        if (el.attr('src').indexOf("?") < 0) {
            el.attr('src', el.attr('src') + '?' + new Date().getTime());
        } else {
            el.attr('src', el.attr('src') + '&' + new Date().getTime());
        }
    };

    this.popupwindow = function (url, title, w, h) {
        var left = (screen.width / 2) - (w / 2);
        var top = (screen.height / 2) - (h / 2);
        return window.open(url, title, 'toolbar=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=yes, copyhistory=no, width=' + w + ', height=' + h + ', top=' + top + ', left=' + left);
    };

    $(document).ready(function () {
        var body = $("body");
        statusWidgetPane = $("<div id='status-widget-pane' class='freeow-widget status-widget-pane'></div>").appendTo(body);
        alertsWidgetPane = $("<div id='alerts-widget-pane' class='freeow-widget alerts-widget-pane'></div>").appendTo(body);

        var $window = $(window);
        var $header = $("#header");
        var windowScroll = function () {
            var height = $header.outerHeight(true);
            var scrollY = $window.scrollTop();
            if (height - scrollY >= 0) {
                statusWidgetPane.css({top: height - scrollY});
                alertsWidgetPane.css({top: height - scrollY});
            } else if (statusWidgetPane.offset().top != 0) {
                statusWidgetPane.css({top: 0});
                alertsWidgetPane.css({top: 0});
            }
        };
        $window.scroll(windowScroll).resize(windowScroll);
        windowScroll();

        var activeWindowTitle = document.title;
        $(window).bind("focus", function () {
            activeWindows = true;
            if (activeWindowTitle != undefined) {
                document.title = activeWindowTitle;
            }
            $(window).stopTime('attention-timer');
        });
        $(window).bind("blur", function () {
            activeWindows = false;
            activeWindowTitle = document.title;
        });
    });
};

bg.account = {};

bg.account.SignIn = function () {

};


bg.assistance = {};

bg.assistance.SupportForm = function () {
    var place = $("#supportQuestionAnchor");
    if (place.length != 0) {
        var form = $("#supportQuestionForm").appendTo(place).show().find("form");
        form.find("button").click(function () {
            bg.ui.lock(null, 'Отправка вашего сообщения. Пожалуйста, подождите...');
            var serializeObject = form.serializeObject();
            $.post("/assistance/question.ajax", JSON.stringify(serializeObject))
                    .done(function (response) {
                        if (response.success) {
                            form.find("input[type=text], textarea").val("");
                            bg.ui.unlock(null, "Сообщение успешно отправлено. Вашему вопросу присвоен номер: " + response.data, false);
                        } else {
                            bg.ui.unlock(null, response.message, true);
                        }
                    })
                    .fail(function (jqXHR, textStatus, errorThrown) {
                        bg.ui.unlock(null, "По техническим причинам сообщение не может быть отправлено в данный момент. " +
                                "Пожалуйста, попробуйте отправить сообщение позже.", true);
                    });
        });
    }
};


bg.warehouse = {};

bg.warehouse.Filter = function (minTotalPrice, maxTotalPrice, minSelectedPrice, maxSelectedPrice, params) {
    var resolution = 10;

    var form = $('#productsFilterForm');
    var priceSlide = $("#priceSlide");

    var minPriceInput = $("#minPriceFilter");
    var maxPriceInput = $("#maxPriceFilter");

    function applyFilter() {
        var inputs = form.find('input');
        if (minPriceInput.val() == minTotalPrice) {
            inputs = inputs.not(minPriceInput);
        }
        if (maxPriceInput.val() == maxTotalPrice) {
            inputs = inputs.not(maxPriceInput);
        }
        var filterParams = encodeURIComponent(inputs.serialize());
        bg.util.url.redirect(bg.util.url.extend("?" + params, 'filter', filterParams, true));
    }

    function resetFilter(el) {
        el.find('input[type=checkbox]').attr('checked', false);
        el.find('input[type=radio].default').attr('checked', true);
        applyFilter();
    }

    form.find('input').change(applyFilter);

    form.find('.reset').click(function () {
        resetFilter($(this).closest('.property'));
    });

    form.find('.fulllist').click(function () {
        var others = $(this).parent().find('.others');
        if (others.is(':hidden')) {
            $(this).text('Только популярные');
            others.slideDown('fast');
        } else {
            $(this).text('Показать еще');
            others.slideUp('fast');
        }
    });

    form.find("#resetFilterButton").click(function () {
        resetFilter(form);
    });

    priceSlide.slider({
        range: true,
        min: minTotalPrice,
        max: maxTotalPrice,
        step: resolution,
        values: [minSelectedPrice, maxSelectedPrice],
        slide: function (event, ui) {
            var min = ui.values[0];
            var max = ui.values[1];
            minPriceInput.val(min).attr('exclude', min == minTotalPrice);
            maxPriceInput.val(max).attr('exclude', max == maxTotalPrice);
        },
        change: applyFilter
    });
};

bg.warehouse.Basket = function () {
    var basket = $(".basket");

    var updatePrice = function (el, price) {
        el.find('.amount').text(price.toFixed(2));
    };

    var showChangedWarning = function () {
        basket.find(".changeWarning").show('slow');
    };

    var recalculateTotal = function () {
        var totalAmount = 0;
        var totalWeight = 0;

        var discountAmount = 0;
        basket.find('.cnt tr.item').each(function (i, el) {
            var row = $(el);
            var quantity = row.find('[name="itemQuantities"]').val();

            totalAmount += quantity * row.find('[name="itemAmounts"]').val();
            totalWeight += quantity * row.find('[name="itemWeights"]').val();
            discountAmount += quantity * row.find('[name="itemDiscounts"]').val();
        });

        var shipmentItem = basket.find('[name="shipment"]:checked');
        var shipmentType = shipmentItem.val();

        var shipmentAmount = 0;
        if (totalAmount < 1000) {
            basket.find('.unregistered').slideDown('fast');
            if (shipmentType == 'REGISTERED') {
                shipmentAmount = 70.;
            }
            basket.find('#shipmentFree').removeAttr('disabled');
            basket.find('#freeRegisteredShipment').hide();
            basket.find('#paidRegisteredShipment').show();
        } else {
            basket.find('.unregistered').slideUp('fast');
            basket.find('#shipmentRegistered').prop('checked', true);
            basket.find('#shipmentFree').attr('disabled', 'disabled');
            basket.find('#freeRegisteredShipment').show();
            basket.find('#paidRegisteredShipment').hide();
        }

        updatePrice(basket.find('.unregistered .price'), 1000 - totalAmount);

        updatePrice(basket.find('.payment-order .price'), totalAmount);
        updatePrice(basket.find('.payment-shipment .price'), shipmentAmount);
        updatePrice(basket.find('.payment-discount .price'), discountAmount);
        updatePrice(basket.find('.payment-total .price'), totalAmount + shipmentAmount - discountAmount);
    };

    basket.find('[name="shipment"]').change(function () {
        recalculateTotal();
    });

    basket.find(".q_input").change(function () {
        var row = $(this).closest("tr");

        var amount = row.find('[name="itemAmounts"]').val();
        var weight = row.find('[name="itemWeights"]').val();
        var quantity = row.find('[name="itemQuantities"]').val();

        row.find(".itemWeight").text((weight * quantity).toFixed(2) + " кг");
        updatePrice(row.find(".itemAmount"), amount * quantity);

        recalculateTotal();
        showChangedWarning();
    });

    basket.find(".removeItem").click(function () {
        $(this).closest("tr").detach();
        recalculateTotal();
        showChangedWarning();
    });

    basket.find("#saveChanges").click(function () {
        basket.find("form").append($("<input name='action' value='update' type='hidden'/>")).submit();
    });

    basket.find("#revertChanges").click(function () {
        basket.find("form").append($("<input name='action' value='rollback' type='hidden'/>")).submit();
    });
};

bg.warehouse.Order = function () {
    this.changeTracking = function (order, email, tracking, successor) {
        bg.ui.lock(null, 'Изменение подписки. Пожалуйста, подождите...');
        $.post("/warehouse/order/tracking.ajax", JSON.stringify({"order": order, "email": email, "enable": tracking}))
                .done(function (response) {
                    if (response.success) {
                        successor();
                        bg.ui.unlock(null, tracking ? "Вы успешно подписаны на обновления." : "Вы успешно отписаны от обновлений.", false);
                    } else {
                        bg.ui.unlock(null, response.message, true);
                    }
                })
                .fail(function (jqXHR, textStatus, errorThrown) {
                    bg.ui.unlock(null, "Подписка не может быть добавлена в связи с внутренней ошибкой. Если проблема " +
                            "не исчезла, пожалуйста, свяжитесь с нами.", true);
                });
    };

    this.confirmReceived = function (order, email, successor) {
        bg.ui.confirm("Вы уверены, что хотите закрыть заказ?", "При нажатии кнопки \"Да\", вы подтверждаете получение вами " +
                "заказа в полном объеме и в хорошем состоянии.", function (approved) {
            if (approved) {
                bg.ui.lock(null, 'Подтверждение заказа. Пожалуйста, подождите...');
                $.post("/warehouse/order/close.ajax", JSON.stringify({"order": order, "email": email}))
                        .done(function (response) {
                            if (response.success) {
                                successor(true);
                                bg.ui.unlock(null, "Спасибо за подтверждение о доставке.", false);
                            } else {
                                successor(false);
                                bg.ui.unlock(null, response.message, true);
                            }
                        })
                        .fail(function (jqXHR, textStatus, errorThrown) {
                            successor(false);
                            bg.ui.unlock(null, "В связи с внутренней ошибкой мы не смогли обработать ваш запрос. Если проблема " +
                                    "не исчезла, пожалуйста, свяжитесь с нами.", true);
                        });
            } else {
                successor(false);
            }
        });
    };
};

bg.warehouse.Maintain = new function () {
    this.editProduct = function (pid) {
        bg.util.url.redirect('/maintain/product?id=' + pid);
    };

    this.recommend = function (pid, recommend) {
        bg.ui.lock(null, 'Изменение рекомендации продукта...');
        $.post("/maintain/recommends/" + (recommend ? "add.ajax" : "remove.ajax") + "?id=" + pid)
                .done(function (response) {
                    if (response.success) {
                        bg.ui.unlock(null, recommend ? "Продукт добавлен в рекомендации" : "Продукт удален из рекомендаций", false);
                        bg.util.url.reload();
                    } else {
                        bg.ui.unlock(null, response.message, true);
                    }
                })
                .fail(function (jqXHR, textStatus, errorThrown) {
                    bg.ui.unlock(null, "Возникли системные проблемы. Состояние продукта не может быть обновлено в данный момент.", true);
                });
    };
};

bg.warehouse.ProductController = function () {
    var addToBasket = function (callback) {
        bg.ui.lock(null, 'Добавление в корзину. Пожалуйста, подождите...');
        var serializeObject = $("#shoppingForm").serializeObject();
        if (!$.isArray(serializeObject['optionIds'])) {
            serializeObject['optionIds'] = [serializeObject['optionIds']];
        }
        if (!$.isArray(serializeObject['optionValues'])) {
            serializeObject['optionValues'] = [serializeObject['optionValues']];
        }
        $.post("/warehouse/basket/add.ajax", JSON.stringify(serializeObject))
                .done(function (response) {
                    if (response.success) {
                        var bq = $("#basketQuantity");
                        var qi = $("#shoppingForm").find("[name='quantity']");
                        bq.text(parseInt(bq.text()) + parseInt(qi.val()));

                        bg.ui.unlock(null, "Товар добавлен в корзину", false);
                    } else {
                        bg.ui.unlock(null, "Товар не может быть добавлен в связи с внутренней ошибкой. Если проблема " +
                                "не исчезла, пожалуйста, свяжитесь с нами.", true);
                    }
                    if (callback != null && callback != undefined) {
                        callback(response.success);
                    }
                })
                .fail(function (jqXHR, textStatus, errorThrown) {
                    bg.ui.unlock(null, "Товар не может быть добавлен в связи с внутренней ошибкой. Если проблема " +
                            "не исчезла, пожалуйста, свяжитесь с нами.", true);
                });
    };

    $("#add").click(function (event) {
        addToBasket();

        event.preventDefault();
        return false;
    });

    $("#buy").click(function (event) {
        addToBasket(function () {
            bg.util.url.redirect("/warehouse/basket");
        });

        event.preventDefault();
        return false;
    });

    function selectThumb(img) {
        $(".thumb img").removeClass("selected");
        var src = img.addClass("selected").attr('src');
        previewImage.attr('src', src.replace('_T', '_M'));
    }

    var images = [];
    var previewImage = $(".preview img");

    $(".thumb img")
            .each(function (i, v) {
                var img = $(v);
                images[img.attr('page')] = img.attr('view');
            })
            .click(function () {
                selectThumb($(this));
            });

    $.fn.prettyPhoto({
        animation_speed: 'fast',
        social_tools: false,
        deeplinking: false,
        show_title: false,
        allow_resize: false,
        overlay_gallery: true,
        default_width: 600,
        default_height: 600
    });

    $(".product .preview").click(function () {
        $.prettyPhoto.open(images);
        $.prettyPhoto.changePage(parseInt($(".thumb img.selected").attr("page")));
    });

    var relatedScroll = $('.group-horizontal');
    relatedScroll.find('.sly-frame').sly({
        horizontal: 1,
        itemNav: 'basic',
        itemSelector: null,
        smart: 1,
        activateOn: null,
        mouseDragging: 1,
        touchDragging: 1,
        releaseSwing: 1,
        startAt: 0,
        scrollBy: 1,
        scrollBar: relatedScroll.find('.sly-scrollbar'),
        pagesBar: relatedScroll.find('.sly-pages'),
        activatePageOn: 'click',
        speed: 300,
        elasticBounds: 1,
        dragHandle: 1,
        dynamicHandle: 1,
        clickBar: 1
    });

    var accessoriesScroll = $('.group-vertical');
    accessoriesScroll.find('.sly-frame').sly({
        horizontal: 0,
        smart: 1,
        itemSelector: null,
        activatePageOn: 'click',
        speed: 300,
        pagesBar: accessoriesScroll.find('.sly-pages'),
        scrollBar: accessoriesScroll.find('.sly-scrollbar'),
        scrollBy: 100,
        dragHandle: 1,
        dynamicHandle: 1,
        clickBar: 1
    });

    this.showPriceProtection = function () {
        $('<div></div>').html("Мы заботимся о возможности предоставить нашим покупателям лучшие товары по наименьшим ценам. Если вы купили " +
                "продукт и в течение 48 часов обнаружили, что его стоимость снизилась, мы вернем вам разницу. " +
                "<br>Просто напищите нам письмо на наш адрес поддержки: <a href='mailto:support@billiongoods.ru'>support@billiongoods.ru</a>").dialog({
            title: "Ценовая защита 48 часов",
            draggable: false,
            modal: true,
            resizable: false,
            width: 400
        });
    };
};

bg.privacy = {};

bg.privacy.Tracking = function () {
    var processUIElements = function (trackingType) {
        if (trackingType == 'AVAILABILITY') {
            $(".availabilityTracking").toggle();
        } else {
            $(".descriptionTracking").toggle();
        }
    };

    this.remove = function (el, productId, trackingType) {
        var block = $(el).closest(".privacy .item");
        bg.ui.lock(block, 'Удаление подписки. Пожалуйста, подождите...');
        $.post("/privacy/tracking/remove.ajax", JSON.stringify({productId: productId, type: trackingType}))
                .done(function (response) {
                    if (response.success) {
                        bg.ui.unlock(block, "Подписка успешно удалена", false);
                        block.remove();
                        processUIElements(trackingType);
                    } else {
                        bg.ui.unlock(block, response.message, true);
                    }
                })
                .fail(function (jqXHR, textStatus, errorThrown) {
                    bg.ui.unlock(block, "По техническим причинам сообщение не может быть отправлено в данный момент. " +
                            "Пожалуйста, попробуйте отправить сообщение позже.", true);
                });
    };

    this.add = function (el, productId, trackingType, member) {
        var block = $(el);

        var letsDo = function (lemail, callback) {
            bg.ui.lock(block, 'Отправки заявки. Пожалуйста, подождите...');
            $.post("/privacy/tracking/add.ajax", JSON.stringify({productId: productId, type: trackingType, email: lemail}))
                    .done(function (response) {
                        if (response.success) {
                            bg.ui.unlock(block, "Ваша заявка успешно отправлена", false);
                            if (member) {
                                processUIElements(trackingType);
                            }
                            callback(true);
                        } else {
                            bg.ui.unlock(block, response.message, true);
                            callback(false);
                        }
                    })
                    .fail(function (jqXHR, textStatus, errorThrown) {
                        bg.ui.unlock(block, "Подписка не может быть добавлена в связи с внутренней ошибкой. Если проблема " +
                                "не исчезла, пожалуйста, свяжитесь с нами.", true);
                        callback(false);
                    });
        };

        if (member) {
            letsDo(null, function () {
            });
        } else {
            $('#trackingEmailForm').dialog({
                title: 'Запрос на описание товара',
                draggable: true,
                modal: true,
                resizable: false,
                width: 500,
                buttons: [
                    {
                        text: 'Отправить заявку',
                        click: function () {
                            var d = $(this);
                            letsDo($('#subscribeDescriptionEmail').val(), function (r) {
                                if (r) {
                                    d.dialog("close");
                                }
                            });
                        }
                    },
                    {
                        text: 'Отмена',
                        click: function () {
                            $(this).dialog("close");
                        }
                    }
                ]
            });
        }
    };
};
/*
 $("#requestProductDescription").click(function (event) {
 var letsDo = function (callback) {
 bg.ui.lock(null, 'Отправки заявки. Пожалуйста, подождите...');
 var serializeObject = $('#subscribeDescriptionForm').find('form').serializeObject();
 $.post("/privacy/tracking/add.ajax", JSON.stringify(serializeObject))
 .done(function (response) {
 if (response.success) {
 bg.ui.unlock(null, "Ваша заявка на добавление описание успешно отправлена", false);
 } else {
 bg.ui.unlock(null, response.message, true);
 }
 })
 .fail(function (jqXHR, textStatus, errorThrown) {
 bg.ui.unlock(null, "Подписка не может быть изменения в связи с внутренней ошибкой. Если проблема " +
 "не исчезла, пожалуйста, свяжитесь с нами.", true);
 });
 };

 */


bg.privacy.AddressBook = function () {
    var form = $("#addressForm");
    var fields = ["firstName", "lastName", "postcode", "region", "city", "location"];

    this.registerNew = function () {
        clearErrors();
        clearAddressForm();
        showAddressDialog('create');
    };

    this.editAddress = function (id) {
        fillAddressForm(id);
        showAddressDialog('update');
    };

    this.removeAddress = function (id) {
        fillAddressForm(id);
        bg.ui.confirm("Удаление адреса", "Вы уверены, что хотите удалить данный адрес?", function (approved) {
            if (approved) {
                fillAddressForm(id);
                submitAddressForm('remove', function (ignore) {
                });
            }
        });
    };

    var clearErrors = function () {
        form.find(".error-msg").remove();
    };

    var clearAddressForm = function () {
        form.find("#id").val("");
        $.each(fields, function (i, v) {
            form.find("#" + v).val("");
        });
    };

    var fillAddressForm = function (id) {
        var tr = $("#addressRecord" + id);

        form.find("#id").val(id);
        $.each(fields, function (i, v) {
            form.find("#" + v).val(tr.find("." + v).text());
        });
    };

    var submitAddressForm = function (action, callback) {
        clearErrors();
        var serializeObject = form.serializeObject();
        bg.ui.lock(null, 'Изменение адреса. Пожалуйста, подождите...');
        $.post("/privacy/address/" + action + ".ajax", JSON.stringify(serializeObject))
                .done(function (response) {
                    if (response.success) {
                        bg.ui.unlock(null, "Адрес успешно изменен", false);
                        bg.util.url.reload();
                    } else {
                        if (response.data != null) {
                            $.each(response.data, function (key, v) {
                                form.find("#" + key).closest("td").append("<div class=\"ui-state-error-text error-msg\">" + v + "</div>");
                            });
                            bg.ui.unlock(null, null, true);
                        } else {
                            bg.ui.unlock(null, response.message, true);
                        }
                    }
                    callback(response.success);
                })
                .fail(function (jqXHR, textStatus, errorThrown) {
                    bg.ui.unlock(null, "По техническим причинам сообщение не может быть отправлено в данный момент. " +
                            "Пожалуйста, попробуйте отправить сообщение позже.", true);
                    callback(false);
                });
    };

    var showAddressDialog = function (action) {
        form.dialog({
            title: 'Редактирование адреса',
            draggable: true,
            modal: true,
            resizable: false,
            width: 500,
            buttons: [
                {
                    text: 'Сохранить',
                    click: function () {
                        submitAddressForm(action, function (close) {
                            if (close) {
                                $(this).dialog("close");
                            }
                        });
                    }
                },
                {
                    text: 'Отменить',
                    click: function () {
                        $(this).dialog("close");
                    }
                }
            ]
        });
    }
};

$(document).ready(function () {
    jQuery.fn.extend({
        serializeObject: function () {
            var arrayData, objectData;
            arrayData = this.serializeArray();
            objectData = {};

            $.each(arrayData, function () {
                var value;

                if (this.value != null) {
                    value = this.value;
                } else {
                    value = '';
                }

                if (objectData[this.name] != null) {
                    if (!objectData[this.name].push) {
                        objectData[this.name] = [objectData[this.name]];
                    }

                    objectData[this.name].push(value);
                } else {
                    objectData[this.name] = value;
                }
            });

            return objectData;
        }
    });

    $('[title]').cluetip({ showTitle: false, activation: 'hover', local: true});

    $(".quickInfo").addClass('ui-state-default').hover(
            function () {
                if (!$(this).hasClass('ui-state-active')) {
                    $(this).attr('class', 'quickInfo ui-state-hover');
                }
            },
            function () {
                if (!$(this).hasClass('ui-state-active')) {
                    $(this).attr('class', 'quickInfo ui-state-default');
                }
            });

    var activeQuickInfo = undefined;
    $(".quickInfo.ajax a").cluetip({
        width: 340,
        showTitle: false,
        ajaxCache: true,
        activation: 'click',
        closePosition: 'bottom',
        closeText: '???????',
        arrows: false,
        sticky: true,
        ajaxProcess: function (response) {
            if (response.success) {
                return response.data;
            }
            return null;
        },
        ajaxSettings: {
            type: 'post',
            dataType: 'json',
            contentType: 'application/json'
        },
        onActivate: function (e) {
            var element = $(this);
            if (activeQuickInfo != undefined) {
                activeQuickInfo.parent().attr('class', 'quickInfo ui-state-default');
            }
            activeQuickInfo = element;
            element.parent().attr('class', 'quickInfo ui-state-active');
            return true;
        },
        onHide: function (ct, ci) {
            $(this).parent().attr('class', 'quickInfo ui-state-default');
            activeQuickInfo = undefined;
        }
    });

    $(".quickInfo.local a").cluetip({
        width: 340,
        local: true,
        showTitle: false,
        ajaxCache: true,
//        activation: 'click',
        arrows: false,
        sticky: false,
        ajaxSettings: {
            dataType: 'html'
        },
        onActivate: function (e) {
            var element = $(this);
            if (activeQuickInfo != undefined) {
                activeQuickInfo.parent().attr('class', 'quickInfo ui-state-default');
            }
            activeQuickInfo = element;
            element.parent().attr('class', 'quickInfo ui-state-active');
            return true;
        },
        onHide: function (ct, ci) {
            $(this).parent().attr('class', 'quickInfo ui-state-default');
            activeQuickInfo = undefined;
        }
    });

    $(".bg-ui-button").click(function (el) {
        var url = $(this).find("a").attr("href");
        if (url != undefined && url != "#") {
            bg.util.url.redirect(url);
        }
    });

    var timeoutID;

    function updateSearchFields() {
        var searchCatalog = $("#searchCatalog");
        var find = searchCatalog.find('option:selected');
        $("#searchCatId").val(find.val());
        $("#searchCatName").text(find.text().trim());

        $("#searchInputField").css('padding-left', searchCatalog.width()).css('padding-right', $("#searchAction").width());
    }

    $("#searchCatalog").change(updateSearchFields);
    updateSearchFields();

    $("#tableSorting").change(function () {
        bg.util.url.redirect($("#tableSorting").val());
    });

    $(".quantity").each(function (i, el) {
        el = $(el);

        var changeValue = function (v) {
            quantity.val(v);
            quantity.trigger('change');
        };

        var validateQuantityActions = function () {
            var v = quantity.val();
            if (v == 1) {
                down.attr("disabled", "disabled");
            } else {
                down.removeAttr("disabled");
            }
        };

        var quantity = el.find(".q_input").on('input', function () {
            var v = quantity.val();
            if (!$.isNumeric(v) || quantity < 1) {
                changeValue(1);
            }
            validateQuantityActions();
        });

        var up = el.find(".q_up").click(function (event) {
            var v = quantity.val();
            v++;
            changeValue(v);

            validateQuantityActions();

            event.preventDefault();
            return false;
        });

        var down = el.find(".q_down").click(function (event) {
            var v = quantity.val();
            if (v > 1) {
                v--;
                changeValue(v);
            }

            validateQuantityActions();

            event.preventDefault();
            return false;
        });

        validateQuantityActions();
    });

    $('.dropdown')
            .mouseenter(function () {
                var submenu = $('.sublinks').stop(false, true).hide();
                window.clearTimeout(timeoutID);

                submenu.css({
                    width: $(this).width() + 20 + 'px',
                    top: $(this).offset().top + $(this).height() + 7 + 'px',
                    left: $(this).offset().left + 'px'
                });

                submenu.stop().slideDown(300);

                submenu.mouseleave(function () {
                    $(this).slideUp(300);
                });

                submenu.mouseenter(function () {
                    window.clearTimeout(timeoutID);
                });

            })
            .mouseleave(function () {
                timeoutID = window.setTimeout(function () {
                    $('.sublinks').stop(false, true).slideUp(300);
                }, 250);
            });

    $(".catalog").find(".ct-item")
            .hover(function () {
                var $2 = $(this);
                $2.find(".ct-toc").addClass("hover");
                $2.find(".ct-list").show();
            }, function () {
                var $2 = $(this);
                $2.find(".ct-toc").removeClass("hover");
                $2.find(".ct-list").hide();
            })
            .click(function () {
                bg.util.url.redirect($(this).find("a").attr("href"));
            });

    var globalCatalog = $("#globalCatalog");
    $(".tb-item-catalog").hover(function () {
        globalCatalog.show();
    }, function () {
        globalCatalog.hide();
    });

    $(".social-signin-link").click(function (event) {
        event.preventDefault();
        bg.ui.popupwindow($(this).attr("href"), "BillionGoods: Авторизация", 500, 300).focus();
        return false;
    });
});