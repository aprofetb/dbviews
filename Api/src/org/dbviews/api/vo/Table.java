package org.dbviews.api.vo;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.dbviews.api.database.Connector;
import org.dbviews.model.DbvConnection;
import org.dbviews.model.DbvTable;
import org.dbviews.model.DbvTableField;

public class Table extends Item {
  private final static Logger logger = Logger.getLogger(Table.class.getName());

  String filterPosition;
  String toolbarPosition;

  public Table(DbvTable t, Map<String, String> args, Map<Integer, String> filter,
               Map<Integer, Map<String, String>> options, Map<Integer, String> sortby, String focuson) {
    super(t.getDbvView().getDbvConnection());
    if (t.getDbvTableFieldList().size() > 0) {
      //TODO: merge customized fields with query columns
      headers = new ArrayList<Header>();
      columnMap = new TreeMap<Integer, Map<String, Object>>();
      for (DbvTableField field : t.getDbvTableFieldList()) {
        headers.add(new Header(field));
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put("CatalogName", "");
        attrs.put("ColumnClassName", "");
        attrs.put("ColumnDisplaySize", 0);
        attrs.put("ColumnLabel", "");
        attrs.put("ColumnName", field.getColumnName());
        attrs.put("ColumnType", field.getType());
        attrs.put("ColumnTypeName", "");
        attrs.put("Precision", 0);
        attrs.put("Scale", 0);
        attrs.put("SchemaName", "");
        attrs.put("TableName", "");
        columnMap.put(field.getId(), attrs);
      }
    }
    id = t.getId();
    label = t.getLabel();
    description = t.getDescription();
    index = t.getTabIndex();
    query = t.getSqlQuery();
    queryIndex = t.getSqlQueryIndex();
    csvSeparator = t.getCsvSeparator();
    this.args = args != null ? args : new HashMap<String, String>();
    this.filter = filter != null ? filter : new HashMap<Integer, String>();
    this.options = options != null ? options : new HashMap<Integer, Map<String, String>>();
    this.sortby = sortby != null ? sortby : new HashMap<Integer, String>();
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
        headers.add(new Header(id, (String) attrs.get("ColumnName"), (Integer) attrs.get("ColumnType")));
      }
    } catch (Exception e) {
      logger.severe(e.getMessage());
      logger.severe(getQuery());
    } finally {
      Connector.relres(con);
    }
  }

  public static Item getInstance(DbvTable t, Map<String, String> args, Map<Integer, String> filter,
                                 Map<Integer, Map<String, String>> options, Map<Integer, String> sortby,
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
