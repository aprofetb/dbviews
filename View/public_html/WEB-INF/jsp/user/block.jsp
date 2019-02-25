<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" import="java.util.Locale, java.util.Map" %>
<%@ taglib prefix="dbv" uri="http://dbviews.org"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
  Locale locale = (Locale)session.getAttribute("locale");
  String language = locale.getLanguage();
%>
<html lang="<%=language%>">
  <head>
    <title><dbv:i18n key="title"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link type="text/css" rel="stylesheet" href="/dbviews/css/ui/jquery-ui.min.css"/>
    <script type="text/javascript" src="/dbviews/js/jquery.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/ui/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/dbviews.i18n.js.jsp"></script>
    <script type="text/javascript" src="/dbviews/js/dbviews.util.js"></script>
    <script type="text/javascript" src="/dbviews/js/dbviews.core.js"></script>
    <script type="text/javascript" src="/dbviews/js/dbviews.dialogs.js"></script>
    <script type="text/javascript">
      $(document).ready(function() {
        $.get('/dbviews-api/user/block/${it.blockId}' + window.location.search, {}, function(block, textStatus) {
          buildItem(block, '#content', true, true);
        }).error(function(jqXHR) {
          dlg.alert(jqXHR.statusText);
          $('.loading').removeClass('loading');
        });
      });
    </script>
  </head>
  <body>
    <div id="content"><div class="loading"><span class="loading-modal" title="<dbv:i18n key="loading_please_wait"/>"></span></div></div>
  </body>
</html>