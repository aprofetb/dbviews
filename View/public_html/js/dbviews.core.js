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

function buildView(view, container) {
  document.title = 'Database Views - ' + view.description;
  var $view = $('<div/>').attr('id', 'view');
  $(container).append($view);
  var $itemHeader;
  if (view.jquiPlugin == 'tabs') {
    $itemHeader = $('<ul/>');
    $view.append($itemHeader);
  }
  for (var i in view.items) {
    var item = view.items[i];
    if (view.jquiPlugin == 'tabs') {
      $itemHeader.append(
        $('<li/>').append(
          $('<a/>').attr({
            href: '#item-' + item.type + '-' + item.id,
            title: item.description
          })
          .css('padding', '.3em 1em')
          .append(
            $('<span/>')
              .addClass(item.type == 'table' ? 'ui-icon ui-icon-calculator' : 'ui-icon ui-icon-image')
              .css('display', 'inline-block')
          )
          .append(item.label)
        )
      );
    }
    else if (view.jquiPlugin == 'accordion') {
      $itemHeader = $('<h3/>')
        .attr('title', item.description)
        .append(
          $('<span/>')
            .addClass(item.type == 'table' ? 'ui-icon ui-icon-calculator' : 'ui-icon ui-icon-image')
            .css('display', 'inline-block')
        )
        .append(item.label);
      $view.append($itemHeader);
    }
    buildItem(item, $view, true);
  }
  if ($.type($view[view.jquiPlugin]) !== 'function') {
    alert(msg['jqui_plugin_not_found']);
    return null;
  }
  var options;
  try {
    options = $.parseJSON(view.jquiPluginOptions);
  }
  catch (err) {
    alert(msg['jqui_plugin_parsing_error'] + '<br>' + err);
    return null;
  }
  $view[view.jquiPlugin](options);
  return $view;
}

function buildItem(item, container) {
  var $item = $('<div/>').attr('id', 'item-' + item.type + '-' + item.id).css('text-align', 'center').css('position', 'relative');
  $(container).append($item);
  if (item.type == 'table') {
    buildInfoTag(item, $item);
    buildTable(item, $item);
    buildInfoTag(item, $item);
    buildToolbar(item, $item);
  }
  else if (item.type == 'graph') {
    buildGraph(item, $item);
  }
  else if (item.type == 'block') {
    buildBlock(item, $item);
  }
  else {
    alert('Unknown item type');
    return false;
  }
  $item.append(buildModal());
  return $item;
}

function buildInfoTag(item, container) {
  var offsetRow = item.offsetRow;
  var countRows = item.countRows;
  var totalRows = item.totalRows;
  var totalPag = Math.floor(totalRows / countRows) + (totalRows % countRows == 0 ? 0 : 1);
  var currentPag = Math.floor((offsetRow - 1) / countRows) + 1;
  var $infoTag = $('<div/>').css('margin', '10px').addClass('bold').attr('title', str4mat(msg['query_delay'], { query_delay: item.queryDelay })).html(str4mat(msg['page_info'], { current_pag: currentPag, total_pag: totalPag, total_rows: totalRows, s: totalRows > 1 ? 's' : '' }))
  $(container).append($infoTag);
  return $infoTag;
}

