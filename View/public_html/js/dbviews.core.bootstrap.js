/**
 *  dbviews.core.js
 *  @requires jQueryUI v1.8 or above
 *
 *  Copyright (c) Alejandro Profet Beruff
 *  Version 1.0 : 22-Apr-2013
 */
 
$(document).ready(function() {
  // TODO:
  dlg = {
    alert: bootbox.alert,
    info: bootbox.alert
  }
});

$.ajaxSetup({
  cache: false
});

function getItemContainer(item) {
  return '#item-' + item.type + '-' + item.id;
}

function clearContainer(container) {
  var $container = $(container);
  $container.find('input.filter').each(function() {
    var drp = $(this).data('daterangepicker');
    if (drp && drp.container)
      drp.container.remove();
  });
  return $container.empty();
}

function buildModal(container) {
  var $modal = $('<div/>').addClass('loading-modal').attr('title', msg['loading_please_wait']);
  $(container).append($modal);
  return $modal;
}

function buildView(view, container, replaceContent) {
  document.title = msg['title'] + ' - ' + view.description;
  var $view = $('<div/>').attr('id', 'view').css({
    "margin": '1em',
    "border": '1px #ccc solid',
    "padding": '.2em',
    "border-radius": '.25rem'
  });

  if ($.type($view[view.jquiPlugin]) !== 'function') {
//    dlg.alert(msg['jqui_plugin_not_found']);
//    $('.loading').removeClass('loading');
//    return null;
  }
  var options;
  try {
    options = $.parseJSON(view.jquiPluginOptions);
  }
  catch (err) {
    dlg.alert(msg['jqui_plugin_parsing_error'] + '<br>' + err);
    $('.loading').removeClass('loading');
    return null;
  }

  var $container = $(container);
  if (replaceContent)
    clearContainer($container);
  $container.append($view);
  var $itemHeader;
  if (view.jquiPlugin == 'tabs') {
    $itemHeader = $('<ul>').addClass('nav nav-tabs');
    $view.append($itemHeader);
  }
  var $tabContent = $('<div class="tab-content">').appendTo($view);
  var loadContent = true;
  var active = true;
  for (var i in view.items) {
    var item = view.items[i];
    if (view.jquiPlugin == 'tabs') {
      var $li = $('<li>').addClass('nav-item').appendTo($itemHeader);
      var $a = $('<a class="nav-link" data-toggle="tab" role="tab" aria-selected="true">').attr({
        "id": 'tab-' + item.type + '-' + item.id,
        "href": getItemContainer(item),
        "aria-controls": getItemContainer(item),
        "title": item.description
      })
      .addClass(active ? 'active show' : '')
      .css({
        'padding': '.3em 1em',
        'color': loadContent ? '' : '#aaa'
      })
      .append(
        $('<span/>')
          .addClass('fas ' + (item.type == 'table' ? 'fa-th' : item.type == 'graph' ? 'fa-chart-pie' : 'fa-code'))
          .css('display', 'inline-block')
      )
      .append(" " + item.label)
      .appendTo($li)
      .data('item', item);
      if (view.lazyLoad === true) {
        $a.on('show.bs.tab', function (e) {
          var $tab = $(e.target);
          loadItem($tab.data('item'), $tab.attr('href'));
        });
      }
    }
    else if (view.jquiPlugin == 'accordion') {
      $('<h3/>')
        .attr('title', item.description)
        .append(
          $('<span/>')
            .addClass('ui-icon ' + (item.type == 'table' ? 'ui-icon-calculator' : item.type == 'graph' ? 'ui-icon-image' : 'ui-icon-carat-2-e-w'))
            .css('display', 'inline-block')
        )
        .append(item.label)
        .css({
          'color': loadContent ? '' : '#aaa'
        })
        .data({
          'item': item
        }).appendTo($view);
    }
    else if (view.jquiPlugin == 'dashboard') {
      //TODO
    }

    buildItem(item, $tabContent, false, loadContent, active);
    if (view.lazyLoad)
      loadContent = false;
    active = false;
  }

  if (view.lazyLoad === true) {
    if (view.jquiPlugin == 'tabs') {
      options = $.extend({}, options, {
        beforeActivate: function(e, ui) {
          var $tab = ui.newTab;
          loadItem($tab.data('item'), $tab.children('a[href]').first().attr('href'));
        },
        beforeLoad: function(event, ui) {
          ui.jqXHR.fail(function() {
            dlg.alert('An error occurred when trying to load the content');
            $('.loading').removeClass('loading');
          });
        }
      });
    } else if (view.jquiPlugin == 'accordion') {
      options = $.extend({}, options, {
        heightStyle: 'content',
        beforeActivate: function(e, ui) {
          var $tab = ui.newHeader;
          loadItem($tab.data('item'), '#' + $tab.attr('aria-controls'));
        }
      });
    } else if (view.jquiPlugin == 'dashboard') {
      //TODO
    }
  }

  //$view[view.jquiPlugin](options);
  return $view;
}

