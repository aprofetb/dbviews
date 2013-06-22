package org.dbviews.api.admin;

import javax.annotation.security.RolesAllowed;

import javax.naming.NamingException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dbviews.model.DbvView;
import org.dbviews.api.EJBClient;
import org.dbviews.api.bean.BeanWrapper;

@Path("admin/view")
@RolesAllowed("admins")
public class ViewRest
  extends EJBClient
{
  public ViewRest()
    throws NamingException
  {
    super();
  }

  @GET
  @Path("/{viewId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getById(@PathParam("viewId") Integer viewId)
  {
    DbvView view = dbViewsEJB.getDbvViewFindById(viewId);
    BeanWrapper model = new BeanWrapper(view);
    return model == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(model).build();
  }

  @PUT
  @Path("/new")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response put()
  {
    return Response.ok().build();
  }
}
