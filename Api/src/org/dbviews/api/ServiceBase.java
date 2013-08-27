package org.dbviews.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Context;

import org.dbviews.model.DbViewsEJB;

public class ServiceBase
{
  protected static final String EJB_SB_NAME = "java:comp/env/ejb/DbViewsEJB";

  @Resource
  protected DbViewsEJB dbViewsEJB;
  @Context
  protected HttpServletRequest request;

  public ServiceBase()
    throws NamingException
  {
    InitialContext context = new InitialContext();
    dbViewsEJB = (DbViewsEJB)context.lookup(EJB_SB_NAME);
  }

  public Map<String, String> processAllQueryParams(Map<String, String> argsMap, String paramsToSkip)
  {
    if (argsMap == null)
      argsMap = new HashMap<String, String>();
    Map params = request.getParameterMap();
    List<String> p2s = Arrays.asList(paramsToSkip.split(","));
    for (Object k : params.keySet())
    {
      String key = k.toString();
      if (!p2s.contains(key) && !argsMap.containsKey(key))
        argsMap.put(key, request.getParameter(key));
    }
    return argsMap;
  }
}
