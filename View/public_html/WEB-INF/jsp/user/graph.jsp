<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" import="java.util.Locale, java.util.Map" %>
<%@ taglib prefix="dbv" uri="http://dbviews.org"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
  Locale locale = request.getLocale();
  String language = locale.getLanguage();
  String country = locale.getCountry();
%>
<html lang="<%=language%>">
  <head>
    <title>Database Views</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link type="text/css" rel="stylesheet" href="/dbviews/css/ui/jquery-ui.min.css"/>
    <link type="text/css" rel="stylesheet" href="/dbviews/css/styles.css"/>
    <script type="text/javascript" src="/dbviews/js/jquery.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/ui/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/dbviews.i18n.js.jsp"></script>
    <script type="text/javascript" src="/dbviews/js/dbviews.core.js"></script>
    <script type="text/javascript" src="/dbviews/js/dbviews.util.js"></script>
    <script type="text/javascript" src="/dbviews/js/dbviews.dialogs.js"></script>
    <script type="text/javascript" src="/dbviews/js/jquery.flot.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/jquery.flot.pie.js"></script>
    <!--<script type="text/javascript" src="/dbviews/js/jquery.flot.pie.min.js"></script>-->
    <script type="text/javascript" src="/dbviews/js/jquery.flot.categories.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/jquery.flot.canvas.min.js"></script>
    <script type="text/javascript" src="/dbviews/js/jquery.flot.time.min.js"></script>
    <script type="text/javascript">
      $(document).ready(function() {
        $.get('/api/user/graph/${it.graphId}', { "args": "${it.args}", "filter": "${it.filter}" }, function(graph, textStatus) {
          $('#content').html(buildSection(graph));
        }).error(function() {
          alert(msg['alert_error']);
        });
      });
    </script>
  </head>
  <body>
    <div id="content"><dbv:i18n key="loading_please_wait"/></div>
  </body>
</html>