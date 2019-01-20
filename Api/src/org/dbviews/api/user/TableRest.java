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
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang.StringUtils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import org.dbviews.api.ServiceBase;
import org.dbviews.api.vo.Item;
import org.dbviews.api.vo.Table;
import org.dbviews.api.vo.exporters.CsvExporter;
import org.dbviews.api.vo.exporters.HtmlExporter;
import org.dbviews.commons.utils.SecUtils;
import org.dbviews.model.DbvTable;
import org.dbviews.model.DbvView;

@Path("user/table")
@RolesAllowed("valid-users")
public class TableRest
  extends ServiceBase
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
  public Response getTable(@PathParam("tableId") Integer tableId, 
                           @QueryParam("args") String args, 
                           @QueryParam("filter") String filter, 
                           @QueryParam("options") String options, 
                           @QueryParam("offsetRow") @DefaultValue("1") Integer offsetRow, 
                           @QueryParam("countRows") @DefaultValue("20") Integer countRows, 
                           @QueryParam("sortby") String sortby, 
                           @QueryParam("focuson") String focuson,
                           @QueryParam("paqp") @DefaultValue("false") Boolean paqp)
  {
    DbvTable t = dbViewsEJB.getDbvTableFindById(tableId);
    if (t == null)
      return Response.status(Response.Status.NOT_FOUND).build();
    DbvView dbvView = t.getDbvView();
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
      if (paqp)
        argsMap = processAllQueryParams(argsMap, "args,filter,options,offsetRow,countRows,sortby,focuson,paqp,language");
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

    Item item = Table.getInstance(t, argsMap, filterMap, optionsMap, sortbyMap, offsetRow, countRows, focuson, true, true);
    if (item == null)
      return Response.status(Response.Status.BAD_REQUEST).build();

    return Response.ok(item).build();
  }

  @GET
  @Path("/{tableId}/{exporter:(excel|csv)}")
  @Produces({ MediaType.TEXT_HTML, MediaType.TEXT_PLAIN })
  public Response export(@PathParam("tableId") Integer tableId,
                         @PathParam("exporter") String exporter,
                         @QueryParam("args") String args,
                         @QueryParam("filter") String filter,
                         @QueryParam("options") String options,
                         @QueryParam("sortby") String sortby,
                         @QueryParam("focuson") String focuson,
                         @QueryParam("paqp") @DefaultValue("false") Boolean paqp)
  {
    DbvTable t = dbViewsEJB.getDbvTableFindById(tableId);
    if (t == null)
      return Response.status(Response.Status.NOT_FOUND).build();
    DbvView dbvView = t.getDbvView();
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
      if (paqp)
        argsMap = processAllQueryParams(argsMap, "args,filter,options,sortby,focuson,paqp,language");
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

    Item item = Table.getInstance(t, argsMap, filterMap, optionsMap, sortbyMap, 1, Integer.MAX_VALUE - 1, focuson, true, false);
    if (item == null)
      return Response.status(Response.Status.BAD_REQUEST).build();

    StreamingOutput so = null;
    MediaType mediaType = null;
    String fileExtension = null;
    if ("excel".equalsIgnoreCase(exporter)) {
      so = item.getStreamingOutput(HtmlExporter.class);
      mediaType = MediaType.TEXT_HTML_TYPE;
      fileExtension = "xls";
    } else if ("csv".equalsIgnoreCase(exporter)) {
      so = item.getStreamingOutput(CsvExporter.class);
      mediaType = MediaType.TEXT_PLAIN_TYPE;
      fileExtension = "csv";
    } else {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }

    return Response.ok(so, mediaType).header("Content-Disposition", String.format("attachment;filename=\"%s.%s\"", item.getLabel(), fileExtension)).build();
  }
}
