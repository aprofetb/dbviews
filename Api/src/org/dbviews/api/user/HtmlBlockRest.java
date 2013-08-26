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

import org.dbviews.api.vo.HtmlBlock;
import org.dbviews.api.vo.Item;

import org.dbviews.commons.utils.SecUtils;
import org.dbviews.model.DbvHtmlBlock;
import org.dbviews.model.DbvView;

@Path("user/block")
@RolesAllowed("valid-users")
public class HtmlBlockRest
  extends ServiceBase
{
  private final static Logger logger = Logger.getLogger(HtmlBlockRest.class.getName());

  public HtmlBlockRest()
    throws NamingException
  {
    super();
  }

  @GET
  @Path("/{blockId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getBlock(@PathParam("blockId") Integer blockId, 
                           @QueryParam("args") String args, 
                           @QueryParam("filter") String filter, 
                           @QueryParam("options") String options, 
                           @QueryParam("offsetRow") @DefaultValue("1") Integer offsetRow, 
                           @QueryParam("countRows") @DefaultValue("20") Integer countRows, 
                           @QueryParam("sortby") String sortby, 
                           @QueryParam("focuson") String focuson)
  {
    DbvHtmlBlock b = dbViewsEJB.getDbvHtmlBlockFindById(blockId);
    if (b == null)
      return Response.status(Response.Status.NOT_FOUND).build();
    DbvView dbvView = b.getDbvView();
    if (!SecUtils.hasAccess(dbvView.getAuthPrincipals()))
      return Response.status(Response.Status.UNAUTHORIZED).build();

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

    Item item = HtmlBlock.getInstance(b, argsMap, filterMap, optionsMap, sortbyMap, offsetRow, countRows, focuson);
    if (item == null)
      return Response.status(Response.Status.BAD_REQUEST).build();

    return Response.ok(item).build();
  }

  @GET
  @Path("/{blockId}/excel")
  @Produces(MediaType.TEXT_HTML)
  public Response exportToExcel(@PathParam("blockId") Integer blockId,
                                @QueryParam("args") String args,
                                @QueryParam("filter") String filter,
                                @QueryParam("options") String options,
                                @QueryParam("sortby") String sortby,
                                @QueryParam("focuson") String focuson)
  {
    DbvHtmlBlock b = dbViewsEJB.getDbvHtmlBlockFindById(blockId);
    if (b == null)
      return Response.status(Response.Status.NOT_FOUND).build();
    DbvView dbvView = b.getDbvView();
    if (!SecUtils.hasAccess(dbvView.getAuthPrincipals()))
      return Response.status(Response.Status.UNAUTHORIZED).build();

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

    Item item = HtmlBlock.getInstance(b, argsMap, filterMap, optionsMap, sortbyMap, 1, Integer.MAX_VALUE - 1, focuson);
    if (item == null)
      return Response.status(Response.Status.BAD_REQUEST).build();

    return Response.ok(item.getHtml()).header("Content-Disposition", String.format("attachment;filename=\"%s.xls\"", item.getLabel())).build();
  }
}
