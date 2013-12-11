<%@ page contentType="text/javascript;charset=UTF-8"
         import="java.util.ResourceBundle,
                 java.util.Locale,
                 org.apache.commons.lang.StringEscapeUtils" %>
<% if (false) { %><script type="text/javascript"><% } %>
var msg = {};
<%
  Locale locale = (Locale)session.getAttribute("locale");
  if (locale == null)
    locale = request.getLocale();
  ResourceBundle msg = ResourceBundle.getBundle("org.dbviews.view.resources.Messages", locale);
  for (String key : msg.keySet())
  {
%>msg['<%=key%>'] = "<%=StringEscapeUtils.escapeJavaScript(msg.getString(key))%>";
<%
  }
%>
<% if (false) { %></script><% } %>
