/**
 *  dbviews.core.js
 *  @requires jQueryUI v1.8 or above
 *
 *  Copyright (c) Alejandro Profet Beruff
 *  Version 1.0 : 22-Apr-2013
 */

$.ajaxSetup({
  cache: false
});

function getItemContainer(item) {
  return '#item-' + item.type + '-' + item.id;
}

function clearContainer(container) {
  var $container = $(container);
  $container.find('.filter input').each(function() {
    var drp = $(this).data('daterangepicker');
    if (drp && drp.container)
      drp.container.remove();
  });
  return $container.empty();
}

function buildView(view, container, replaceContent) {
  document.title = msg['title'] + ' - ' + view.description;
  var $view = $('<div/>').attr('id', 'view');

  if ($.type($view[view.jquiPlugin]) !== 'function') {
    dlg.alert(msg['jqui_plugin_not_found']);
    $('.loading').removeClass('loading');
    return null;
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
    $itemHeader = $('<ul/>');
    $view.append($itemHeader);
  }
  var loadContent = true;
  for (var i in view.items) {
    var item = view.items[i];
    if (view.jquiPlugin == 'tabs') {
      $itemHeader.append(
        $('<li>').append(
          $('<a/>').attr({
            href: getItemContainer(item),
            title: item.description
          })
          .css({
            'padding': '.3em 1em',
            'color': loadContent ? '' : '#aaa'
          })
          .append(
            $('<span/>')
              .addClass('ui-icon ' + (item.type == 'table' ? 'ui-icon-calculator' : item.type == 'graph' ? 'ui-icon-image' : 'ui-icon-carat-2-e-w'))
              .css('display', 'inline-block')
          )
          .append(item.label)
        ).data({
          'item': item
        })
      );
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

    buildItem(item, $view, false, loadContent);
    if (view.lazyLoad)
      loadContent = false;
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

  $view[view.jquiPlugin](options);
  return $view;
}

function buildItem(item, container, replaceContent, loadContent) {
  if (replaceContent)
    document.title = msg['title'] + ' - ' + item.description;
  var $item = $('<div/>').attr({
    'id': 'item-' + item.type + '-' + item.id,
    'data-label': item.label,
    'data-title': item.description
  }).css({
    'text-align': 'center',
    'position': 'relative'
  }).addClass('panel-content');
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
  var $infoTag = $('<div/>').css('margin', '10px').addClass('bold').attr('title', str4mat(msg['query_delay'], { query_delay: item.queryDelay })).html(str4mat(msg['page_info'], { current_pag: currentPag, total_pag: Math.max(totalPag, 1), total_rows: totalRows, s: totalRows > 1 ? 's' : '' }))
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
  var $table = $('<table/>').attr( {
    width: '100%'
  }); //.addClass('fixed');
  var $tableContainer = $('<div/>').css('overflow', 'auto').append($table);
  $(container).append($tableContainer);
  var $tr = $('<tr/>');
  var $nRow = $('<th/>').addClass('ui-state-default').css('padding', '4px').text('#').attr({
    width: '5%', //(item.totalRows.toString().length * 7) + 'px',
    align: 'left',
    valign: item.filterPosition == 'top' ? 'bottom' : 'top',
    rowspan: item.filterPosition == 'top' || item.filterPosition == 'bottom' ? 2 : 1
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
      sortby[sortbyKey] = asc ? 'Desc' : 'Asc';
    }
    var $headerTh = $('<th/>').appendTo($tr)
    .addClass('ui-state-default')
    .css('padding', '4px')
    .attr({
      width: th.width,
      align: th.align,
      valign: th.valign
    }).data({
      'item': item,
      'sortby': sortby
    });
    var $headerThDiv = $('<div/>').text(th.columnName).appendTo($headerTh);
    if (th.sortable) {
      var dirIcon = asc ? 'ui-icon-triangle-1-n' : desc ? 'ui-icon-triangle-1-s' : 'ui-icon-carat-2-n-s';
      $headerThDiv.addClass('sort-wrapper').append(dirIcon && $('<span/>').addClass('sort-icon ui-icon ' + dirIcon))
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
    var $filterTd = $('<td/>').appendTo($filter);
    if (th.filterable) {
      var $input = $('<input/>').val(item.filter[th.id]).attr({
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
      $filterTd.append($('<div/>').addClass('filter').append($input));
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
      var even = j % 2 == 0;
      $tr = $('<tr/>').addClass(even ? 'even' : 'odd').hover(function() {
        $(this).children('td').addClass('hover');
      }, function() {
        $(this).children('td').removeClass('hover');
      });
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
  var $toolbar = $('<div/>').addClass('toolbar ui-widget-header ui-corner-all');
  $(container).append($toolbar);
  if (item.type == 'table') {
    $toolbar.append($('<button/>').html(msg['first_page']).button({
      text: false,
      icons: {
        primary: 'ui-icon-seek-start'
      },
      disabled: offsetRow == 1
    }).data({
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
    }));
    $toolbar.append($('<button/>').html(msg['previous_page']).css('margin-right', '20px').button({
      text: false,
      icons: {
        primary: 'ui-icon-seek-prev'
      },
      disabled: offsetRow == 1
    }).data({
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
    }));
    for (var p = -4; p <= 4; p++) {
      var pagToShow = currentPag + p;
      if (pagToShow >= 1 && pagToShow <= totalPag) {
        $toolbar.append($('<button/>').html(pagToShow).attr('title', p != 0 ? msg['go_to_page'] + ' ' + pagToShow : '').button({
          disabled: p == 0
        }).data({
          'item': item,
          'pagToShow': pagToShow
        }).click(function() {
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
        }));
      }
    }
    $toolbar.append($('<button/>').html(msg['next_page']).css('margin-left', '20px').button({
      text: false,
      icons: {
        primary: 'ui-icon-seek-next'
      },
      disabled: offsetRow + countRows > totalRows
    }).data({
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
    }));
    $toolbar.append($('<button/>').css('margin-right', '20px').html(msg['last_page']).button({
      text: false,
      icons: {
        primary: 'ui-icon-seek-end'
      },
      disabled: offsetRow + countRows > totalRows
    }).data({
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
    }));
  }
  $toolbar.append($('<button/>').html(msg['refresh']).button({
    text: false,
    icons: {
      primary: 'ui-icon-refresh'
    }
  }).data({
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
  })).append($('<button/>').html(msg['clear']).button({
    text: false,
    icons: {
      primary: 'ui-icon-cancel'
    }
  }).data({
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
  })).append($('<a/>').attr({
    'href': '/dbviews/rest/user/' + item.type + '/' + item.id + '?' + $.param({ args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow, sortby: JSON.stringify(item.sortby) }),
    'target': '_blank'
  }).html(msg['new_window']).button({
    text: false,
    icons: {
      primary: 'ui-icon-newwin'
    }
  })).append($('<a/>')
    .attr('href', '/dbviews-api/user/' + item.type + '/' + item.id + '/excel?' + $.param({ args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), sortby: JSON.stringify(item.sortby) }))
    .html(msg['export_to_excel'])
    .button({
      text: false,
      icons: {
        primary: 'excel'
      }
    }
  )).append($('<a/>')
    .attr('href', '/dbviews-api/user/' + item.type + '/' + item.id + '/csv?' + $.param({ args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), sortby: JSON.stringify(item.sortby) }))
    .html(msg['export_to_csv'])
    .button({
      text: false,
      icons: {
        primary: 'ui-icon-grip-solid-vertical'
      }
    }
  ));
  return $toolbar;
}

function buildModal(container) {
  var $modal = $('<div/>').addClass('loading-modal').attr('title', msg['loading_please_wait']);
  $(container).append($modal);
  return $modal;
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
    $filter.append($('<tr/>').append($('<th/>').attr('align', 'right').append($('<label/>').attr('for', id).text(th.columnName))).append($('<td/>').append($('<div/>').addClass('filter').append($input))));
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
