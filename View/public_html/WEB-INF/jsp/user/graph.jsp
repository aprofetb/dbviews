<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" import="java.util.Locale, java.util.Map" %>
<%@ taglib prefix="dbv" uri="http://dbviews.org"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
  Locale locale = (Locale)session.getAttribute("locale");
  String language = locale.getLanguage();
%>
<c:set var="ui" value="${empty param.ui ? 'jqueryui' : param.ui}"/>
<html lang="<%=language%>">
  <head>
    <title><dbv:i18n key="title"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link type="text/css" rel="stylesheet" href="/dbviews/css/daterangepicker.css" />
    <script type="text/javascript" src="/dbviews/js/jquery.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/dbviews.i18n.js.jsp"></script>
    <script type="text/javascript" src="/dbviews/js/dbviews.util.js"></script>
    <script type="text/javascript" src="/dbviews/js/dbviews.dialogs.js"></script>
    <script type="text/javascript" src="/dbviews/js/flot/jquery.flot.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/flot/jquery.flot.pie.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/flot/jquery.flot.categories.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/flot/jquery.flot.canvas.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/flot/jquery.flot.time.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/flot/jquery.flot.resize.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/moment.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/daterangepicker.min.js"></script>
    <c:choose>
      <c:when test="${ui == 'jqueryui'}">
        <link type="text/css" rel="stylesheet" href="/dbviews/css/ui/jquery-ui.min.css"/>
        <link type="text/css" rel="stylesheet" href="/dbviews/css/styles.jqueryui.css"/>
        <script type="text/javascript" src="/dbviews/js/ui/jquery-ui.min.js"></script>
        <script type="text/javascript" src="/dbviews/js/dbviews.core.jqueryui.js"></script>
      </c:when>
      <c:when test="${ui == 'bootstrap'}">
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.2/css/all.css">
        <link type="text/css" rel="stylesheet" href="/dbviews/css/styles.bootstrap.css"/>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootbox.js/4.4.0/bootbox.min.js"></script>
        <script type="text/javascript" src="/dbviews/js/dbviews.core.bootstrap.js"></script>
      </c:when>
    </c:choose>
    <script type="text/javascript">
      $(document).ready(function() {
        $.get('/dbviews-api/user/graph/${it.graphId}' + window.location.search, {}, function(graph, textStatus) {
          buildItem(graph, '#content', true, true);
        }).error(function(jqXHR) {
          dlg.alert(jqXHR.statusText);
          $('.loading').removeClass('loading');
        });
      });
    </script>
  </head>
  <body>
    <div id="content" class="ui-widget"><div class="loading"><span class="loading-modal" title="<dbv:i18n key="loading_please_wait"/>"></span></div></div>
  </body>
</html>