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
                           @QueryParam("paqp") @DefaultValue("false") Boolean paqp)
  {
    DbvHtmlBlock b = dbViewsEJB.getDbvHtmlBlockFindById(blockId);
    if (b == null)
      return Response.status(Response.Status.NOT_FOUND).build();
    DbvView dbvView = b.getDbvView();
    if (!SecUtils.hasAccess(dbvView.getAuthPrincipals()))
      return Response.status(Response.Status.UNAUTHORIZED).build();

    ObjectMapper om = new ObjectMapper();
    Map<String, String> argsMap = null;
    try
    {
      if (StringUtils.isNotBlank(args))
        argsMap = (Map<String, String>)om.readValue(args, TypeFactory.fromCanonical("java.util.Map<java.lang.String,java.lang.String>"));
      if (paqp)
        argsMap = processAllQueryParams(argsMap, "args,paqp,language");
    }
    catch (Exception e)
    {
      logger.warning(e.getMessage());
    }

    Item item = HtmlBlock.getInstance(b, argsMap);
    if (item == null)
      return Response.status(Response.Status.BAD_REQUEST).build();

    return Response.ok(item).build();
  }
}
