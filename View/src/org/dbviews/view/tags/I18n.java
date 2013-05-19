package org.dbviews.view.tags;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import java.util.*;
import java.util.logging.Logger;

public class I18n
  extends TagSupport
{
  private final static Logger logger = Logger.getLogger(I18n.class.getName());
  private String key = "";

  public int doStartTag()
    throws JspException
  {
    try
    {
      ResourceBundle msg = ResourceBundle.getBundle("org.dbviews.view.resources.Messages");
      JspWriter out = pageContext.getOut();
      out.print(msg.containsKey(key) ? msg.getString(key) : String.format("{%s}", key));
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
}
