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
        return '<div style="padding: 10px 24px; padding-bottom: 10px">' + message + '</div><div class="closeButton"><a href="#"><div class="wm-icon-close"/></a></div>';
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

    this.message = function (element, message, error) {
        var v = {
            message: messageTemplate(null, message),
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
                            bg.ui.unlock(null, "Сообщение успешно отправлено", false);
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

bg.warehouse.Basket = function (cource) {
    var basket = $(".basket");

    var updatePrice = function (el, price) {
        el.find('.usd .v').text(price.toFixed(2));
        el.find('.rub .v').text((price * cource).toFixed(2));
    };

    var showChangedWarning = function () {
        basket.find(".changeWarning").show('slow');
    };

    var recalculateTotal = function () {
        var totalAmount = 0;
        var totalWeight = 0;

        basket.find('.cnt tr.item').each(function (i, el) {
            var row = $(el);
            var quantity = row.find('[name="itemQuantities"]').val();

            totalAmount += quantity * row.find('[name="itemAmounts"]').val();
            totalWeight += quantity * row.find('[name="itemWeights"]').val();
        });

        var shipmentItem = basket.find('[name="shipment"]:checked');
        var shipmentType = shipmentItem.val();

        var shipmentAmount = 0;
        if (totalAmount < 25) {
            basket.find('.unregistered').slideDown('fast');
            if (shipmentType == 'REGISTERED') {
                shipmentAmount = 1.70;
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

        updatePrice(basket.find('.unregistered .price'), 25 - totalAmount);

        updatePrice(basket.find('.payment-order .price'), totalAmount);
        updatePrice(basket.find('.payment-shipment .price'), shipmentAmount);
        updatePrice(basket.find('.payment-total .price'), totalAmount + shipmentAmount);
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
                    bg.ui.unlock(null, "Подписка не может быть изменения в связи с внутренней ошибкой. Если проблема " +
                            "не исчезла, пожалуйста, свяжитесь с нами.", true);
                });
    };
};

bg.warehouse.Controller = function () {
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

    var previewImage = $(".preview img");
    $(".thumb img").click(function () {
        $(".thumb img").removeClass("selected");
        var src = $(this).addClass("selected").attr('src');
        previewImage.attr('src', src.replace('_T', '_M'));
    });
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
        closeText: 'Закрыть',
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
        if (url != undefined) {
            bg.util.url.redirect(url);
        }
    });
});

$(document).ready(function () {
    var timeoutID;

    var applyTableFilters = function () {
        var url = null;
        url = bg.util.url.extend(url, 'page', $("#tableFormPage").val(), true);
        url = bg.util.url.extend(url, 'count', $("#tableFormCount").val(), true);
        url = bg.util.url.extend(url, 'sort', $("#tableSorting").val(), true);
        url = bg.util.url.extend(url, 'query', $("#tableQuery").val(), true);
        bg.util.url.redirect(url);
    };

    $("#tableSorting").change(applyTableFilters);
    $("#tableQueryButton").click(applyTableFilters);
    $("#tableQuery").keyup(function (event) {
        if (event.keyCode == 13) {
            applyTableFilters();
        }
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
});