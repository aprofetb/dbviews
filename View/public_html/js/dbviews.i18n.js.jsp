<%@ page contentType="text/javascript;charset=UTF-8" import="java.util.ResourceBundle" %>
<% if (false) { %><script type="text/javascript"><% } %>
var msg = {};
<%
ResourceBundle msg = ResourceBundle.getBundle("org.dbviews.view.resources.Messages");
for (String key : msg.keySet())
{
%>msg['<%=key%>'] = "<%=msg.getString(key)%>";
<%
}
%>
<% if (false) { %></script><% } %>
