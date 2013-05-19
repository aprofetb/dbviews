package org.dbviews.api.admin;

import javax.naming.NamingException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.dbviews.model.DbvConnection;
import org.dbviews.api.EJBClient;
import org.dbviews.api.bean.BeanWrapper;

@Path("admin/connection")
public class ConnectionRest
  extends EJBClient
{
  public ConnectionRest()
    throws NamingException
  {
    super();
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getById(@Context
    SecurityContext sc, @PathParam("id")
    Integer id)
  {
    if (!sc.isUserInRole("admins"))
      return Response.status(Response.Status.UNAUTHORIZED).build();

    DbvConnection con = dbViewsEJB.getDbvConnectionFindById(id);
    BeanWrapper model = new BeanWrapper(con);
    return model == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(model).build();
  }

  @PUT
  @Path("/new")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response put(@Context
    SecurityContext sc, DbvConnection conn)
  {
    if (!sc.isUserInRole("admins"))
      return Response.status(Response.Status.UNAUTHORIZED).build();

    DbvConnection model = dbViewsEJB.persistDbvConnection(conn);
    return Response.status(Response.Status.CREATED).entity(model).build();
  }
}
