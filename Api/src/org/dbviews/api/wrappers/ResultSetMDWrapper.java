package org.dbviews.api.wrappers;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResultSetMDWrapper
  extends HashMap<Integer, Map<String, Object>>
{
  public ResultSetMDWrapper(ResultSetMetaData rsMD)
    throws SQLException
  {
    super();
    for (int i = 1; i <= rsMD.getColumnCount(); i++)
    {
      Map<String, Object> attrs = new HashMap<String, Object>();
      attrs.put("CatalogName", rsMD.getCatalogName(i));
      attrs.put("ColumnName", rsMD.getColumnName(i));
      attrs.put("ColumnClassName", rsMD.getColumnClassName(i));
      attrs.put("ColumnDisplaySize", rsMD.getColumnDisplaySize(i));
      attrs.put("ColumnLabel", rsMD.getColumnLabel(i));
      attrs.put("ColumnName", rsMD.getColumnName(i));
      attrs.put("ColumnType", rsMD.getColumnType(i));
      attrs.put("ColumnTypeName", rsMD.getColumnTypeName(i));
      attrs.put("Precision", rsMD.getPrecision(i));
      attrs.put("Scale", rsMD.getScale(i));
      attrs.put("SchemaName", rsMD.getSchemaName(i));
      attrs.put("TableName", rsMD.getTableName(i));
      put(i, attrs);
    }
  }

  public static ResultSetMDWrapper getInstance(ResultSetMetaData rsMD)
    throws SQLException
  {
    return new ResultSetMDWrapper(rsMD);
  }
}
