package org.dbviews.api.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.dbviews.api.database.Discoverer;
import org.dbviews.model.DbvConnection;
import org.dbviews.model.DbvHtmlBlock;

public class HtmlBlock extends Item {
  public HtmlBlock(DbvHtmlBlock b, Map<String, String> args, Map<Integer, String> filter,
                   Map<Integer, Map<String, String>> options, Map<Integer, String> sortby, String focuson) {
    super(b.getDbvView().getDbvConnection());
    headers = new ArrayList<Header>();
    DbvConnection dbvConn = b.getDbvView().getDbvConnection();
    Discoverer disco = new Discoverer(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
    columnMap = disco.getColumns(b.getSqlQuery(), args, true);
    for (Map.Entry<Integer, Map<String, Object>> e : columnMap.entrySet()) {
      Integer id = e.getKey();
      Map<String, Object> attrs = e.getValue();
      headers.add(new Header(id, (String) attrs.get("ColumnName"), (Integer) attrs.get("ColumnType")));
    }
    id = b.getId();
    label = b.getLabel();
    description = b.getDescription();
    index = b.getTabIndex();
    query = b.getSqlQuery();
    this.args = args != null ? args : new HashMap<String, String>();
    this.filter = filter != null ? filter : new HashMap<Integer, String>();
    this.options = options != null ? options : new HashMap<Integer, Map<String, String>>();
    this.sortby = sortby != null ? sortby : new HashMap<Integer, String>();
    this.focuson = focuson;
  }

  public static Item getInstance(DbvHtmlBlock b, Map<String, String> args) {
    Item item = new HtmlBlock(b, args, null, null, null, null);
    item.fetchFromDatabase(1, Integer.MAX_VALUE - 1, true);
    return item;
  }

  public String getType() {
    return "block";
  }
}
