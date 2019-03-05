package org.dbviews.api.vo;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.dbviews.api.database.Connector;
import org.dbviews.model.DbvConnection;
import org.dbviews.model.DbvTable;
import org.dbviews.model.DbvTableField;

public class Table extends Item {
  private final static Logger logger = Logger.getLogger(Table.class.getName());

  String filterPosition;
  String toolbarPosition;
  protected Map<String, Header> customHeaders;

  public Table(DbvTable t, Map<String, String> args, Map<String, List<String>> filter,
               Map<String, Map<String, String>> options, Map<String, String> sortby, String focuson) {
    super(t.getDbvView().getDbvConnection());
    if (t.getDbvTableFieldList().size() > 0) {
      customHeaders = new HashMap<String, Header>();
      for (DbvTableField field : t.getDbvTableFieldList())
        customHeaders.put(field.getColumnName(), new Header(field));
    }
    id = t.getId();
    label = t.getLabel();
    description = t.getDescription();
    index = t.getTabIndex();
    query = t.getSqlQuery();
    queryIndex = t.getSqlQueryIndex();
    csvSeparator = t.getCsvSeparator();
    this.args = args != null ? args : new HashMap<String, String>();
    this.filter = filter != null ? filter : new HashMap<String, List<String>>();
    this.options = options != null ? options : new HashMap<String, Map<String, String>>();
    this.sortby = sortby != null ? sortby : new HashMap<String, String>();
    this.focuson = focuson;
    this.filterPosition = t.getFilterPosition();
    this.toolbarPosition = t.getToolbarPosition();
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
        String columnName = (String) attrs.get("ColumnName");
        Header header = null;
        if (customHeaders != null) {
          Header customHeader = customHeaders.get(columnName);
          if (customHeader != null)
            header = new Header(id, customHeader);
        }
        if (header == null)
          header = new Header(id, columnName, (Integer) attrs.get("ColumnType"));
        headers.add(header);
      }
    } catch (Exception e) {
      logger.severe(e.getMessage());
      logger.severe(getQuery());
    } finally {
      Connector.relres(con);
    }
  }

  public static Item getInstance(DbvTable t, Map<String, String> args, Map<String, List<String>> filter,
                                 Map<String, Map<String, String>> options, Map<String, String> sortby,
                                 Integer offsetRow, Integer countRows, String focuson, boolean fetchFromDatabase,
                                 boolean fetchRows) {
    Table item = new Table(t, args, filter, options, sortby, focuson);
    item.setOffsetRow(offsetRow);
    item.setCountRows(countRows);
    if (fetchFromDatabase)
      item.fetchFromDatabase(offsetRow, countRows, fetchRows);
    return item;
  }

  public String getType() {
    return "table";
  }

  public void setFilterPosition(String filterPosition) {
    this.filterPosition = filterPosition;
  }

  public String getFilterPosition() {
    return filterPosition;
  }

  public void setToolbarPosition(String toolbarPosition) {
    this.toolbarPosition = toolbarPosition;
  }

  public String getToolbarPosition() {
    return toolbarPosition;
  }
}