function buildTable(item, container) {
  var $table = $('<table/>').attr( {
    width: '100%'
  }); //.addClass('fixed');
  var $tableContainer = $('<div/>').css('overflow', 'auto').append($table);
  $(container).append($tableContainer);
  var $tr = $('<tr/>');
  $tr.append($('<th/>').addClass('ui-state-default').css('padding', '4px').html('#').attr({
    width: '5%', //(item.totalRows.toString().length * 7) + 'px',
    align: 'left',
    valign: 'top',
    rowspan: 2
  }));
  var $filter = $('<tr/>');
  for (var v in item.headers) {
    th = item.headers[v];
    var dir = item.sortby[th.id];
    var asc = dir == 'Asc';
    var desc = dir == 'Desc';
    var sortby = { };
    sortby[th.id] = asc ? 'Desc' : 'Asc';
    var dirIcon = asc ? 'ui-icon-triangle-1-n' : desc ? 'ui-icon-triangle-1-s' : 'ui-icon-carat-2-n-s';
    $tr.append($('<th/>').attr({
      width: th.width,
      align: th.align,
      valign: th.valign
    }).addClass('sortable ui-state-default')
      .append($('<div/>').addClass('sort-wrapper').html(th.columnName).append(dirIcon && $('<span/>').addClass('sort-icon ui-icon ' + dirIcon)))
      .data({
        'item': item,
        'sortby': sortby
      })
      .click(function() {
        $('#item-' + item.type + '-' + item.id).addClass('loading');
        item = $(this).data('item');
        sortby = $(this).data('sortby');
        $.get('/api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow, sortby: JSON.stringify(sortby) }, function(newItem) {
          var $item = $('#item-' + item.type + '-' + item.id).empty();
          buildInfoTag(newItem, $item);
          buildTable(newItem, $item);
          buildInfoTag(newItem, $item);
          buildToolbar(newItem, $item);
          $item.append(buildModal()).removeClass('loading');
        }).error(function() {
          alert(msg['alert_error']);
        });
      })
    );
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
      $('#item-' + item.type + '-' + item.id).addClass('loading');
      item = $(this).data('item');
      th = $(this).data('th');
      $(this).data('$tr').find('input').each(function() {
        item.filter[$(this).attr('colId')] = $(this).val();
      });
      $.get('/api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow, sortby: JSON.stringify(item.sortby), focuson: th.id }, function(newItem) {
        var $item = $('#item-' + item.type + '-' + item.id).empty();
        buildInfoTag(newItem, $item);
        buildTable(newItem, $item);
        buildInfoTag(newItem, $item);
        buildToolbar(newItem, $item);
        $item.append(buildModal()).removeClass('loading');
        var sft = $('#filter-' + newItem.type + '-' + newItem.id + '-' + newItem.focuson).get(0);
        sft.focus();
        sft.select();
      }).error(function() {
        alert(msg['alert_error']);
      });
      return false;
    });
    if (th.type == 93) {
      $input.datepicker({ dateFormat: 'dd/mm/yy' }).change(function() {
        $('#item-' + item.type + '-' + item.id).addClass('loading');
        item = $(this).data('item');
        th = $(this).data('th');
        $(this).data('$tr').find('input').each(function() {
          item.filter[$(this).attr('colId')] = $(this).val();
        });
        $.get('/api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow, sortby: JSON.stringify(item.sortby), focuson: th.id }, function(newItem) {
          var $item = $('#item-' + item.type + '-' + item.id).empty();
          buildInfoTag(newItem, $item);
          buildTable(newItem, $item);
          buildInfoTag(newItem, $item);
          buildToolbar(newItem, $item);
          $item.append(buildModal()).removeClass('loading');
          var sft = $('#filter-' + newItem.type + '-' + newItem.id + '-' + newItem.focuson).get(0);
          sft.focus();
          sft.select();
        }).error(function() {
          alert(msg['alert_error']);
        });
        return false;
      });
    }
    $filter.append($('<td/>').append($('<div/>').addClass('filter').append($input)));
  }
  $table.append($tr);
  $table.append($filter);
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
      $tr.append($('<td/>').addClass(item.sortby[th.id] ? 'sorted' : '').html(cells[th.id])).attr({
        align: th.align,
        valign: th.valign
      });
    }
    $table.append($tr);
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
    $toolbar.append($('<button/>').html(msg['first_page']).button( {
      text : false, icons :  {
        primary : 'ui-icon-seek-start'
      }, disabled: offsetRow == 1
    }).data({
      'item': item
    }).click(function() {
      $('#item-' + item.type + '-' + item.id).addClass('loading');
      item = $(this).data('item');
      $.get('/api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: 1, sortby: JSON.stringify(item.sortby) }, function(newItem) {
        var $item = $('#item-' + item.type + '-' + item.id).empty();
        buildInfoTag(newItem, $item);
        buildTable(newItem, $item);
        buildInfoTag(newItem, $item);
        buildToolbar(newItem, $item);
        $item.append(buildModal()).removeClass('loading');
      }).error(function() {
        alert(msg['alert_error']);
        $('#item-' + item.type + '-' + item.id).removeClass('loading');
      });
    }));
    $toolbar.append($('<button/>').html(msg['previous_page']).css('margin-right', '20px').button( {
      text : false, icons :  {
        primary : 'ui-icon-seek-prev'
      }, disabled: offsetRow == 1
    }).data({
      'item': item
    }).click(function() {
      $('#item-' + item.type + '-' + item.id).addClass('loading');
      item = $(this).data('item');
      $.get('/api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow - item.countRows, sortby: JSON.stringify(item.sortby) }, function(newItem) {
        var $item = $('#item-' + item.type + '-' + item.id).empty();
        buildInfoTag(newItem, $item);
        buildTable(newItem, $item);
        buildInfoTag(newItem, $item);
        buildToolbar(newItem, $item);
        $item.append(buildModal()).removeClass('loading');
      }).error(function() {
        alert(msg['alert_error']);
        $('#item-' + item.type + '-' + item.id).removeClass('loading');
      });
    }));
    for (var p = -4; p <= 4; p++) {
      var pagToShow = currentPag + p;
      if (pagToShow >= 1 && pagToShow <= totalPag)
        $toolbar.append($('<button/>').html(pagToShow).attr('title', p != 0 ? msg['go_to_page'] + ' ' + pagToShow : '').button( {
          disabled: p == 0
        }).data({
          'item': item,
          'pagToShow': pagToShow
        }).click(function() {
          $('#item-' + item.type + '-' + item.id).addClass('loading');
          item = $(this).data('item');
          sortby = $(this).data('sortby');
          pagToShow = $(this).data('pagToShow');
          $.get('/api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: (pagToShow - 1) * item.countRows + 1, sortby: JSON.stringify(item.sortby) }, function(newItem) {
            var $item = $('#item-' + item.type + '-' + item.id).empty();
            buildInfoTag(newItem, $item);
            buildTable(newItem, $item);
            buildInfoTag(newItem, $item);
            buildToolbar(newItem, $item);
            $item.append(buildModal()).removeClass('loading');
          }).error(function() {
            alert(msg['alert_error']);
            $('#item-' + item.type + '-' + item.id).removeClass('loading');
          });
        }));
    }
    $toolbar.append($('<button/>').html(msg['next_page']).css('margin-left', '20px').button( {
      text : false, icons :  {
        primary : 'ui-icon-seek-next'
      }, disabled: offsetRow + countRows > totalRows
    }).data({
      'item': item
    }).click(function() {
      $('#item-' + item.type + '-' + item.id).addClass('loading');
      item = $(this).data('item');
      sortby = $(this).data('sortby');
      $.get('/api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow + item.countRows, sortby: JSON.stringify(item.sortby) }, function(newItem) {
        var $item = $('#item-' + item.type + '-' + item.id).empty();
        buildInfoTag(newItem, $item);
        buildTable(newItem, $item);
        buildInfoTag(newItem, $item);
        buildToolbar(newItem, $item);
        $item.append(buildModal()).removeClass('loading');
      }).error(function() {
        alert(msg['alert_error']);
        $('#item-' + item.type + '-' + item.id).removeClass('loading');
      });
    }));
    $toolbar.append($('<button/>').css('margin-right', '20px').html(msg['last_page']).button( {
      text : false, icons :  {
        primary : 'ui-icon-seek-end'
      }, disabled: offsetRow + countRows > totalRows
    }).data({
      'item': item
    }).click(function() {
      $('#item-' + item.type + '-' + item.id).addClass('loading');
      item = $(this).data('item');
      $.get('/api/user/table/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: Math.floor((item.totalRows - 1) / item.countRows) * item.countRows + 1, sortby: JSON.stringify(item.sortby) }, function(newItem) {
        var $item = $('#item-' + item.type + '-' + item.id).empty();
        buildInfoTag(newItem, $item);
        buildTable(newItem, $item);
        buildInfoTag(newItem, $item);
        buildToolbar(newItem, $item);
        $item.append(buildModal()).removeClass('loading');
      }).error(function() {
        alert(msg['alert_error']);
        $('#item-' + item.type + '-' + item.id).removeClass('loading');
      });
    }));
  }
  $toolbar.append($('<button/>').html(msg['refresh']).button( {
    text : false, icons :  {
      primary : 'ui-icon-refresh'
    }
  }).data({
    'item': item
  }).click(function() {
    $('#item-' + item.type + '-' + item.id).addClass('loading');
    item = $(this).data('item');
    $.get('/api/user/' + item.type + '/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow, sortby: JSON.stringify(item.sortby) }, function(newItem) {
      var $item = $('#item-' + item.type + '-' + item.id).empty();
      if (item.type == 'table') {
        buildInfoTag(newItem, $item);
        buildTable(newItem, $item);
        buildInfoTag(newItem, $item);
        buildToolbar(newItem, $item);
      }
      else if (item.type == 'graph') {
        buildGraph(item, $item);
      }
      $item.append(buildModal()).removeClass('loading');
    }).error(function() {
      alert(msg['alert_error']);
      $('#item-' + item.type + '-' + item.id).removeClass('loading');
    });
  })).append($('<a/>').attr({
    'href': '/dbviews/rest/user/' + item.type + '/' + item.id + '?' + $.param({ args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), countRows: item.countRows, offsetRow: item.offsetRow, sortby: JSON.stringify(item.sortby) }),
    'target': '_blank'
  }).html(msg['new_window']).button( {
    text : false, icons :  {
      primary : 'ui-icon-newwin'
    }
  })).append($('<a/>')
    .attr('href', '/api/user/' + item.type + '/' + item.id + '/excel?' + $.param({ args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), sortby: JSON.stringify(item.sortby) }))
    .html(msg['export_to_excel'])
    .button({
      text: false,
      icons: {
        primary: 'excel'
      }
    }
  ));
  return $toolbar;
}

