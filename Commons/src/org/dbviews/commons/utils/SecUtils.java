package org.dbviews.commons.utils;

import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Principal;

import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.commons.lang.StringUtils;

import weblogic.security.principal.WLSGroupImpl;

public class SecUtils
{
  public static Subject getSubject()
  {
    AccessControlContext context = AccessController.getContext();
    Subject subject = Subject.getSubject(context);
    if (subject == null)
      throw new AccessControlException("Denied");
    return subject;
  }

  public static Set<Principal> getPrincipals()
  {
    return getSubject().getPrincipals();
  }

  public static Set<Principal> getPrincipals(Class<Principal> c)
  {
    Subject subject = getSubject();
    return subject.getPrincipals(c);
  }

  public static Set<WLSGroupImpl> getWLSGroups()
  {
    Subject subject = getSubject();
    return subject.getPrincipals(WLSGroupImpl.class);
  }

  public static Set<String> getPrincipalsName()
  {
    Set<String> names = new HashSet<String>();
    Set<Principal> principals = getPrincipals();
    for (Principal pal : principals)
      names.add(pal.getName());
    return names;
  }

  public static boolean hasAccess(String authPrincipals)
  {
    if (StringUtils.isBlank(authPrincipals))
      return true;
    String[] authPals = authPrincipals.split("\\s*,\\s*");
    Set<String> principals = getPrincipalsName();
    for (String pal : authPals)
      if (principals.contains(pal))
        return true;
    return false;
  }
}
