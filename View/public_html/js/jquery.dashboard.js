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
      selector: ".panel-content"
    }, options);

    var panelWidth = (100.0 - settings.columns * 2) / settings.columns;

    return this.each( function() {
      var $parent = $(this).css("padding", "10px 20px");
      $parent.find(settings.selector).each(function(index, elem) {
        var $elem = $(elem);
        var $panel = $("<div/>").addClass("panel").css("width", panelWidth + "%").append($("<h3/>").text($elem.attr("data-title")));
        if ((index + 1) % settings.columns == 0)
          $panel.addClass("last");
        if (settings.css)
          $panel.css(settings.css);
        $panel.append($elem);
        $parent.append($panel);
      });
    });

  };

}(jQuery));