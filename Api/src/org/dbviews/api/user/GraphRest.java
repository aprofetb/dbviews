package org.dbviews.api.user;

import java.util.Map;
import java.util.logging.Logger;

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

import org.apache.commons.lang.StringUtils;

import org.codehaus.jackson.map.ObjectMapper;

import org.codehaus.jackson.map.type.TypeFactory;

import org.dbviews.api.ServiceBase;
import org.dbviews.api.vo.Graph;
import org.dbviews.api.vo.Item;
import org.dbviews.commons.utils.SecUtils;
import org.dbviews.model.DbvGraph;
import org.dbviews.model.DbvView;

@Path("user/graph")
@RolesAllowed("valid-users")
public class GraphRest
  extends ServiceBase
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
  public Response getGraph(@PathParam("graphId") Integer graphId, 
                           @QueryParam("args") String args, 
                           @QueryParam("filter") String filter, 
                           @QueryParam("options") String options, 
                           @QueryParam("focuson") String focuson,
                           @QueryParam("paqp") @DefaultValue("false") Boolean paqp)
  {
    DbvGraph g = dbViewsEJB.getDbvGraphFindById(graphId);
    if (g == null)
      return Response.status(Response.Status.NOT_FOUND).build();
    DbvView dbvView = g.getDbvView();
    if (!SecUtils.hasAccess(dbvView.getAuthPrincipals()))
      return Response.status(Response.Status.UNAUTHORIZED).build();

    ObjectMapper om = new ObjectMapper();
    Map<String, String> argsMap = null;
    Map<Integer, String> filterMap = null;
    Map<Integer, Map<String, String>> optionsMap = null;
    try
    {
      if (StringUtils.isNotBlank(args))
        argsMap = (Map<String, String>)om.readValue(args, TypeFactory.fromCanonical("java.util.Map<java.lang.String,java.lang.String>"));
      if (paqp)
        argsMap = processAllQueryParams(argsMap, "args,filter,options,focuson,paqp,language");
      if (StringUtils.isNotBlank(filter))
        filterMap = (Map<Integer, String>)om.readValue(filter, TypeFactory.fromCanonical("java.util.Map<java.lang.Integer,java.lang.String>"));
      if (StringUtils.isNotBlank(options))
        optionsMap = (Map<Integer, Map<String, String>>)om.readValue(options, TypeFactory.fromCanonical("java.util.Map<java.lang.Integer,java.util.Map<java.lang.String,java.lang.String>>"));
    }
    catch (Exception e)
    {
      logger.warning(e.getMessage());
    }

    Item item = Graph.getInstance(g, argsMap, filterMap, optionsMap, focuson);
    if (item == null)
      return Response.status(Response.Status.BAD_REQUEST).build();

    return Response.ok(item).build();
  }

  @GET
  @Path("/{graphId}/excel")
  @Produces(MediaType.TEXT_HTML)
  public Response exportToExcel(@PathParam("graphId") Integer graphId,
                                @QueryParam("args") String args,
                                @QueryParam("filter") String filter,
                                @QueryParam("options") String options,
                                @QueryParam("focuson") String focuson,
                                @QueryParam("paqp") @DefaultValue("false") Boolean paqp)
  {
    DbvGraph g = dbViewsEJB.getDbvGraphFindById(graphId);
    if (g == null)
      return Response.status(Response.Status.NOT_FOUND).build();
    DbvView dbvView = g.getDbvView();
    if (!SecUtils.hasAccess(dbvView.getAuthPrincipals()))
      return Response.status(Response.Status.UNAUTHORIZED).build();

    ObjectMapper om = new ObjectMapper();
    Map<String, String> argsMap = null;
    Map<Integer, String> filterMap = null;
    Map<Integer, Map<String, String>> optionsMap = null;
    try
    {
      if (StringUtils.isNotBlank(args))
        argsMap = (Map<String, String>)om.readValue(args, TypeFactory.fromCanonical("java.util.Map<java.lang.String,java.lang.String>"));
      if (paqp)
        argsMap = processAllQueryParams(argsMap, "args,filter,options,focuson,paqp,language");
      if (StringUtils.isNotBlank(filter))
        filterMap = (Map<Integer, String>)om.readValue(filter, TypeFactory.fromCanonical("java.util.Map<java.lang.Integer,java.lang.String>"));
      if (StringUtils.isNotBlank(options))
        optionsMap = (Map<Integer, Map<String, String>>)om.readValue(options, TypeFactory.fromCanonical("java.util.Map<java.lang.Integer,java.util.Map<java.lang.String,java.lang.String>>"));
    }
    catch (Exception e)
    {
      logger.warning(e.getMessage());
    }

    Item item = Graph.getInstance(g, argsMap, filterMap, optionsMap, focuson);
    if (item == null)
      return Response.status(Response.Status.BAD_REQUEST).build();

    return Response.ok(item.getHtmlAsStream()).header("Content-Disposition", String.format("attachment;filename=\"%s.xls\"", item.getLabel())).build();
  }
}
