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

function buildTabs(view, container) {
  document.title = 'Database Views - ' + view.description;
  var $tabs = $('<div/>').attr('id', 'tabs');
  $(container).append($tabs);
  var $ul = $('<ul/>');
  $tabs.append($ul);
  for (var i in view.tabs) {
    var tab = view.tabs[i];
    $ul.append($('<li/>').append($('<a/>').attr('href', '#sect-' + tab.type + '-' + tab.id).attr('title', tab.description).css('padding', '.3em 1em').append($('<span/>').addClass(tab.type == 'table' ? 'ui-icon ui-icon-calculator' : 'ui-icon ui-icon-image').css('display', 'inline-block')).append(tab.label)));
    buildSection(tab, $tabs, true);
  }
  $tabs.tabs({
    collapsible: true
  });
  $tabs.find('.ui-tabs-nav').sortable({
    axis: 'x',
    stop: function() {
      $tabs.tabs('refresh');
    }
  });
  return $tabs;
}

function buildSection(tab, container, isTab) {
  var $sect = $('<div/>').attr('id', 'sect-' + tab.type + '-' + tab.id).css('text-align', 'center');
  $(container).append($sect);
  if (tab.type == 'table') {
    buildInfoTag(tab, $sect);
    buildTable(tab, $sect);
    buildInfoTag(tab, $sect);
    buildToolbar(tab, $sect);
  }
  else if (tab.type == 'graph') {
    buildGraph(tab, $sect, isTab);
  }
  $sect.append(buildModal());
  return $sect;
}

function buildInfoTag(tab, container) {
  var offsetRow = tab.offsetRow;
  var countRows = tab.countRows;
  var totalRows = tab.totalRows;
  var totalPag = Math.floor(totalRows / countRows) + (totalRows % countRows == 0 ? 0 : 1);
  var currentPag = Math.floor((offsetRow - 1) / countRows) + 1;
  var $infoTag = $('<div/>').css('margin', '10px').addClass('bold').attr('title', str4mat(msg['query_delay'], { query_delay: tab.queryDelay })).html(str4mat(msg['page_info'], { current_pag: currentPag, total_pag: totalPag, total_rows: totalRows, s: totalRows > 1 ? 's' : '' }))
  $(container).append($infoTag);
  return $infoTag;
}

