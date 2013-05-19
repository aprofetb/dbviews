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

import org.dbviews.model.DbvTable;

import org.dbviews.api.EJBClient;

import org.dbviews.api.vo.Tab;

import org.dbviews.api.vo.Table;
import org.dbviews.commons.utils.ParamsMap;

@Path("user/table")
public class TableRest
  extends EJBClient
{
  private final static Logger logger = Logger.getLogger(TableRest.class.getName());

  public TableRest()
    throws NamingException
  {
    super();
  }

  @GET
  @Path("/{tableId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("valid-users")
  public Response getTable(@PathParam("tableId") Integer tableId, 
                           @QueryParam("args") String args, 
                           @QueryParam("filter") String filter, 
                           @QueryParam("options") String options, 
                           @QueryParam("offsetRow") @DefaultValue("1") Integer offsetRow, 
                           @QueryParam("countRows") @DefaultValue("20") Integer countRows, 
                           @QueryParam("sortby") String sortby, 
                           @QueryParam("focuson") String focuson)
  {
    DbvTable t = dbViewsEJB.getDbvTableFindById(tableId);
    if (t == null)
      return Response.status(Response.Status.NOT_FOUND).build();

    ObjectMapper om = new ObjectMapper();
    Map<String, String> argsMap = null;
    Map<String, String> filterMap = null;
    Map<String, Map<String, String>> optionsMap = null;
    Map<String, String> sortbyMap = null;
    try
    {
      if (StringUtils.isNotBlank(args))
        argsMap = om.readValue(args, ParamsMap.class);
      if (StringUtils.isNotBlank(filter))
        filterMap = om.readValue(filter, ParamsMap.class);
      if (StringUtils.isNotBlank(options))
        optionsMap = om.readValue(options, Map.class);
      if (StringUtils.isNotBlank(sortby))
        sortbyMap = om.readValue(sortby, ParamsMap.class);
    }
    catch (Exception e)
    {
      logger.warning(e.getMessage());
    }
    Tab tab = Table.getInstance(t, argsMap, filterMap, optionsMap, sortbyMap, offsetRow, countRows, focuson);
    return tab == null ? Response.status(Response.Status.BAD_REQUEST).build() : Response.ok(tab).build();
  }

  @GET
  @Path("/{tableId}/excel")
  @Produces(MediaType.TEXT_HTML)
  @RolesAllowed("valid-users")
  public Response excel(@PathParam("tableId") Integer tableId,
                        @QueryParam("args") String args,
                        @QueryParam("filter") String filter,
                        @QueryParam("options") String options,
                        @QueryParam("sortby") String sortby,
                        @QueryParam("focuson") String focuson)
  {
    DbvTable t = dbViewsEJB.getDbvTableFindById(tableId);
    if (t == null)
      return Response.status(Response.Status.NOT_FOUND).build();

    ObjectMapper om = new ObjectMapper();
    Map<String, String> argsMap = null;
    Map<String, String> filterMap = null;
    Map<String, Map<String, String>> optionsMap = null;
    Map<String, String> sortbyMap = null;
    try
    {
      if (StringUtils.isNotBlank(args))
        argsMap = om.readValue(args, ParamsMap.class);
      if (StringUtils.isNotBlank(filter))
        filterMap = om.readValue(filter, ParamsMap.class);
      if (StringUtils.isNotBlank(options))
        optionsMap = om.readValue(options, Map.class);
      if (StringUtils.isNotBlank(sortby))
        sortbyMap = om.readValue(sortby, ParamsMap.class);
    }
    catch (Exception e)
    {
      logger.warning(e.getMessage());
    }
    Tab tab = Table.getInstance(t, argsMap, filterMap, optionsMap, sortbyMap, 1, Integer.MAX_VALUE - 1, focuson);
    if (tab == null)
      return Response.status(Response.Status.BAD_REQUEST).build();

    return Response.ok(tab.getHtml()).header("Content-Disposition", String.format("attachment;filename=%s.xls", tab.getLabel())).build();
  }
}
