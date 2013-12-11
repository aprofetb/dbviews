package org.dbviews.view.tags;

import java.net.URLEncoder;

import java.text.MessageFormat;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringEscapeUtils;

public class I18n
  extends TagSupport
{
  private final static Logger logger = Logger.getLogger(I18n.class.getName());
  private String key;
  private Object[] args;
  private String escape;

  public int doStartTag()
    throws JspException
  {
    try
    {
      HttpSession session = pageContext.getSession();
      Locale locale = (Locale)session.getAttribute("locale");
      ResourceBundle msg = ResourceBundle.getBundle("org.dbviews.view.resources.Messages", locale);
      JspWriter out = pageContext.getOut();
      String value = msg.containsKey(key) ? args != null && args.length > 0 ? MessageFormat.format(msg.getString(key), args) : msg.getString(key) : String.format("{%s}", key);
      if ("java".equalsIgnoreCase(escape))
        value = StringEscapeUtils.escapeJava(value);
      else if ("javascript".equalsIgnoreCase(escape))
        value = StringEscapeUtils.escapeJavaScript(value);
      else if ("sql".equalsIgnoreCase(escape))
        value = StringEscapeUtils.escapeSql(value);
      else if ("xml".equalsIgnoreCase(escape))
        value = StringEscapeUtils.escapeXml(value);
      else if ("html".equalsIgnoreCase(escape))
        value = StringEscapeUtils.escapeHtml(value);
      else if ("url".equalsIgnoreCase(escape))
        value = URLEncoder.encode(value, "UTF-8");
      out.print(value);
    }
    catch (Exception e)
    {
      logger.warning(e.getMessage());
    }
    return SKIP_BODY;
  }

  public int doAfterBody()
    throws JspException
  {
    return SKIP_BODY;
  }

  public int doEndTag()
  {
    return EVAL_PAGE;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  public String getKey()
  {
    return key;
  }

  public void setArgs(Object[] args)
  {
    this.args = args;
  }

  public Object[] getArgs()
  {
    return args;
  }

  public void setEscape(String escape)
  {
    this.escape = escape;
  }

  public String getEscape()
  {
    return escape;
  }
}
