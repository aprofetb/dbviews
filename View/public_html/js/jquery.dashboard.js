/**
 *  jquery.dashboard.js
 *  @requires jQuery v1.7 or above
 *
 *  Copyright (c) Alejandro Profet Beruff
 *  Version 1.0 : 12-Dic-2013
 */

(function($) {
  "use strict";

  $.fn.dashboard = function(options) {

    var settings = $.extend({
      columns: 2,
      css: null,
      classes: null,
      selector: ".panel-content",
      autoResize: true
    }, options);

    var panelWidth = (100.0 - settings.columns * 2) / settings.columns;

    return this.each(function() {
      var $parent = $(this).css("padding", "10px 20px");
      var $panels = $parent.find(settings.selector);
      var panelCount = $panels.length;
      var cols = settings.columns;
      var rows = Math.floor(panelCount / cols + ($panels.length % cols == 0 ? 0 : 1));
      var relWidth = 100.0;
      var relHeight = 100.0;
      var rowPercent = relWidth / rows;
      var colPercent = relHeight / cols;
      $panels.each( function(index, elem) {
        var $elem = $(elem);
        var $panel = $("<div/>").addClass("panel").append($("<h3/>").text($elem.attr("data-label")).attr("title", $elem.attr("data-title")));
        if (settings.autoResize) {
          var row = Math.floor(index / cols);
          var col = index % cols;
          $panel.css({
            position: "absolute",
            left: col * colPercent + "%",
            top: row * rowPercent + "%",
            right: relWidth - (col + 1) * colPercent + "%",
            bottom: relHeight - (row + 1) * rowPercent + "%",
            zoom: 1.0
          });
        }
        else {
          $panel.css("width", panelWidth + "%");
          if ((index + 1) % cols == 0)
            $panel.addClass("last");
        }
        if (settings.css)
          $panel.css(settings.css);
        if (settings.classes)
          $panel.addClass(settings.classes);
        $panel.append($elem);
        $parent.append($panel);
      });
    });

  };

}(jQuery));