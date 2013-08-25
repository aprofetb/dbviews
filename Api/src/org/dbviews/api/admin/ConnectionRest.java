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

import org.dbviews.model.DbvConnection;
import org.dbviews.api.ServiceBase;
import org.dbviews.api.wrappers.BeanWrapper;

@Path("admin/connection")
@RolesAllowed("admins")
public class ConnectionRest
  extends ServiceBase
{
  public ConnectionRest()
    throws NamingException
  {
    super();
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getById(@PathParam("id") Integer id)
  {
    DbvConnection con = dbViewsEJB.getDbvConnectionFindById(id);
    BeanWrapper model = new BeanWrapper(con);
    return model == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(model).build();
  }

  @PUT
  @Path("/new")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response put(DbvConnection conn)
  {
    DbvConnection model = dbViewsEJB.persistDbvConnection(conn);
    return Response.status(Response.Status.CREATED).entity(model).build();
  }
}