function buildItem(item, container, replaceContent, loadContent, active) {
  if (replaceContent)
    document.title = msg['title'] + ' - ' + item.description;
  var $item = $('<div class="tab-pane show" role="tabpanel">')
  .attr({
    "id": 'item-' + item.type + '-' + item.id,
    "aria-labelledby": 'tab-' + item.type + '-' + item.id,
    "data-label": item.label,
    "data-title": item.description
  })
  .css({
    "text-align": 'center',
    "position": 'relative',
    "padding": '1em 1.4em'
  })
  .addClass('panel-content')
  .addClass('panel-content');
  if (active)
    $item.addClass('active show');
  var $container = $(container);
  if (replaceContent)
    clearContainer($container);
  $container.append($item);
  if (loadContent && !loadItem(item, $item))
    return false;
  $item.append(buildModal());
  return $item;
}

function loadItem(item, container) {
  var $container = $(container);
  if ($container.data('loaded') === true)
    return true;

  if (!item.rows) {
    $container.css('min-height', '100px').addClass('loading');
    $.get('/dbviews-api/user/' + item.type + '/' + item.id, {
      args: JSON.stringify(item.args),
      filter: JSON.stringify(item.filter),
      options: JSON.stringify(item.options),
      countRows: item.countRows,
      offsetRow: item.offsetRow,
      sortby: JSON.stringify(item.sortby)
    }, function(newItem) {
      var $item = clearContainer(getItemContainer(newItem));
      buildItemContent(newItem, $item);
      $item.append(buildModal()).removeClass('loading');
      $('#view a[href="#' + $item.attr('id') + '"], #view h3[aria-controls="' + $item.attr('id') + '"]').css('color', '');
      $item.data('loaded', true);
    }).error(function() {
      dlg.alert(msg['alert_error']);
      $(getItemContainer(item)).removeClass('loading');
    });
  } else {
    if (!buildItemContent(item, $container))
      return false;
    $container.data('loaded', true);
  }

  return true;
}

function buildItemContent(item, $container) {
  if (item.type == 'table') {
    buildTableElements(item, $container);
  }
  else if (item.type == 'graph') {
    buildGraph(item, $container);
  }
  else if (item.type == 'block') {
    buildBlock(item, $container);
  }
  else {
    dlg.alert('Unknown item type');
    $('.loading').removeClass('loading');
    return false;
  }
  return true;
}

function buildInfoTag(item, container) {
  var offsetRow = item.offsetRow;
  var countRows = item.countRows;
  var totalRows = item.totalRows;
  var totalPag = Math.floor(totalRows / countRows) + (totalRows % countRows == 0 ? 0 : 1);
  var currentPag = Math.floor((offsetRow - 1) / countRows) + 1;
  var $infoTag = $('<div/>').css('margin', '10px').addClass('bold text-info').attr('title', str4mat(msg['query_delay'], { query_delay: item.queryDelay })).html(str4mat(msg['page_info'], { current_pag: currentPag, total_pag: Math.max(totalPag, 1), total_rows: totalRows, s: totalRows > 1 ? 's' : '' }))
  $(container).append($infoTag);
  return $infoTag;
}

function buildTableElements(item, container) {
  var toolbarPosition = item.toolbarPosition ? item.toolbarPosition.split(/\s*,\s*/) : [];
  var topToolbar = false;
  if ($.inArray('top', toolbarPosition) != -1) {
    buildToolbar(item, container);
    topToolbar = true;
  }
  buildInfoTag(item, container);
  buildTable(item, container);
  buildInfoTag(item, container);
  if (!topToolbar || $.inArray('bottom', toolbarPosition) != -1)
    buildToolbar(item, container);
}

