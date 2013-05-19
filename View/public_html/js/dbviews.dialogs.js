/**
 *  dbviews.dialogs.js
 *  @requires jQueryUI v1.8 or above
 *
 *  Copyright (c) Alejandro Profet Beruff
 *  Version 1.0 : 15-Jun-2011
 */

window.clrfRE = /\r\n|\r|\n/;
window._alert = window.alert;
window._confirm = window.confirm;

window.alertExt = function(text, title, afterClose) {
  if (!text)
    return;
  if (typeof(text) != 'string')
    try {
      text = text.toString();
    }
    catch (err) {
      return;
    }
  $('<div>').html('<p style="padding:10px;text-align:justify" class="ui-state-error ui-corner-all"><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"><\/span>' + text.replace(clrfRE, '<br>') + '<\/p>').dialog({ autoResize: true, height: 'auto', width: '400px', minHeight: '200px', modal: true, dialogClass: 'alert', title: title, buttons: { 'Continue': function() { $(this).dialog('close'); typeof(afterClose) == 'function' && afterClose(); $(this).dialog('destroy').remove() } } }).dialog('open');
};
window.alert = function(text, afterClose) {
  alertExt(text, msg['alert'], afterClose);
};

window.infoExt = function(text, title, afterClose) {
  if (!text)
    return;
  if (typeof(text) != 'string')
    try {
      text = text.toString();
    }
    catch (err) {
      return;
    }
  $('<div>').html('<p style="padding:10px;text-align:justify" class="ui-state-highlight ui-corner-all"><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"><\/span>' + text.replace(clrfRE, '<br>') + '<\/p>').dialog({ autoResize: true, height: 'auto', width: '400px', minHeight: '200px', modal: true, dialogClass: 'info', title: title, buttons: { 'Continue': function() { $(this).dialog('close'); typeof(afterClose) == 'function' && afterClose(); $(this).dialog('destroy').remove() } } }).dialog('open');
};
window.info = function(text, afterClose) {
  infoExt(text, msg['information'], afterClose);
};

window.confirmExt = function(text, title, response) {
  if (!text)
    return;
  if (typeof(text) != 'string')
    try {
      text = text.toString();
    }
    catch (err) {
      return;
    }
  $('<div>').html('<p style="padding:10px;text-align:justify" class="ui-state-highlight ui-corner-all"><span class="ui-icon ui-icon-alert" style="float:left;margin-right:.3em;"><\/span>' + text.replace(clrfRE, '<br>') + '<\/p>').dialog({ autoResize: true, height: 'auto', width: '400px', minHeight: '200px', modal: true, dialogClass: 'confirm', title: msg['confirm'], buttons: { 'Continue': function() { $(this).dialog('close'); typeof(response) == 'function' && response(true); $(this).dialog('destroy').remove() }, 'Cancel': function() { $(this).dialog('close'); typeof(response) == 'function' && response(false); } } }).dialog('open');
};
window.confirm = function(text, response) {
  confirmExt(text, msg['confirm'], response);
};

window.wait = function(text) {
  var $pbar = $('.pbar');
  var $waitdlg = $('.waitdlg');
  clearInterval($pbar.attr('sId'));
  $pbar.progressbar('destroy').remove();
  if ($waitdlg.data('dialog')) {
    waitdlg.dialog('destroy').remove();
  }
  if (!text) {
    return;
  }
  if (typeof(text) != 'string') {
    try {
      text = text.toString();
    }
    catch (err) {
      return;
    }
  }
  $waitdlg = $('<div/>').append($('<div/>').addClass('pbar'))
                        .append($('<p/>').css('font-weight', 'bold').html(text.replace(clrfRE, '<br>')))
                        .dialog({ autoResize: true, 
                                  height: 'auto', 
                                  width: '400px', 
                                  minHeight: '200px', 
                                  closeOnEscape: false, 
                                  draggable: false, 
                                  resizable: false, 
                                  modal: true, 
                                  autoOpen: false, 
                                  title: msg['please_wait'], 
                                  dialogClass: 'waitdlg'
                                });
  $pbar.progressbar({ value: 1 }).attr('sId', setInterval(function() {
    $('.pbar').progressbar('value', ($('.pbar').progressbar('value') + 1) % 101);
  }, 100));
  $waitdlg.dialog('open');
};

window.w2c = {
  info: function(msg) {
    if (typeof(console) != 'undefined' && typeof(console.info) == 'function')
      console.info(msg);
  },
  warn: function(msg) {
    if (typeof(console) != 'undefined' && typeof(console.warn) == 'function')
      console.warn(msg);
  },
  log: function(msg) {
    if (typeof(console) != 'undefined' && typeof(console.log) == 'function')
      console.log(msg);
  },
  debug: function(msg) {
    if (typeof(console) != 'undefined' && typeof(console.debug) == 'function')
      console.debug(msg);
  },
  error: function(msg) {
    if (typeof(console) != 'undefined' && typeof(console.error) == 'function')
      console.error(msg);
  }
};
