package org.dbviews.api.vo;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.dbviews.api.database.Connector;
import org.dbviews.model.DbvConnection;
import org.dbviews.model.DbvHtmlBlock;

public class HtmlBlock extends Item {
  private final static Logger logger = Logger.getLogger(HtmlBlock.class.getName());

  public HtmlBlock(DbvHtmlBlock b, Map<String, String> args, Map<Integer, String> filter,
                   Map<Integer, Map<String, String>> options, Map<Integer, String> sortby, String focuson) {
    super(b.getDbvView().getDbvConnection());
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

  @Override
  public void fetchFromDatabase(Integer offsetRow, Integer countRows, boolean fetchRows) {
    Connection con = null;
    try {
      DbvConnection dbvConn = getDbvConnection();
      con = Connector.getConnection(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
      con.setReadOnly(true);
      fetchFromDatabase(con, offsetRow, countRows, fetchRows);
      headers = new ArrayList<Header>();
      for (Map.Entry<Integer, Map<String, Object>> e : columnMap.entrySet()) {
        Integer id = e.getKey();
        Map<String, Object> attrs = e.getValue();
        headers.add(new Header(id, (String) attrs.get("ColumnName"), (Integer) attrs.get("ColumnType")));
      }
    } catch (Exception e) {
      logger.severe(e.getMessage());
      logger.severe(getQuery());
    } finally {
      Connector.relres(con);
    }
  }

  public static Item getInstance(DbvHtmlBlock b, Map<String, String> args, boolean fetchFromDatabase,
                                 boolean fetchRows) {
    Item item = new HtmlBlock(b, args, null, null, null, null);
    if (fetchFromDatabase)
      item.fetchFromDatabase(1, Integer.MAX_VALUE - 1, fetchRows);
    return item;
  }

  public String getType() {
    return "block";
  }
}