function buildTable(item, container) {
  var $table = $('<table>').css({
    "margin": '0',
    "border-collapse": "inherit"
  }).addClass('table table-sm table-striped table-hover table-xtra-condensed small');
  var $tableContainer = $('<div/>').css('overflow', 'auto').addClass('table-responsive').append($table);
  $(container).append($tableContainer);
  var $tr = $('<tr/>');
  var $nRow = $('<th/>')
  .text('#')
  .css({
    "text-align": 'left',
    "vertical-align": 'bottom',
    "border": "1px solid #d3d3d3",
    "background-color": "#e6e6e6",
    "font-weight": "400",
    "color": "#555"
  })
  .attr({
    "width": '5%', //(item.totalRows.toString().length * 7) + 'px',
    "align": 'left',
    "valign": item.filterPosition == 'top' ? 'bottom' : 'top',
    "rowspan": item.filterPosition == 'top' || item.filterPosition == 'bottom' ? 2 : 1,
    "scope": 'col'
  });
  var $filter = $('<tr/>');
  if (item.filterPosition == 'top')
    $filter.append($nRow);
  else
    $tr.append($nRow);
  for (var v in item.headers) {
    th = item.headers[v];
    var sortbyKey = th.id;
    var dir = item.sortby[th.id];
    if (typeof dir === 'undefined') {
      sortbyKey = th.columnName;
      dir = item.sortby[sortbyKey];
    }
    var asc = (/^asc$/i).test(dir);
    var desc = (/^desc$/i).test(dir);
    var sortby = null;
    if (th.sortable && !desc) {
      sortby = {};
      sortby[th.id] = asc ? 'Desc' : 'Asc';
    }
    var $headerTh = $('<th/>').appendTo($tr)
    .css({
      "text-align": th.align,
      "border": "1px solid #d3d3d3",
      "background-color": "#e6e6e6",
      "font-weight": "400",
      "color": "#555"
    })
    .attr({
      "width": th.width,
      "align": th.align,
      "valign": th.valign,
      "scope": 'col'
    }).data({
      'item': item,
      'sortby': sortby
    });
    var $headerThDiv = $('<div/>').text(th.columnName).appendTo($headerTh);
    if (th.sortable) {
      var dirIcon = asc ? 'fa-sort-up' : desc ? 'fa-sort-down' : 'fa-sort';
      $headerThDiv.addClass('sort-wrapper').append(dirIcon && $('<span/>').addClass('sort-icon fas ' + dirIcon))
      $headerTh
      .addClass('sortable')
      .click(function() {
        $(getItemContainer(item)).addClass('loading');
        item = $(this).data('item');
        sortby = $(this).data('sortby');
        $.get('/dbviews-api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow, sortby: JSON.stringify(sortby) }, function(newItem) {
          var $item = clearContainer(getItemContainer(item));
          buildTableElements(newItem, $item);
          $item.append(buildModal()).removeClass('loading');
        }).error(function() {
          dlg.alert(msg['alert_error']);
          $('.loading').removeClass('loading');
        });
      });
    }
    var $filterTd = $('<th>')
    .css({
      "background-color": "#fff",
      "border": "none"
    }).appendTo($filter);
    if (th.filterable) {
      var $input = $('<input>')
      .addClass("filter")
      .val(item.filter[th.id]).attr({
        'placeholder': msg['filter'],
        'id': 'filter-' + item.type + '-' + item.id + '-' + th.id,
        'colId': th.id
      }).data({
        'item': item,
        'th': th,
        '$tr': $filter
      }).keypress(function(e) {
        var code = (e.keyCode ? e.keyCode : e.which);
        if (code != 13)
          return;
        $(getItemContainer(item)).addClass('loading');
        item = $(this).data('item');
        th = $(this).data('th');
        $(this).data('$tr').find('input').each(function() {
          item.filter[$(this).attr('colId')] = [ $(this).val() ];
        });
        $.get('/dbviews-api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow, sortby: JSON.stringify(item.sortby), focuson: th.id }, function(newItem) {
          var $item = clearContainer(getItemContainer(item));
          buildTableElements(newItem, $item);
          $item.append(buildModal()).removeClass('loading');
          var sft = $('#filter-' + newItem.type + '-' + newItem.id + '-' + newItem.focuson).get(0);
          sft.focus();
          sft.select();
        }).error(function() {
          dlg.alert(msg['alert_error']);
          $('.loading').removeClass('loading');
        });
        return false;
      });
      if (th.type == 93) {
        $input.daterangepicker({
          "autoUpdateInput": false,
          "showDropdowns": true,
          "showISOWeekNumbers": true,
          //"timePicker": true,
          "ranges": {
            "CLEAR [x]": [null, null],
            "Today": [moment(), moment()],
            "Yesterday": [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            "Last 7 Days": [moment().subtract(6, 'days'), moment()],
            "Last 30 Days": [moment().subtract(29, 'days'), moment()],
            "This Month": [moment().startOf('month'), moment().endOf('month')],
            "Last Month": [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
          },
          "locale": {
            //"direction": "ltr",
            "format": "MM/DD/YYYY",
            "separator": " - ",
            "applyLabel": "Apply",
            "cancelLabel": "Cancel",
            "customRangeLabel": "Custom Range",
            //"daysOfWeek": ['<%=StringUtils.join(BaseResource.getViewString(locale, "daterangepicker.locale.daysOfWeek").split("\\s*,\\s*"), "','")%>'],
            "firstDay": 1
          },
          "linkedCalendars": false
          //,"alwaysShowCalendars": true
        }).on('apply.daterangepicker', function(ev, picker) {
          $(getItemContainer(item)).addClass('loading');
          if (picker.startDate.isValid() && picker.endDate.isValid()) {
            picker.element.val(picker.startDate.format(picker.locale.format) + picker.locale.separator + picker.endDate.format(picker.locale.format));
          } else {
            picker.element.val('');
          }
          var $drp = $(this);
          item = $drp.data('item');
          th = $drp.data('th');
          $drp.data('$tr').find('input').each(function() {
            var $inpt = $(this);
            item.filter[$inpt.attr('colId')] = [ $inpt.val() ];
          });
          $.get('/dbviews-api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow, sortby: JSON.stringify(item.sortby), focuson: th.id }, function(newItem) {
            var $item = clearContainer(getItemContainer(item));
            buildTableElements(newItem, $item);
            $item.append(buildModal()).removeClass('loading');
            var sft = $('#filter-' + newItem.type + '-' + newItem.id + '-' + newItem.focuson).get(0);
            sft.focus();
            sft.select();
          }).error(function() {
            dlg.alert(msg['alert_error']);
            $('.loading').removeClass('loading');
          });
        }).on('show.daterangepicker', function(ev, picker) {
          if (!picker.element.val())
            picker.container.find('.ranges li.active').removeClass('active');
          picker.container.find('.ranges li[data-range-key^="CLEAR"]').css({
            "font-family": 'monospace',
            "font-size": 'x-small',
            "font-weight": 'bold',
            "background-color": '#fafafa',
            "text-align": 'right'
          });
        });
      }
      $filterTd.append($input);
    }
  }
  if (item.filterPosition == 'top') {
    $table.append($filter);
    $table.append($tr);
  }
  else if (item.filterPosition == 'bottom') {
    $table.append($tr);
    $table.append($filter);
  }
  else {
    $table.append($tr);
  }
  if (item.rows) {
    for (var j = 0; j < item.rows.length; j++) {
      var cells = item.rows[j];
      $tr = $('<tr/>');
      $tr.append($('<td/>').html(item.offsetRow + j)).attr({
        align: 'left',
        valign: 'top'
      });
      for (var v in item.headers) {
        th = item.headers[v];
        var value = cells[th.id];
        $tr.append($('<td/>').addClass(item.sortby[th.id] || item.sortby[th.columnName] ? 'sorted' : '').text(value === null ? '' : value)).attr({
          align: th.align,
          valign: th.valign
        });
      }
      $table.append($tr);
    }
  }
  return $tableContainer;
}