function buildModal(container) {
  var $modal = $('<div/>').addClass('modal');
  $(container).append($modal);
  return $modal;
}

function getGraphData(item) {
  var data = [];
  var dataMap = {};
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
  var $graphContainer = $('<div/>').addClass('graph-container');
  $(container).append($graphContainer);
  var tPie = item.graphType.indexOf('pie') != -1;
  var tBars = item.graphType.indexOf('bars') != -1;
  var tLines = item.graphType.indexOf('lines') != -1;
  var tPoints = item.graphType.indexOf('points') != -1;
  var data = getGraphData(item);
  //alert(item.label + ': ' + JSON.stringify(data));
  var $graph = $('<div/>').addClass('graph').css({
    width: item.width,
    height: item.height
  });
  var $filterItem = $('<table/>').css('border', 'none');
  var $toolbar = buildToolbar(item);
  $graphContainer.append($('<table/>')
    .css('border', 'none')
    .append($('<tr/>')
      .append($('<td/>').attr('valign', 'top').append($graph))
      .append($('<td/>').attr('width', '100px'))
      .append($('<td/>').attr('valign', 'top').append($filterItem))
    )
    .append($('<tr/>')
      .append($('<td/>')
        .attr('align', 'right')
        .append($toolbar)
      )
    )
  );
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
            return '<div class="pie-label">' + label + ': ' + series.data[0][1] + ' (' + series.percent.toFixed(1) + '%)<\/div>';
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
          .html((label ? label + ': ' : '') + '[x=' + gItem.datapoint[0] + (tPie ? '' : ', y=' + gItem.datapoint[1]) + ']')
          .appendTo('body')
          .fadeIn(200);
      }
    })
    .bind('plotclick', function(event, pos, gItem) {
      if (!gItem)
        return;
      var label = gItem.series.label;
      if (tPie)
        info(gItem.series.label + ': ' + parseFloat(gItem.series.percent).toFixed(2) + '%');
      else
        info((label ? label + ': ' : '') + '[x=' + gItem.datapoint[0].toFixed(2) + ', y=' + gItem.datapoint[1].toFixed(2) + ']');
    }
  );

  for (var v in item.headers) {
    th = item.headers[v];
    var $input = $('<input/>').val(item.filter[th.id]).attr({
      'placeholder': msg['filter'],
      'id': 'filter-' + item.type + '-' + item.id + '-' + th.id,
      'colId': th.id
    }).data({
      'item': item,
      'th': th,
      'filterItem': $filterItem
    }).keypress(function(e) {
      var code = (e.keyCode ? e.keyCode : e.which);
      if (code != 13)
        return;
      $('#item-' + item.type + '-' + item.id).addClass('loading');
      item = $(this).data('item');
      th = $(this).data('th');
      $(this).data('filterItem').find('input').each(function() {
        item.filter[$(this).attr('colId')] = $(this).val();
      });
      $.get('/api/user/graph/' + item.id, { args: JSON.stringify(item.args), filter: JSON.stringify(item.filter), options: JSON.stringify(item.options), sortby: JSON.stringify(item.sortby) }, function(newItem) {
        var $item = $('#item-' + item.type + '-' + item.id).empty();
        buildGraph(newItem, $item);
        $item.append(buildModal()).removeClass('loading');
        var sft = $('#filter-' + newItem.type + '-' + newItem.id + '-' + newItem.focuson).get(0);
        sft.focus();
        sft.select();
      }).error(function() {
        alert(msg['alert_error']);
      });
      return false;
    });
    $filterItem.append($('<tr/>').append($('<th/>').attr('align', 'right').html(th.columnName)).append($('<td/>').append($('<div/>').addClass('filter').append($input))));
  }

  return $graphContainer;
}

function buildBlock(item, container) {
  if (item.rows.length > 0) {
    var block = item.rows[0][1];
    if (block)
      $(container).append(block);
  }
}
