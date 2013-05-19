package org.dbviews.view.user;

import com.sun.jersey.api.view.Viewable;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.RolesAllowed;

import javax.naming.NamingException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringEscapeUtils;

@Path("user/view")
@RolesAllowed("admins")
public class ViewRest
{
  public ViewRest()
    throws NamingException
  {
    super();
  }

  @GET
  @Path("/{viewId}")
  @Produces(MediaType.TEXT_HTML)
  public Response index(@PathParam("viewId") Integer viewId,
                        @QueryParam("args") String args,
                        @QueryParam("filter") String filter,
                        @QueryParam("countRows") @DefaultValue("20") Integer countRows)
  {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("viewId", viewId);
    if (args != null)
      map.put("args", StringEscapeUtils.escapeJavaScript(args));
    if (filter != null)
      map.put("filter", StringEscapeUtils.escapeJavaScript(filter));
    map.put("countRows", countRows);
    return Response.ok(new Viewable("/view", map)).build();
  }
}