function buildToolbar(item, container) {
  var offsetRow = item.offsetRow;
  var countRows = item.countRows;
  var totalRows = item.totalRows;
  var totalPag = Math.floor(totalRows / countRows) + (totalRows % countRows == 0 ? 0 : 1);
  var currentPag = Math.floor((offsetRow - 1) / countRows) + 1;
  var $toolbar = $('<ul>').css('margin', '0').addClass('pagination justify-content-center');
  $(container).append($toolbar);
  if (item.type == 'table') {
    $toolbar.append(
      $('<li>')
      .addClass('page-item' + (offsetRow == 1 ? ' disabled' : ''))
      .append(
        $('<a>')
        .addClass('page-link')
        .attr({
          "title": msg['first_page'],
          "href": 'javascript:void(0)'
        })
        .append('<i class="fas fa-fast-backward"><\/i>')
        .data({
          'item': item
        }).click(function() {
          $(getItemContainer(item)).addClass('loading');
          item = $(this).data('item');
          $.get('/dbviews-api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: 1, sortby: JSON.stringify(item.sortby) }, function(newItem) {
            var $item = clearContainer(getItemContainer(item));
            buildTableElements(newItem, $item);
            $item.append(buildModal()).removeClass('loading');
          }).error(function() {
            dlg.alert(msg['alert_error']);
            $(getItemContainer(item)).removeClass('loading');
          });
        })
      )
    );
    $toolbar.append(
      $('<li>')
      .addClass('page-item' + (offsetRow == 1 ? ' disabled' : ''))
      .append(
        $('<a>')
        .addClass('page-link')
        .attr({
          "title": msg['previous_page'],
          "href": 'javascript:void(0)'
        })
        .append('<i class="fas fa-backward"><\/i>')
        .data({
          'item': item
        }).click(function() {
          $(getItemContainer(item)).addClass('loading');
          item = $(this).data('item');
          $.get('/dbviews-api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow - item.countRows, sortby: JSON.stringify(item.sortby) }, function(newItem) {
            var $item = clearContainer(getItemContainer(item));
            buildTableElements(newItem, $item);
            $item.append(buildModal()).removeClass('loading');
          }).error(function() {
            dlg.alert(msg['alert_error']);
            $(getItemContainer(item)).removeClass('loading');
          });
        })
      )
    );
    for (var p = -4; p <= 4; p++) {
      var pagToShow = currentPag + p;
      if (pagToShow >= 1 && pagToShow <= totalPag) {
        var $pageLink = $(p == 0 ? '<span>' : '<a>')
        .addClass('page-link' + (p == 0 ? ' disabled' : ''))
        .attr({
          "title": p != 0 ? msg['go_to_page'] + ' ' + pagToShow : '',
          "href": 'javascript:void(0)'
        })
        .html(pagToShow)
        .data({
          'item': item,
          'pagToShow': pagToShow
        });
        if (p != 0) {
          $pageLink.click(function() {
            $(getItemContainer(item)).addClass('loading');
            item = $(this).data('item');
            sortby = $(this).data('sortby');
            pagToShow = $(this).data('pagToShow');
            $.get('/dbviews-api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: (pagToShow - 1) * item.countRows + 1, sortby: JSON.stringify(item.sortby) }, function(newItem) {
              var $item = clearContainer(getItemContainer(item));
              buildTableElements(newItem, $item);
              $item.append(buildModal()).removeClass('loading');
            }).error(function() {
              dlg.alert(msg['alert_error']);
              $(getItemContainer(item)).removeClass('loading');
            });
          })
        }
        $toolbar.append(
          $('<li>')
          .addClass('page-item' + (p == 0 ? ' active' : ''))
          .append($pageLink)
        );
      }
    }
    $toolbar.append(
      $('<li>')
      .addClass('page-item' + (offsetRow + countRows > totalRows ? ' disabled' : ''))
      .append(
        $('<a>')
        .addClass('page-link')
        .attr({
          "title": msg['next_page'],
          "href": 'javascript:void(0)'
        })
        .append('<i class="fas fa-forward"><\/i>')
        .data({
          'item': item
        }).click(function() {
          $(getItemContainer(item)).addClass('loading');
          item = $(this).data('item');
          sortby = $(this).data('sortby');
          $.get('/dbviews-api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow + item.countRows, sortby: JSON.stringify(item.sortby) }, function(newItem) {
            var $item = clearContainer(getItemContainer(item));
            buildTableElements(newItem, $item);
            $item.append(buildModal()).removeClass('loading');
          }).error(function() {
            dlg.alert(msg['alert_error']);
            $(getItemContainer(item)).removeClass('loading');
          });
        })
      )
    );
    $toolbar.append(
      $('<li>')
      .addClass('page-item' + (offsetRow + countRows > totalRows ? ' disabled' : ''))
      .append(
        $('<a>')
        .addClass('page-link')
        .attr({
          "title": msg['last_page'],
          "href": 'javascript:void(0)'
        })
        .append('<i class="fas fa-fast-forward"><\/i>')
        .data({
          'item': item
        }).click(function() {
          $(getItemContainer(item)).addClass('loading');
          item = $(this).data('item');
          $.get('/dbviews-api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: Math.floor((item.totalRows - 1) / item.countRows) * item.countRows + 1, sortby: JSON.stringify(item.sortby) }, function(newItem) {
            var $item = clearContainer(getItemContainer(item));
            buildTableElements(newItem, $item);
            $item.append(buildModal()).removeClass('loading');
          }).error(function() {
            dlg.alert(msg['alert_error']);
            $(getItemContainer(item)).removeClass('loading');
          });
        })
      )
    );
  }
  $toolbar
  .append(
    $('<li>')
    .addClass('page-item')
    .append(
      $('<a>')
      .addClass('page-link')
      .attr({
        "title": msg['refresh'],
        "href": 'javascript:void(0)'
      })
      .append('<i class="fas fa-sync-alt"><\/i>')
      .data({
        'item': item
      }).click(function() {
        $(getItemContainer(item)).addClass('loading');
        item = $(this).data('item');
        $.get('/dbviews-api/user/' + item.type + '/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow, sortby: JSON.stringify(item.sortby) }, function(newItem) {
          var $item = clearContainer(getItemContainer(item));
          if (item.type == 'table') {
            buildTableElements(newItem, $item);
          }
          else if (item.type == 'graph') {
            buildGraph(item, $item);
          }
          $item.append(buildModal()).removeClass('loading');
        }).error(function() {
          dlg.alert(msg['alert_error']);
          $(getItemContainer(item)).removeClass('loading');
        });
      })
    )
  )
  .append(
    $('<li>')
    .addClass('page-item')
    .append(
      $('<a>')
      .addClass('page-link')
      .attr({
        "title": msg['clear'],
        "href": 'javascript:void(0)'
      })
      .append('<i class="fas fa-ban"><\/i>')
      .data({
        'item': item
      }).click(function() {
        $(getItemContainer(item)).addClass('loading');
        item = $(this).data('item');
        $.get('/dbviews-api/user/' + item.type + '/' + item.id, { args: JSON.stringify(item.args), countRows: item.countRows, offsetRow: 1 }, function(newItem) {
          var $item = clearContainer(getItemContainer(item));
          if (item.type == 'table') {
            buildTableElements(newItem, $item);
          }
          else if (item.type == 'graph') {
            buildGraph(item, $item);
          }
          $item.append(buildModal()).removeClass('loading');
        }).error(function() {
          dlg.alert(msg['alert_error']);
          $(getItemContainer(item)).removeClass('loading');
        });
      })
    )
  )
  .append(
    $('<li>')
    .addClass('page-item')
    .append(
      $('<a>')
      .addClass('page-link')
      .attr({
        "title": msg['new_window'],
        "href": '/dbviews/rest/user/' + item.type + '/' + item.id + '?' + $.param({
          args: JSON.stringify(item.args),
          filter: JSON.stringify(item.filter),
          options: JSON.stringify(item.options),
          countRows: item.countRows,
          offsetRow: item.offsetRow,
          sortby: JSON.stringify(item.sortby),
          ui: 'bootstrap'
        }),
        "target": '_blank'
      })
      .append('<i class="fas fa-external-link-alt"><\/i>')
    )
  )
  .append(
    $('<li>')
    .addClass('page-item')
    .append(
      $('<a>')
      .addClass('page-link')
      .attr({
        "title": msg['export_to_excel'],
        "href": '/dbviews-api/user/' + item.type + '/' + item.id + '/excel?' + $.param({
          args: JSON.stringify(item.args),
          filter: JSON.stringify(item.filter),
          options: JSON.stringify(item.options),
          sortby: JSON.stringify(item.sortby)
        })
      })
      .append('<i class="fas fa-file-excel"><\/i>')
    )
  )
  .append(
    $('<li>')
    .addClass('page-item')
    .append(
      $('<a>')
      .addClass('page-link')
      .attr({
        "title": msg['export_to_csv'],
        "href": '/dbviews-api/user/' + item.type + '/' + item.id + '/csv?' + $.param({
          args: JSON.stringify(item.args),
          filter: JSON.stringify(item.filter),
          options: JSON.stringify(item.options),
          sortby: JSON.stringify(item.sortby)
        })
      })
      .append('<i class="fas fa-file-csv"><\/i>')
    )
  );
  return $toolbar;
}

