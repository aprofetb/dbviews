package org.dbviews.api;

import javax.annotation.Resource;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.dbviews.model.DbViewsEJB;

public class EJBClient
{
  @Resource
  protected DbViewsEJB dbViewsEJB;

  public EJBClient()
    throws NamingException
  {
    InitialContext context = new InitialContext();
    dbViewsEJB = (DbViewsEJB)context.lookup("java:comp/env/ejb/DbViewsEJB");
  }
}
