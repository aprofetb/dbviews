/**
 *  dbviews.dialogs.js
 *  @requires jQueryUI v1.8 or above
 *
 *  Copyright (c) Alejandro Profet Beruff
 *  Version 1.0 : 15-Jun-2011
 */

var clrfRE = /\r\n|\r|\n/;
window.dlg = new Object();

dlg.alertExt = function(text, title, afterClose) {
  if (!text)
    return;
  if (typeof(text) != 'string')
    try {
      text = text.toString();
    }
    catch (err) {
      return;
    }
  var buttons = {};
  buttons[msg['continue']] = function() {
    $(this).dialog('close');
    if (typeof(afterClose) == 'function')
      afterClose();
    $(this).dialog('destroy').remove();
  };
  $('<div>')
    .html('<p style="padding:10px;text-align:justify" class="ui-state-error ui-corner-all"><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"><\/span>' + text.replace(clrfRE, '<br>') + '<\/p>')
    .dialog({
      autoResize: true,
      height: 'auto',
      width: '400px',
      minHeight: '200px',
      modal: true,
      dialogClass: 'alert',
      title: title,
      buttons: buttons
    })
    .dialog('open');
};
dlg.alert = function(text, afterClose) {
  dlg.alertExt(text, msg['alert'], afterClose);
};

dlg.infoExt = function(text, title, afterClose) {
  if (!text)
    return;
  if (typeof(text) != 'string')
    try {
      text = text.toString();
    }
    catch (err) {
      return;
    }
  var buttons = {};
  buttons[msg['continue']] = function() {
    $(this).dialog('close');
    if (typeof(afterClose) == 'function')
      afterClose();
    $(this).dialog('destroy').remove();
  };
  $('<div>')
    .html('<p style="padding:10px;text-align:justify" class="ui-state-highlight ui-corner-all"><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"><\/span>' + text.replace(clrfRE, '<br>') + '<\/p>')
    .dialog({
      autoResize: true,
      height: 'auto',
      width: '400px',
      minHeight: '200px',
      modal: true,
      dialogClass: 'info',
      title: title,
      buttons: buttons
    })
    .dialog('open');
};
dlg.info = function(text, afterClose) {
  dlg.infoExt(text, msg['information'], afterClose);
};

dlg.confirmExt = function(text, title, response) {
  if (!text)
    return;
  if (typeof(text) != 'string')
    try {
      text = text.toString();
    }
    catch (err) {
      return;
    }
  var buttons = {};
  buttons[msg['continue']] = function() {
    $(this).dialog('close');
    if (typeof(response) == 'function')
      response(true);
    $(this).dialog('destroy').remove();
  };
  buttons[msg['cancel']] = function() {
    $(this).dialog('close');
    if (typeof(response) == 'function')
      response(false);
  };
  $('<div>')
    .html('<p style="padding:10px;text-align:justify" class="ui-state-highlight ui-corner-all"><span class="ui-icon ui-icon-alert" style="float:left;margin-right:.3em;"><\/span>' + text.replace(clrfRE, '<br>') + '<\/p>')
    .dialog({
      autoResize: true,
      height: 'auto',
      width: '400px',
      minHeight: '200px',
      modal: true,
      dialogClass: 'confirm',
      title: msg['confirm'],
      buttons: buttons
    })
    .dialog('open');
};
dlg.confirm = function(text, response) {
  dlg.confirmExt(text, msg['confirm'], response);
};

w2c = {
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