function getGraphData(item) {
  var data = [];
  var dataMap = {};
  if (item.rows) {
    for (var r in item.rows) {
      var row = item.rows[r];
      var point = item.yaxisColumn == null ? row[item.xaxisColumn] : [row[item.xaxisColumn], row[item.yaxisColumn]];
      if (item.serieColumn == null) {
        data.push(point);
      }
      else {
        var k = row[item.serieColumn];
        if (!(k in dataMap))
          dataMap[k] = [];
        dataMap[k].push(point);
      }
    }
  }
  if (item.serieColumn == null)
    return [data];
  for (var k in dataMap) {
    var points = dataMap[k];
    if (item.graphType.indexOf('pie') != -1) {
      var total = 0;
      $.each(points, function() {
        total += this;
      });
      points = total;
    }
    var serie = {
      label: k,
      data: points
    };
    data.push(serie);
  }
  return data;
}

function buildGraph(item, container) {
  var $graphContainer = $('<div/>'); //.addClass('graph-container');
  $(container).append($graphContainer);
  var tPie = item.graphType.indexOf('pie') != -1;
  var tBars = item.graphType.indexOf('bars') != -1;
  var tLines = item.graphType.indexOf('lines') != -1;
  var tPoints = item.graphType.indexOf('points') != -1;
  var data = getGraphData(item);
  var $graph = $('<div/>').addClass('graph').css({
    width: item.width,
    height: item.height
  });
  var $filter = $('<table/>').css('border', 'none');
  var $toolbar = buildToolbar(item);
  var $table = $('<table/>').css('border', 'none');
  $graphContainer.append($table);
  var tbHAlign, tbVAlign;
  if (item.toolbarPosition && item.toolbarPosition.length == 2) {
    tbVAlign = item.toolbarPosition.charAt(0);
    tbHAlign = item.toolbarPosition.charAt(1);
  }
  var fHAlign, fVAlign;
  if (item.filterPosition && item.filterPosition.length == 2) {
    fVAlign = item.filterPosition.charAt(0);
    fHAlign = item.filterPosition.charAt(1);
  }
  if (tbVAlign == 'n') {
    $table.append($('<tr/>')
      .append($('<td/>')
        .attr('align', tbHAlign == 'w' ? 'left' : 'right')
        .append($toolbar)
      )
    );
  }
  if (fHAlign == 'w') {
    $table.append($('<tr/>')
      .append($('<td/>').attr('valign', fVAlign == 's' ? 'bottom' : 'top').append($filter))
      .append($('<td/>').attr('width', '30px'))
      .append($('<td/>').attr('valign', 'top').append($graph))
    );
  }
  else if (fHAlign == 'e') {
    $table.append($('<tr/>')
      .append($('<td/>').attr('valign', 'top').append($graph))
      .append($('<td/>').attr('width', '30px'))
      .append($('<td/>').attr('valign', fVAlign == 's' ? 'bottom' : 'top').append($filter))
    );
  }
  else {
    $table.append($('<tr/>')
      .append($('<td/>').attr('valign', 'top').append($graph))
    );
  }
  if (tbVAlign == 's') {
    $table.append($('<tr/>')
      .append($('<td/>')
        .attr('align', tbHAlign == 'w' ? 'left' : 'right')
        .append($toolbar)
      )
    );
  }
  $.plot($graph, data, {
    canvas: false,
    series: {
      pie: {
        show: tPie,
        radius: 1,
        label: {
          show: true,
          radius: 2/3,
          formatter: function (label, series) {
            //return '<div class="pie-label">' + label + ': ' + series.data[0][1] + ' (' + series.percent.toFixed(1) + '%)<\/div>';
            return '<div class="pie-label">' + series.percent.toFixed(1) + '%<\/div>';
          },
          threshold: 0.05,
          background: {
            opacity: 0.3,
            color: '#000'
          }
        },
        stroke: {
          color: '#fff',
          width: 1
        },
        highlight: {
          opacity: 0.2
        }
      },
      bars: {
        show: tBars,
        barWidth: 0.6,
        lineWidth: 1,
        align: 'center',
        fillColor: { colors: [ { opacity: 0.8 }, { opacity: 0.1 } ] }
      },
      points: {
        show: tPoints
      },
      lines: {
        show: tLines
      }
    },
    grid: {
      hoverable: true,
      clickable: true
    },
    xaxis: {
      tickLength: 5,
      mode: item.xmode
    },
    yaxis: {
      tickLength: 5,
      mode: item.ymode
    },
    legend: {
      show: !!item.legendPosition,
      position: item.legendPosition || 'ne'
    }
  });
  $graph.resize(function () {
    console.info("Placeholder is now " + $(this).width() + "x" + $(this).height() + " pixels");
  });
  /*$(window).resize(function() {
    console.info("Window is now " + $(window).width() + "x" + $(window).height() + " pixels");
  });*/
  var previousPoint = null;
  $graph.unbind()
    .bind('plothover', function (event, pos, gItem) {
      if (!gItem) {
        $('#tooltip').remove();
        previousPoint = null;
        return;
      }
      var currentPoint = tPie ? gItem.series.label : gItem.dataIndex;
      if (previousPoint != currentPoint) {
        previousPoint = currentPoint;
        $('#tooltip').remove();
        var label = gItem.series.label;
        $('<div/>')
          .attr('id', 'tooltip')
          .css({
            top: pos.pageY + 5,
            left: pos.pageX + 5
          })
          .text((label ? label + ': ' : '') + (tPie ? gItem.series.data[0][1] : '[x=' + (item.xmode == 'time' ? $.datepicker.formatDate(msg['dateFormat'], new Date(gItem.datapoint[0])) : gItem.datapoint[0]) + (tPie ? '' : ', y=' + (item.ymode == 'time' ? $.datepicker.formatDate(msg['dateFormat'], new Date(gItem.datapoint[1])) : gItem.datapoint[1])) + ']'))
          .appendTo('body')
          .fadeIn(200);
      }
    })
    .bind('plotclick', function(event, pos, gItem) {
      if (!gItem)
        return;
      var label = gItem.series.label;
      if (tPie)
        dlg.info(gItem.series.label + ': ' + gItem.series.data[0][1] + ' (' + parseFloat(gItem.series.percent).toFixed(2) + '%)');
      else
        dlg.info((label ? label + ': ' : '') + '[x=' + (item.xmode == 'time' ? $.datepicker.formatDate(msg['dateFormat'], new Date(gItem.datapoint[0])) : gItem.datapoint[0].toFixed(2)) + ', y=' + (item.ymode == 'time' ? $.datepicker.formatDate(msg['dateFormat'], new Date(gItem.datapoint[1])) : gItem.datapoint[1].toFixed(2)) + ']');
    }
  );

  for (var v in item.headers) {
    th = item.headers[v];
    var id = 'filter-' + item.type + '-' + item.id + '-' + th.id;
    var $input = $('<input/>').val(item.filter[th.id]).attr({
      'placeholder': msg['filter'],
      'id': id,
      'colId': th.id
    }).css({
      'min-width': '50px'
    }).data({
      'item': item,
      'th': th,
      'filter': $filter
    }).keypress(function(e) {
      var code = (e.keyCode ? e.keyCode : e.which);
      if (code != 13)
        return;
      $(getItemContainer(item)).addClass('loading');
      item = $(this).data('item');
      th = $(this).data('th');
      $(this).data('filter').find('input').each(function() {
        item.filter[$(this).attr('colId')] = [ $(this).val() ];
      });
      $.get('/dbviews-api/user/graph/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), sortby: JSON.stringify(item.sortby), focuson: th.id }, function(newItem) {
        var $item = clearContainer(getItemContainer(item));
        buildGraph(newItem, $item);
        $item.append(buildModal()).removeClass('loading');
        var sft = $('#filter-' + newItem.type + '-' + newItem.id + '-' + newItem.focuson).get(0);
        sft.focus();
        sft.select();
      }).error(function() {
        dlg.alert(msg['alert_error']);
        $('.loading').removeClass('loading');
      });
      return false;
    });
    $filter.append(
      $('<tr/>').append(
        $('<th/>').attr('align', 'right').append(
          $('<label/>').attr('for', id).text(th.columnName)
        )
      ).append(
        $('<td/>').append(
          $('<div/>').addClass('filter').append($input)
        )
      )
    );
  }

  return $graphContainer;
}

function buildBlock(item, container) {
  if (item.rows && item.rows.length > 0) {
    var block = item.rows[0][1];
    if (block)
      $(container).append(block);
  }
}
