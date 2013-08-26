package org.dbviews.api.user;

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

import org.dbviews.model.DbvTable;
import org.dbviews.model.DbvView;
import org.dbviews.api.ServiceBase;
import org.dbviews.api.vo.Graph;
import org.dbviews.api.vo.HtmlBlock;
import org.dbviews.api.vo.Item;
import org.dbviews.api.vo.Table;
import org.dbviews.commons.utils.SecUtils;
import org.dbviews.model.DbvGraph;
import org.dbviews.model.DbvHtmlBlock;

@Path("user/view")
@RolesAllowed("valid-users")
public class ViewRest
  extends ServiceBase
{
  private final static Logger logger = Logger.getLogger(ViewRest.class.getName());

  public ViewRest()
    throws NamingException
  {
    super();
  }

  @GET
  @Path("/{viewId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getView(@PathParam("viewId") Integer viewId,
                          @QueryParam("args") String args,
                          @QueryParam("countRows") @DefaultValue("20") Integer countRows,
                          @QueryParam("paqp") @DefaultValue("false") Boolean paqp)
  {
    DbvView dbvView = dbViewsEJB.getDbvViewFindById(viewId);
    if (dbvView == null)
      return Response.status(Response.Status.NOT_FOUND).build();
    if (!SecUtils.hasAccess(dbvView.getAuthPrincipals()))
      return Response.status(Response.Status.UNAUTHORIZED).build();

    ObjectMapper om = new ObjectMapper();
    Map<String, String> argsMap = null;
    try
    {
      if (StringUtils.isNotBlank(args))
        argsMap = (Map<String, String>)om.readValue(args, TypeFactory.fromCanonical("java.util.Map<java.lang.String,java.lang.String>"));
      if (paqp)
        processAllQueryParams(argsMap, "args,countRows,pap");
    }
    catch (Exception e)
    {
      logger.warning(e.getMessage());
    }

    List itemsList = dbvView.getDbvTableList();
    itemsList.addAll(dbvView.getDbvGraphList());
    itemsList.addAll(dbvView.getDbvHtmlBlockList());
    Set<Item> items = new TreeSet<Item>();
    for (Object o : itemsList)
    {
      Item item = null;
      if (o instanceof DbvTable)
        item = Table.getInstance((DbvTable)o, argsMap, null, null, null, 1, countRows, null);
      else if (o instanceof DbvGraph)
        item = Graph.getInstance((DbvGraph)o, argsMap, null, null, null);
      else if (o instanceof DbvHtmlBlock)
        item = HtmlBlock.getInstance((DbvHtmlBlock)o, argsMap, null, null, null, 1, Integer.MAX_VALUE - 1, null);
      if (item == null)
        return Response.status(Response.Status.BAD_REQUEST).build();
      items.add(item);
    }

    Map<String, Object> view = new HashMap<String, Object>();
    view.put("description", dbvView.getDescription());
    view.put("jquiPlugin", dbvView.getJquiPlugin());
    view.put("jquiPluginOptions", dbvView.getJquiPluginOptions());
    view.put("items", items);

    return Response.ok(view).build();
  }

  @GET
  @Path("/{viewId}/excel")
  @Produces(MediaType.TEXT_HTML)
  public Response exportToExcel(@PathParam("viewId") Integer viewId,
                                @QueryParam("args") String args,
                                @QueryParam("paqp") @DefaultValue("false") Boolean paqp)
  {
    DbvView dbvView = dbViewsEJB.getDbvViewFindById(viewId);
    if (dbvView == null)
      return Response.status(Response.Status.NOT_FOUND).build();
    if (!SecUtils.hasAccess(dbvView.getAuthPrincipals()))
      return Response.status(Response.Status.UNAUTHORIZED).build();

    ObjectMapper om = new ObjectMapper();
    Map<String, String> argsMap = null;
    try
    {
      if (StringUtils.isNotBlank(args))
        argsMap = (Map<String, String>)om.readValue(args, TypeFactory.fromCanonical("java.util.Map<java.lang.String,java.lang.String>"));
      if (paqp)
        processAllQueryParams(argsMap, "args,countRows,pap");
    }
    catch (Exception e)
    {
      logger.warning(e.getMessage());
    }

    List itemsList = dbvView.getDbvTableList();
    itemsList.addAll(dbvView.getDbvGraphList());
    itemsList.addAll(dbvView.getDbvHtmlBlockList());
    Set<Item> items = new TreeSet<Item>();
    for (Object o : itemsList)
    {
      Item item = null;
      if (o instanceof DbvTable)
        item = Table.getInstance((DbvTable)o, argsMap, null, null, null, 1, Integer.MAX_VALUE - 1, null);
      else if (o instanceof DbvGraph)
        item = Graph.getInstance((DbvGraph)o, argsMap, null, null, null);
      if (item == null)
        return Response.status(Response.Status.BAD_REQUEST).build();
      items.add(item);
    }

    return Response.ok(Item.getHtml(items)).header("Content-Disposition", String.format("attachment;filename=%s.xls", dbvView.getDescription())).build();
  }
}
