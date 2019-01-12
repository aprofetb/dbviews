package org.dbviews.api.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.TreeMap;

import org.dbviews.api.database.Discoverer;
import org.dbviews.model.DbvConnection;
import org.dbviews.model.DbvTable;
import org.dbviews.model.DbvTableField;

public class Table extends Item {
  String filterPosition;

  public Table(DbvTable t, Map<String, String> args, Map<Integer, String> filter,
               Map<Integer, Map<String, String>> options, Map<Integer, String> sortby, String focuson) {
    super(t.getDbvView().getDbvConnection());
    headers = new ArrayList<Header>();
    if (t.getDbvTableFieldList().size() > 0) {
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
    } else {
      DbvConnection dbvConn = t.getDbvView().getDbvConnection();
      Discoverer disco = new Discoverer(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
      columnMap = disco.getColumns(t.getSqlQuery(), args, true);
      for (Map.Entry<Integer, Map<String, Object>> e : columnMap.entrySet()) {
        Integer id = e.getKey();
        Map<String, Object> attrs = e.getValue();
        headers.add(new Header(id, (String) attrs.get("ColumnName"), (Integer) attrs.get("ColumnType")));
      }
    }
    id = t.getId();
    label = t.getLabel();
    description = t.getDescription();
    index = t.getTabIndex();
    query = t.getSqlQuery();
    queryIndex = t.getSqlQueryIndex();
    this.args = args != null ? args : new HashMap<String, String>();
    this.filter = filter != null ? filter : new HashMap<Integer, String>();
    this.options = options != null ? options : new HashMap<Integer, Map<String, String>>();
    this.sortby = sortby != null ? sortby : new HashMap<Integer, String>();
    this.focuson = focuson;
    this.filterPosition = t.getFilterPosition();
  }

  public static Item getInstance(DbvTable t, Map<String, String> args, Map<Integer, String> filter,
                                 Map<Integer, Map<String, String>> options, Map<Integer, String> sortby,
                                 Integer offsetRow, Integer countRows, String focuson, boolean fetchRows) {
    Item item = new Table(t, args, filter, options, sortby, focuson);
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
}