function buildTable(tab, container) {
  var $table = $('<table/>').attr( {
    width: '100%'
  }); //.addClass('fixed');
  var $tableContainer = $('<div/>').css('overflow', 'auto').append($table);
  $(container).append($tableContainer);
  var $tr = $('<tr/>');
  $tr.append($('<th/>').addClass('ui-state-default').css('padding', '4px').html('#').attr({
    width: '5%', //(tab.totalRows.toString().length * 7) + 'px',
    align: 'left',
    valign: 'top',
    rowspan: 2
  }));
  var $filter = $('<tr/>');
  for (var v in tab.headers) {
    th = tab.headers[v];
    var dir = tab.sortby[th.id];
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
        'tab': tab,
        'sortby': sortby
      })
      .click(function() {
        $('#sect-' + tab.type + '-' + tab.id).addClass('loading');
        tab = $(this).data('tab');
        sortby = $(this).data('sortby');
        $.get('/api/user/table/' + tab.id, { args: JSON.stringify(tab.args), filter: JSON.stringify(tab.filter), options: JSON.stringify(tab.options), countRows: tab.countRows, offsetRow: tab.offsetRow, sortby: JSON.stringify(sortby) }, function(newTab) {
          var $sect = $('#sect-' + tab.type + '-' + tab.id).empty();
          buildInfoTag(newTab, $sect);
          buildTable(newTab, $sect);
          buildInfoTag(newTab, $sect);
          buildToolbar(newTab, $sect);
          $sect.append(buildModal()).removeClass('loading');
        }).error(function() {
          alert(msg['alert_error']);
        });
      })
    );
    var $input = $('<input/>').val(tab.filter[th.id]).attr({
      'placeholder': msg['filter'],
      'id': 'filter-' + tab.type + '-' + tab.id + '-' + th.id,
      'colId': th.id
    }).data({
      'tab': tab,
      'th': th,
      '$tr': $filter
    }).keypress(function(e) {
      var code = (e.keyCode ? e.keyCode : e.which);
      if (code != 13)
        return;
      $('#sect-' + tab.type + '-' + tab.id).addClass('loading');
      tab = $(this).data('tab');
      th = $(this).data('th');
      $(this).data('$tr').find('input').each(function() {
        tab.filter[$(this).attr('colId')] = $(this).val();
      });
      $.get('/api/user/table/' + tab.id, { args: JSON.stringify(tab.args), filter: JSON.stringify(tab.filter), options: JSON.stringify(tab.options), countRows: tab.countRows, offsetRow: tab.offsetRow, sortby: JSON.stringify(tab.sortby), focuson: th.id }, function(newTab) {
        var $sect = $('#sect-' + tab.type + '-' + tab.id).empty();
        buildInfoTag(newTab, $sect);
        buildTable(newTab, $sect);
        buildInfoTag(newTab, $sect);
        buildToolbar(newTab, $sect);
        $sect.append(buildModal()).removeClass('loading');
        var sft = $('#filter-' + newTab.type + '-' + newTab.id + '-' + newTab.focuson).get(0);
        sft.focus();
        sft.select();
      }).error(function() {
        alert(msg['alert_error']);
      });
      return false;
    });
    if (th.type == 93) {
      $input.datepicker({ dateFormat: 'dd/mm/yy' }).change(function() {
        $('#sect-' + tab.type + '-' + tab.id).addClass('loading');
        tab = $(this).data('tab');
        th = $(this).data('th');
        $(this).data('$tr').find('input').each(function() {
          tab.filter[$(this).attr('colId')] = $(this).val();
        });
        $.get('/api/user/table/' + tab.id, { args: JSON.stringify(tab.args), filter: JSON.stringify(tab.filter), options: JSON.stringify(tab.options), countRows: tab.countRows, offsetRow: tab.offsetRow, sortby: JSON.stringify(tab.sortby), focuson: th.id }, function(newTab) {
          var $sect = $('#sect-' + tab.type + '-' + tab.id).empty();
          buildInfoTag(newTab, $sect);
          buildTable(newTab, $sect);
          buildInfoTag(newTab, $sect);
          buildToolbar(newTab, $sect);
          $sect.append(buildModal()).removeClass('loading');
          var sft = $('#filter-' + newTab.type + '-' + newTab.id + '-' + newTab.focuson).get(0);
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
  for (var j = 0; j < tab.rows.length; j++) {
    var cells = tab.rows[j];
    var even = j % 2 == 0;
    $tr = $('<tr/>').addClass(even ? 'even' : 'odd').hover(function() {
      $(this).children('td').addClass('hover');
    }, function() {
      $(this).children('td').removeClass('hover');
    });
    $tr.append($('<td/>').html(tab.offsetRow + j)).attr({
      align: 'left',
      valign: 'top'
    });
    for (var v in tab.headers) {
      th = tab.headers[v];
      $tr.append($('<td/>').addClass(tab.sortby[th.id] ? 'sorted' : '').html(cells[th.id])).attr({
        align: th.align,
        valign: th.valign
      });
    }
    $table.append($tr);
  }
  return $tableContainer;
}

function buildToolbar(tab, container) {
  var offsetRow = tab.offsetRow;
  var countRows = tab.countRows;
  var totalRows = tab.totalRows;
  var totalPag = Math.floor(totalRows / countRows) + (totalRows % countRows == 0 ? 0 : 1);
  var currentPag = Math.floor((offsetRow - 1) / countRows) + 1;
  var $toolbar = $('<div/>').addClass('toolbar ui-widget-header ui-corner-all');
  $(container).append($toolbar);
  $toolbar.append($('<button/>').html(msg['first_page']).button( {
    text : false, icons :  {
      primary : 'ui-icon-seek-start'
    }, disabled: offsetRow == 1
  }).data({
    'tab': tab
  }).click(function() {
    $('#sect-' + tab.type + '-' + tab.id).addClass('loading');
    tab = $(this).data('tab');
    $.get('/api/user/table/' + tab.id, { args: JSON.stringify(tab.args), filter: JSON.stringify(tab.filter), options: JSON.stringify(tab.options), countRows: tab.countRows, offsetRow: 1, sortby: JSON.stringify(tab.sortby) }, function(newTab) {
      var $sect = $('#sect-' + tab.type + '-' + tab.id).empty();
      buildInfoTag(newTab, $sect);
      buildTable(newTab, $sect);
      buildInfoTag(newTab, $sect);
      buildToolbar(newTab, $sect);
      $sect.append(buildModal()).removeClass('loading');
    }).error(function() {
      alert(msg['alert_error']);
      $('#sect-' + tab.type + '-' + tab.id).removeClass('loading');
    });
  })).append($('<button/>').html(msg['previous_page']).css('margin-right', '20px').button( {
    text : false, icons :  {
      primary : 'ui-icon-seek-prev'
    }, disabled: offsetRow == 1
  }).data({
    'tab': tab
  }).click(function() {
    $('#sect-' + tab.type + '-' + tab.id).addClass('loading');
    tab = $(this).data('tab');
    $.get('/api/user/table/' + tab.id, { args: JSON.stringify(tab.args), filter: JSON.stringify(tab.filter), options: JSON.stringify(tab.options), countRows: tab.countRows, offsetRow: tab.offsetRow - tab.countRows, sortby: JSON.stringify(tab.sortby) }, function(newTab) {
      var $sect = $('#sect-' + tab.type + '-' + tab.id).empty();
      buildInfoTag(newTab, $sect);
      buildTable(newTab, $sect);
      buildInfoTag(newTab, $sect);
      buildToolbar(newTab, $sect);
      $sect.append(buildModal()).removeClass('loading');
    }).error(function() {
      alert(msg['alert_error']);
      $('#sect-' + tab.type + '-' + tab.id).removeClass('loading');
    });
  }));
  for (var p = -4; p <= 4; p++) {
    var pagToShow = currentPag + p;
    if (pagToShow >= 1 && pagToShow <= totalPag)
      $toolbar.append($('<button/>').html(pagToShow).attr('title', p != 0 ? msg['go_to_page'] + ' ' + pagToShow : '').button( {
        disabled: p == 0
      }).data({
        'tab': tab,
        'pagToShow': pagToShow
      }).click(function() {
        $('#sect-' + tab.type + '-' + tab.id).addClass('loading');
        tab = $(this).data('tab');
        sortby = $(this).data('sortby');
        pagToShow = $(this).data('pagToShow');
        $.get('/api/user/table/' + tab.id, { args: JSON.stringify(tab.args), filter: JSON.stringify(tab.filter), options: JSON.stringify(tab.options), countRows: tab.countRows, offsetRow: (pagToShow - 1) * tab.countRows + 1, sortby: JSON.stringify(tab.sortby) }, function(newTab) {
        var $sect = $('#sect-' + tab.type + '-' + tab.id).empty();
        buildInfoTag(newTab, $sect);
        buildTable(newTab, $sect);
        buildInfoTag(newTab, $sect);
        buildToolbar(newTab, $sect);
        $sect.append(buildModal()).removeClass('loading');
        }).error(function() {
          alert(msg['alert_error']);
          $('#sect-' + tab.type + '-' + tab.id).removeClass('loading');
        });
      }));
  }
  $toolbar.append($('<button/>').html(msg['next_page']).css('margin-left', '20px').button( {
    text : false, icons :  {
      primary : 'ui-icon-seek-next'
    }, disabled: offsetRow + countRows > totalRows
  }).data({
    'tab': tab
  }).click(function() {
    $('#sect-' + tab.type + '-' + tab.id).addClass('loading');
    tab = $(this).data('tab');
    sortby = $(this).data('sortby');
    $.get('/api/user/table/' + tab.id, { args: JSON.stringify(tab.args), filter: JSON.stringify(tab.filter), options: JSON.stringify(tab.options), countRows: tab.countRows, offsetRow: tab.offsetRow + tab.countRows, sortby: JSON.stringify(tab.sortby) }, function(newTab) {
      var $sect = $('#sect-' + tab.type + '-' + tab.id).empty();
      buildInfoTag(newTab, $sect);
      buildTable(newTab, $sect);
      buildInfoTag(newTab, $sect);
      buildToolbar(newTab, $sect);
      $sect.append(buildModal()).removeClass('loading');
    }).error(function() {
      alert(msg['alert_error']);
      $('#sect-' + tab.type + '-' + tab.id).removeClass('loading');
    });
  })).append($('<button/>').html(msg['last_page']).button( {
    text : false, icons :  {
      primary : 'ui-icon-seek-end'
    }, disabled: offsetRow + countRows > totalRows
  }).data({
    'tab': tab
  }).click(function() {
    $('#sect-' + tab.type + '-' + tab.id).addClass('loading');
    tab = $(this).data('tab');
    $.get('/api/user/table/' + tab.id, { args: JSON.stringify(tab.args), filter: JSON.stringify(tab.filter), options: JSON.stringify(tab.options), countRows: tab.countRows, offsetRow: Math.floor((tab.totalRows - 1) / tab.countRows) * tab.countRows + 1, sortby: JSON.stringify(tab.sortby) }, function(newTab) {
      var $sect = $('#sect-' + tab.type + '-' + tab.id).empty();
      buildInfoTag(newTab, $sect);
      buildTable(newTab, $sect);
      buildInfoTag(newTab, $sect);
      buildToolbar(newTab, $sect);
      $sect.append(buildModal()).removeClass('loading');
    }).error(function() {
      alert(msg['alert_error']);
      $('#sect-' + tab.type + '-' + tab.id).removeClass('loading');
    });
  })).append($('<button/>').css('margin-left', '20px').html(msg['refresh']).button( {
    text : false, icons :  {
      primary : 'ui-icon-refresh'
    }
  }).data({
    'tab': tab
  }).click(function() {
    $('#sect-' + tab.type + '-' + tab.id).addClass('loading');
    tab = $(this).data('tab');
    $.get('/api/user/table/' + tab.id, { args: JSON.stringify(tab.args), filter: JSON.stringify(tab.filter), options: JSON.stringify(tab.options), countRows: tab.countRows, offsetRow: tab.offsetRow, sortby: JSON.stringify(tab.sortby) }, function(newTab) {
      var $sect = $('#sect-' + tab.type + '-' + tab.id).empty();
      buildInfoTag(newTab, $sect);
      buildTable(newTab, $sect);
      buildInfoTag(newTab, $sect);
      buildToolbar(newTab, $sect);
      $sect.append(buildModal()).removeClass('loading');
    }).error(function() {
      alert(msg['alert_error']);
      $('#sect-' + tab.type + '-' + tab.id).removeClass('loading');
    });
  })).append($('<a/>').attr({
    'href': '/api/user/table/' + tab.id + '/excel?' + $.param({ args: JSON.stringify(tab.args), filter: JSON.stringify(tab.filter), options: JSON.stringify(tab.options), sortby: JSON.stringify(tab.sortby) }),
    'target': '_blank'
  }).html(msg['export_this_table_to_excel']).button( {
    text : false, icons :  {
      primary : 'excel'
    }
  }));
  return $toolbar;
}

function buildModal(container) {
  var $modal = $('<div/>').addClass('modal');
  $(container).append($modal);
  return $modal;
}

function getGraphData(tab) {
  var data = [];
  var dataMap = {};
  for (var r in tab.rows) {
    var row = tab.rows[r];
    var point = tab.yaxisColumn == null ? row[tab.xaxisColumn] : [row[tab.xaxisColumn], row[tab.yaxisColumn]];
    if (tab.serieColumn == null) {
      data.push(point);
    }
    else {
      var k = row[tab.serieColumn];
      if (!(k in dataMap))
        dataMap[k] = [];
      dataMap[k].push(point);
    }
  }
  if (tab.serieColumn == null)
    return [data];
  for (var k in dataMap) {
    var points = dataMap[k];
    if (tab.graphType.indexOf('pie') != -1) {
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

function buildGraph(tab, container, showExtLink) {
  var $graphContainer = $('<div/>').addClass('graph-container');
  $(container).append($graphContainer);
  var tPie = tab.graphType.indexOf('pie') != -1;
  var tBars = tab.graphType.indexOf('bars') != -1;
  var tLines = tab.graphType.indexOf('lines') != -1;
  var tPoints = tab.graphType.indexOf('points') != -1;
  var data = getGraphData(tab);
  //alert(tab.label + ': ' + JSON.stringify(data));
  var $graph = $('<div/>').addClass('graph').css({
    width: tab.width,
    height: tab.height
  });
  var $filterTab = $('<table/>').css('border', 'none');
  $graphContainer
    .append($('<table/>')
      .css('border', 'none')
      .append($('<tr/>')
        .append($('<td/>').attr('valign', 'top').append($graph))
        .append($('<td/>').attr('width', '100px'))
        .append($('<td/>').attr('valign', 'top').append($filterTab))
      )
      .append($('<tr/>')
        .css('display', showExtLink ? '' : 'none')
        .append($('<td/>')
          .attr('align', 'right')
          .append($('<a/>')
            .attr('href', '/dbviews/rest/user/graph/' + tab.id + '?' + $.param({ args: JSON.stringify(tab.args), filter: JSON.stringify(tab.filter), options: JSON.stringify(tab.options), sortby: JSON.stringify(tab.sortby) }))
            .attr('target', '_blank')
            .addClass('ui-icon ui-icon-newwin')
            .attr('title', msg['new_window'])
          )
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
            return '<div style="font-size:8pt; text-align:center; padding:2px; color:white; width:50px; height:50px;">' + label + ': ' + series.data[0][1] + ' (' + series.percent.toFixed(1) + '%)<\/div>';
          },
          threshold: 0.05
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
      mode: tab.xmode
    },
    yaxis: {
      tickLength: 5,
      mode: tab.ymode
    }
  });
  $graph.unbind()
    .bind('plothover', function (event, pos, item) {
      if (!item) {
        $('#tooltip').remove();
        previousPoint = null;
        return;
      }
      var currentPoint = tPie ? item.series.label : item.dataIndex;
      if (previousPoint != currentPoint) {
        previousPoint = currentPoint;
        $('#tooltip').remove();
        var label = item.series.label;
        $('<div/>')
          .attr('id', 'tooltip')
          .css({
            top: pos.pageY + 5,
            left: pos.pageX + 5
          })
          .html((label ? label + ': ' : '') + '[x=' + item.datapoint[0] + ', y=' + item.datapoint[1] + ']')
          .appendTo('body')
          .fadeIn(200);
      }
    })
    .bind('plotclick', function(event, pos, item) {
      if (!item)
        return;
      var label = item.series.label;
      if (tPie)
        info(item.series.label + ': ' + parseFloat(item.series.percent).toFixed(2) + '%');
      else
        info((label ? label + ': ' : '') + '[x=' + item.datapoint[0] + ', y=' + item.datapoint[1] + ']');
    }
  );

  for (var v in tab.headers) {
    th = tab.headers[v];
    var $input = $('<input/>').val(tab.filter[th.id]).attr({
      'placeholder': msg['filter'],
      'id': 'filter-' + tab.type + '-' + tab.id + '-' + th.id,
      'colId': th.id
    }).data({
      'tab': tab,
      'th': th,
      'filterTable': $filterTab
    }).keypress(function(e) {
      var code = (e.keyCode ? e.keyCode : e.which);
      if (code != 13)
        return;
      $('#sect-' + tab.type + '-' + tab.id).addClass('loading');
      tab = $(this).data('tab');
      th = $(this).data('th');
      $(this).data('filterTable').find('input').each(function() {
        tab.filter[$(this).attr('colId')] = $(this).val();
      });
      $.get('/api/user/graph/' + tab.id, { args: JSON.stringify(tab.args), filter: JSON.stringify(tab.filter), options: JSON.stringify(tab.options), sortby: JSON.stringify(tab.sortby) }, function(newTab) {
        var $sect = $('#sect-' + tab.type + '-' + tab.id).empty();
        buildGraph(newTab, $sect, showExtLink);
        $sect.append(buildModal()).removeClass('loading');
        var sft = $('#filter-' + newTab.type + '-' + newTab.id + '-' + newTab.focuson).get(0);
        sft.focus();
        sft.select();
      }).error(function() {
        alert(msg['alert_error']);
      });
      return false;
    });
    $filterTab.append($('<tr/>').append($('<th/>').attr('align', 'right').html(th.columnName)).append($('<td/>').append($('<div/>').addClass('filter').append($input))));
  }

  return $graphContainer;
}
