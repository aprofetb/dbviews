package org.dbviews.view.filters;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpSession;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.dbviews.commons.Constants;

public class DbvContainerRequestFilter
  implements ContainerRequestFilter
{
  @Context
  protected HttpServletRequest request;
  @Context
  protected SecurityContext securityContext;

  public ContainerRequest filter(ContainerRequest containerRequest)
  {
    // check for user authentication
    if (securityContext.getUserPrincipal() == null)
      throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).build());

    // check locale
    checkLocale();

    return containerRequest;
  }

  public void checkLocale()
  {
    HttpSession session = request.getSession();
    Locale locale = (Locale)session.getAttribute("locale");
    String language = request.getParameter("language");
    if (locale == null || language != null)
      session.setAttribute("locale", language != null && Constants.LANGUAGES.containsKey(language) ? new Locale(language) : request.getLocale());
  }
}
