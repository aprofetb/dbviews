package org.dbviews.api.admin;

import javax.annotation.security.RolesAllowed;

import javax.naming.NamingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dbviews.api.EJBClient;

@Path("admin/cache")
@RolesAllowed("admins")
public class CacheRest
  extends EJBClient
{
  public CacheRest()
    throws NamingException
  {
    super();
  }

  @GET
  @Path("/clear")
  @Produces(MediaType.TEXT_HTML)
  public Response clear()
  {
    dbViewsEJB.clearCache();
    return Response.ok("Cache cleared successfully").build();
  }
}
