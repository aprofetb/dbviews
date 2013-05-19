package org.dbviews.api.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.dbviews.api.database.Discoverer;
import org.dbviews.commons.utils.StrUtils;
import org.dbviews.model.DbvConnection;
import org.dbviews.model.DbvTable;
import org.dbviews.model.DbvTableField;

public class Table
  extends Tab
{
  public Table(DbvTable t, Map<String, String> args, Map<String, String> filter, Map<String, Map<String, String>> options, Map<String, String> sortby, String focuson)
  {
    headers = new ArrayList<Header>();
    if (t.getDbvTableFieldList().size() > 0)
    {
      columnMap = new HashMap<String, String>(t.getDbvTableFieldList().size());
      for (DbvTableField field : t.getDbvTableFieldList())
      {
        headers.add(new Header(field, args));
        columnMap.put(Integer.toString(field.getId()), field.getColumnName());
      }
    }
    else
    {
      DbvConnection dbvConn = t.getDbvView().getDbvConnection();
      Discoverer disco = new Discoverer(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
      columnMap = disco.getColumns(t.getSqlQuery());
      for (Map.Entry<String, String> e : columnMap.entrySet())
      {
        String id = e.getKey();
        String columnName = e.getValue();
        headers.add(new Header(id, columnName));
      }
    }
    id = t.getId();
    label = StrUtils.str4mat(t.getLabel(), args);
    description = StrUtils.str4mat(t.getDescription(), args);
    index = t.getTabIndex();
    query = StrUtils.str4mat(t.getSqlQuery(), args);
    this.args = args != null ? args : new HashMap<String, String>();
    this.filter = filter != null ? filter : new HashMap<String, String>();
    this.options = options != null ? options : new HashMap<String, Map<String, String>>();
    this.sortby = sortby != null ? sortby : new HashMap<String, String>();
    this.focuson = focuson;
  }

  public static Tab getInstance(DbvTable t,
                                Map<String, String> args,
                                Map<String, String> filter,
                                Map<String, Map<String, String>> options,
                                Map<String, String> sortby,
                                Integer offsetRow,
                                Integer countRows,
                                String focuson)
  {
    DbvConnection dbvConn = t.getDbvView().getDbvConnection();
    Tab table = new Table(t, args, filter, options, sortby, focuson);
    return getInstance(table, dbvConn, args, filter, options, sortby, offsetRow, countRows, focuson);
  }

  public String getType()
  {
    return "table";
  }
}
