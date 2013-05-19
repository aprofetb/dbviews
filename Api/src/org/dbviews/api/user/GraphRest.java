package org.dbviews.api.user;

import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;

import javax.naming.NamingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import org.codehaus.jackson.map.ObjectMapper;

import org.dbviews.api.EJBClient;
import org.dbviews.api.vo.Graph;
import org.dbviews.api.vo.Tab;
import org.dbviews.commons.utils.ParamsMap;
import org.dbviews.model.DbvGraph;

@Path("user/graph")
public class GraphRest
  extends EJBClient
{
  private final static Logger logger = Logger.getLogger(GraphRest.class.getName());

  public GraphRest()
    throws NamingException
  {
    super();
  }

  @GET
  @Path("/{graphId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("valid-users")
  public Response getTable(@PathParam("graphId") Integer graphId, 
                           @QueryParam("args") String args, 
                           @QueryParam("filter") String filter, 
                           @QueryParam("options") String options, 
                           @QueryParam("focuson") String focuson)
  {
    DbvGraph g = dbViewsEJB.getDbvGraphFindById(graphId);
    if (g == null)
      return Response.status(Response.Status.NOT_FOUND).build();

    ObjectMapper om = new ObjectMapper();
    Map<String, String> argsMap = null;
    Map<String, String> filterMap = null;
    Map<String, Map<String, String>> optionsMap = null;
    try
    {
      if (StringUtils.isNotBlank(args))
        argsMap = om.readValue(args, ParamsMap.class);
      if (StringUtils.isNotBlank(filter))
        filterMap = om.readValue(filter, ParamsMap.class);
      if (StringUtils.isNotBlank(options))
        optionsMap = om.readValue(options, Map.class);
    }
    catch (Exception e)
    {
      logger.warning(e.getMessage());
    }
    Tab tab = Graph.getInstance(g, argsMap, filterMap, optionsMap, focuson);
    return tab == null ? Response.status(Response.Status.BAD_REQUEST).build() : Response.ok(tab).build();
  }
}
