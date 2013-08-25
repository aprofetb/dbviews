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

@Path("user/graph")
@RolesAllowed("valid-users")
public class GraphRest
{
  @GET
  @Path("/{graphId}")
  @Produces(MediaType.TEXT_HTML)
  public Response index(@PathParam("graphId") Integer graphId)
  {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("graphId", graphId);
    return Response.ok(new Viewable("/user/graph", map)).build();
  }
}
