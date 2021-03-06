package org.dbviews.view.admin;

import com.sun.jersey.api.view.Viewable;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.RolesAllowed;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringEscapeUtils;

@Path("admin/connection")
@RolesAllowed("admins")
public class ConnectionRest
{
  @GET
  @Path("/{connectionId}")
  @Produces(MediaType.TEXT_HTML)
  public Response index(@PathParam("connectionId") Integer connectionId,
                        @QueryParam("args") String args,
                        @QueryParam("filter") String filter,
                        @QueryParam("countRows") @DefaultValue("20") Integer countRows)
  {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("connectionId", connectionId);
    if (args != null)
      map.put("args", StringEscapeUtils.escapeJavaScript(args));
    if (filter != null)
      map.put("filter", StringEscapeUtils.escapeJavaScript(filter));
    map.put("countRows", countRows);
    return Response.ok(new Viewable("/admin/connection", map)).build();
  }
}
