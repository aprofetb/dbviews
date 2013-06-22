package org.dbviews.api.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.TreeSet;
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

import org.dbviews.model.DbvConnection;
import org.dbviews.model.DbvTable;
import org.dbviews.model.DbvView;
import org.dbviews.api.EJBClient;
import org.dbviews.api.bean.BeanWrapper;
import org.dbviews.api.database.Discoverer;
import org.dbviews.api.database.ResultSetWrapper;
import org.dbviews.api.vo.Graph;
import org.dbviews.api.vo.Tab;
import org.dbviews.api.vo.Table;
import org.dbviews.model.DbvGraph;

@Path("user/view")
@RolesAllowed("valid-users")
public class ViewRest
  extends EJBClient
{
  private final static Logger logger = Logger.getLogger(ViewRest.class.getName());

  public ViewRest()
    throws NamingException
  {
    super();
  }

  /* @GET
  @Path("/old/{id}")
  @Produces(MediaType.APPLICATION_JSON) */
  public Response getById(@PathParam("id") Integer id)
  {
    DbvView view = dbViewsEJB.getDbvViewFindById(id);
    DbvConnection con = view.getDbvConnection();
    Discoverer disco = new Discoverer(con.getUrl(), con.getUsername(), con.getPassword());
    Map<String, Object> model = new HashMap<String, Object>();
    List<Map<String, Object>> tabs = new ArrayList<Map<String, Object>>();
    model.put("view", BeanWrapper.getInstance(view));
    model.put("tabs", tabs);
    Collections.sort(view.getDbvTableList());
    for (DbvTable t : view.getDbvTableList())
    {
      ResultSetWrapper rows = disco.getRows(t.getSqlQuery());
      Map<String, Object> table = new HashMap<String, Object>();
      table.put("structure", BeanWrapper.getInstance(t));
      table.put("rows", rows);
      tabs.add(table);
    }
    return model == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(model).build();
  }

  @GET
  @Path("/{viewId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getView(@PathParam("viewId") Integer viewId,
                          @QueryParam("args") String args,
                          @QueryParam("filter") String filter,
                          @QueryParam("options") String options,
                          @QueryParam("offsetRow") @DefaultValue("1") Integer offsetRow,
                          @QueryParam("countRows") @DefaultValue("20") Integer countRows,
                          @QueryParam("sortby") String sortby)
  {
    DbvView dbvView = dbViewsEJB.getDbvViewFindById(viewId);
    if (dbvView == null)
      return Response.status(Response.Status.NOT_FOUND).build();

    ObjectMapper om = new ObjectMapper();
    Map<String, String> argsMap = null;
    Map<Integer, String> filterMap = null;
    Map<Integer, Map<String, String>> optionsMap = null;
    Map<Integer, String> sortbyMap = null;
    try
    {
      if (StringUtils.isNotBlank(args))
        argsMap = (Map<String, String>)om.readValue(args, TypeFactory.fromCanonical("java.util.Map<java.lang.String,java.lang.String>"));
      if (StringUtils.isNotBlank(filter))
        filterMap = (Map<Integer, String>)om.readValue(filter, TypeFactory.fromCanonical("java.util.Map<java.lang.Integer,java.lang.String>"));
      if (StringUtils.isNotBlank(options))
        optionsMap = (Map<Integer, Map<String, String>>)om.readValue(options, TypeFactory.fromCanonical("java.util.Map<java.lang.Integer,java.util.Map<java.lang.String,java.lang.String>>"));
      if (StringUtils.isNotBlank(sortby))
        sortbyMap = (Map<Integer, String>)om.readValue(sortby, TypeFactory.fromCanonical("java.util.Map<java.lang.Integer,java.lang.String>"));
    }
    catch (Exception e)
    {
      logger.warning(e.getMessage());
    }

    List tabsList = dbvView.getDbvTableList();
    tabsList.addAll(dbvView.getDbvGraphList());
    Set<Tab> tabs = new TreeSet<Tab>();
    for (Object o : tabsList)
    {
      Tab tab = null;
      if (o instanceof DbvTable)
        tab = Table.getInstance((DbvTable)o, argsMap, filterMap, optionsMap, sortbyMap, offsetRow, countRows, null);
      else if (o instanceof DbvGraph)
        tab = Graph.getInstance((DbvGraph)o, argsMap, filterMap, optionsMap, null);
      if (tab == null)
        return Response.status(Response.Status.BAD_REQUEST).build();
      tabs.add(tab);
    }

    Map<String, Object> view = new HashMap<String, Object>();
    view.put("description", dbvView.getDescription());
    view.put("tabs", tabs);

    return Response.ok(view).build();
  }

  @GET
  @Path("/{viewId}/excel")
  @Produces(MediaType.TEXT_HTML)
  public Response excel(@PathParam("viewId") Integer viewId,
                        @QueryParam("args") String args,
                        @QueryParam("filter") String filter,
                        @QueryParam("options") String options,
                        @QueryParam("sortby") String sortby)
  {
    DbvView view = dbViewsEJB.getDbvViewFindById(viewId);
    if (view == null)
      return Response.status(Response.Status.NOT_FOUND).build();

    ObjectMapper om = new ObjectMapper();
    Map<String, String> argsMap = null;
    Map<Integer, String> filterMap = null;
    Map<Integer, Map<String, String>> optionsMap = null;
    Map<Integer, String> sortbyMap = null;
    try
    {
      if (StringUtils.isNotBlank(args))
        argsMap = (Map<String, String>)om.readValue(args, TypeFactory.fromCanonical("java.util.Map<java.lang.String,java.lang.String>"));
      if (StringUtils.isNotBlank(filter))
        filterMap = (Map<Integer, String>)om.readValue(filter, TypeFactory.fromCanonical("java.util.Map<java.lang.Integer,java.lang.String>"));
      if (StringUtils.isNotBlank(options))
        optionsMap = (Map<Integer, Map<String, String>>)om.readValue(options, TypeFactory.fromCanonical("java.util.Map<java.lang.Integer,java.util.Map<java.lang.String,java.lang.String>>"));
      if (StringUtils.isNotBlank(sortby))
        sortbyMap = (Map<Integer, String>)om.readValue(sortby, TypeFactory.fromCanonical("java.util.Map<java.lang.Integer,java.lang.String>"));
    }
    catch (Exception e)
    {
      logger.warning(e.getMessage());
    }

    List tabsList = view.getDbvTableList();
    tabsList.addAll(view.getDbvGraphList());
    Set<Tab> tabs = new TreeSet<Tab>();
    for (Object o : tabsList)
    {
      Tab tab = null;
      if (o instanceof DbvTable)
        tab = Table.getInstance((DbvTable)o, argsMap, filterMap, optionsMap, sortbyMap, 1, Integer.MAX_VALUE - 1, null);
      else if (o instanceof DbvGraph)
        tab = Graph.getInstance((DbvGraph)o, argsMap, filterMap, optionsMap, null);
      if (tab == null)
        return Response.status(Response.Status.BAD_REQUEST).build();
      tabs.add(tab);
    }

    return Response.ok(Tab.getHtml(tabs)).header("Content-Disposition", String.format("attachment;filename=%s.xls", view.getDescription())).build();
  }
}
