package org.dbviews.view.user;

import com.sun.jersey.api.view.Viewable;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.RolesAllowed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("user/table")
@RolesAllowed("valid-users")
public class TableRest
{
  @GET
  @Path("/{tableId}")
  @Produces(MediaType.TEXT_HTML)
  public Response index(@PathParam("tableId") Integer tableId)
  {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("tableId", tableId);
    return Response.ok(new Viewable("/user/table", map)).build();
  }
}
