package org.dbviews.api.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.dbviews.api.database.Discoverer;
import org.dbviews.model.DbvConnection;
import org.dbviews.model.DbvHtmlBlock;

public class HtmlBlock
  extends Item
{
  public HtmlBlock(DbvHtmlBlock t, Map<String, String> args, Map<Integer, String> filter, Map<Integer, Map<String, String>> options, Map<Integer, String> sortby, String focuson)
  {
    headers = new ArrayList<Header>();
    DbvConnection dbvConn = t.getDbvView().getDbvConnection();
    Discoverer disco = new Discoverer(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
    columnMap = disco.getColumns(t.getSqlQuery(), args, true);
    for (Map.Entry<Integer, Map<String, Object>> e : columnMap.entrySet())
    {
      Integer id = e.getKey();
      Map<String, Object> attrs = e.getValue();
      headers.add(new Header(id, (String)attrs.get("ColumnName"), (Integer)attrs.get("ColumnType")));
    }
    id = t.getId();
    label = t.getLabel();
    description = t.getDescription();
    index = t.getTabIndex();
    query = t.getSqlQuery();
    this.args = args != null ? args : new HashMap<String, String>();
    this.filter = filter != null ? filter : new HashMap<Integer, String>();
    this.options = options != null ? options : new HashMap<Integer, Map<String, String>>();
    this.sortby = sortby != null ? sortby : new HashMap<Integer, String>();
    this.focuson = focuson;
  }

  public static Item getInstance(DbvHtmlBlock t, Map<String, String> args)
  {
    DbvConnection dbvConn = t.getDbvView().getDbvConnection();
    Item block = new HtmlBlock(t, args, null, null, null, null);
    return getInstance(block, dbvConn, args, null, null, null, 1, Integer.MAX_VALUE - 1);
  }

  public String getType()
  {
    return "block";
  }
}
