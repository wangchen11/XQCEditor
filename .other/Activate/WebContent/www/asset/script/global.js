/* 全局变量 */
var BASE = '/smart-sample'; // Context Path（若以 ROOT 发布，则为空字符串）

var Pager = function(pagerId, $tableComponent) {
    (function() {
        // 翻页
        $(document).on('click', '#' + pagerId + ' .ext-pager-button button', function() {
            var pageNumber = $(this).data('pn');
            $tableComponent.load(pageNumber);
        });

        // 更改当前页号
        var pageNumberInput = '#' + pagerId + ' .ext-pager-pn';
        $(document)
            .on('click', pageNumberInput, function() {
                $(this).select();
            })
            .on('keydown', pageNumberInput, function(event) {
                if (event.keyCode == '13') {
                    var pageNumber = $(this).val();
                    var totalPage = parseInt($('#' + pagerId + ' .ext-pager-tp').text());
                    if (isNaN(pageNumber) || pageNumber <= 0 || pageNumber > totalPage) {
                        alert('Input error for page number!');
                        $(this).select();
                        return;
                    }
                    $tableComponent.load(pageNumber);
                }
            });

        // 更改每页条数
        var pageSizeInput = '#' + pagerId + ' .ext-pager-ps';
        $(document)
            .on('click', pageSizeInput, function() {
                $(this).select();
            })
            .on('keydown', pageSizeInput, function(event) {
                if (event.keyCode == '13') {
                    var pageSize = $(this).val();
                    if (isNaN(pageSize) || pageSize <= 0) {
                        alert('Input error for page size!');
                        $(this).select();
                        return;
                    }
                    $tableComponent.load(1);
                }
            });
    })();

    // 渲染
    this.render = function(data) {
        var pageNumber = data.pageNumber;
        var totalPage = data.totalPage;
        var pageSize = data.pageSize;
        var totalRecord = data.totalRecord;

        var pagerHTML = '';
        pagerHTML += '<span>Page: </span>';
        pagerHTML += '<input type="text" value="' + pageNumber + '"class="css-width-25 css-text-center ext-pager-pn"/>';
        pagerHTML += '<span> / </span>';
        pagerHTML += '<span class="ext-pager-tp">' + totalPage + '</span>';
        pagerHTML += '<span class="css-blank-10"></span>';
        pagerHTML += '<span>Size: </span>';
        pagerHTML += '<input type="text" value="' + pageSize + '"class="css-width-25 css-text-center ext-pager-ps"/>';
        pagerHTML += '<span class="css-blank-10"></span>';
        pagerHTML += '<span>Total: </span>';
        pagerHTML += '<span id="total_record">' + totalRecord + '</span>';
        pagerHTML += '<span class="css-blank-10"></span>';
        pagerHTML += '<div class="css-button-group ext-pager-button">';
        if (pageNumber > 1 && pageNumber <= totalPage) {
            pagerHTML += '    <button type="button" title="First" data-pn="1">|&lt;</button>';
            pagerHTML += '    <button type="button" title="Pre" data-pn="' + (pageNumber - 1) + '">&lt;</button>';
        } else {
            pagerHTML += '    <button type="button" title="First" disabled>|&lt;</button>';
            pagerHTML += '    <button type="button" title="Pre" disabled>&lt;</button>';
        }
        if (pageNumber < totalPage) {
            pagerHTML += '    <button type="button" title="Next" data-pn="' + (pageNumber + 1) + '">&gt;</button>';
            pagerHTML += '    <button type="button" title="Last" data-pn="' + totalPage + '">&gt;|</button>';
        } else {
            pagerHTML += '    <button type="button" title="Next" disabled>&gt;</button>';
            pagerHTML += '    <button type="button" title="Last" disabled>&gt;|</button>';
        }
        pagerHTML += '</div>';

        $('#' + pagerId).html(pagerHTML);
    };
};

var Renderer = function() {
    this.render = function(template, data) {
        return template.replace(/\{([\w\.]*)\}/g, function(str, key) {
            var keys = key.split('.');
            var value = data[keys.shift()];
            for (var i = 0, l = keys.length; i < l; i++) {
                value = value[keys[i]];
            }
            return (typeof value !== 'undefined' && value !== null) ? value : '';
        });
    };
};

var Validator = function() {
    this.required = function(formId) {
        var result = true;
        $('#' + formId + ' .ext-required')
            .each(function() {
                var value = $.trim($(this).val());
                var tagName = this.tagName;
                if (tagName == 'INPUT' || tagName == 'TEXTAREA') {
                    if (value == '') {
                        $(this).addClass('css-error');
                        result = false;
                    } else {
                        $(this).removeClass('css-error');
                    }
                } else if (tagName == 'SELECT') {
                    if (value == '' || value == 0) {
                        $(this).addClass('css-error');
                        result = false;
                    } else {
                        $(this).removeClass('css-error');
                    }
                }
            })
            .change(function() {
                var value = $.trim($(this).val());
                var tagName = this.tagName;
                if (tagName == 'INPUT' || tagName == 'TEXTAREA') {
                    if (value != '') {
                        $(this).removeClass('css-error');
                    } else {
                        $(this).addClass('css-error');
                    }
                } else if (tagName == 'SELECT') {
                    if (value != '' && value != 0) {
                        $(this).removeClass('css-error');
                    } else {
                        $(this).addClass('css-error');
                    }
                }
            });
        return result;
    }
};

$(function() {
    // 忽略空链接
    $('a[href="#"]').click(function() {
        return false;
    });

    // 全局 AJAX 设置
    $.ajaxSetup({
        cache: false,
        error: function(jqXHR, textStatus, errorThrown) {
            switch (jqXHR.status) {
                case 403:
                    document.write('');
                    location.href = BASE + '/';
                    break;
                case 503:
                    alert(errorThrown);
                    break;
            }
        }
    });

    // 绑定注销事件
    $('#logout').click(function() {
        if (confirm('Do you want to logout system?')) {
            $.ajax({
                type: 'get',
                url: BASE + '/logout',
                success: function(result) {
                    if (result.success) {
                        location.href = BASE + '/';
                    }
                }
            });
        }
    });
});