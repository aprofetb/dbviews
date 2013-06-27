package org.dbviews.api.wrappers;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResultSetWrapper
  extends ArrayList<Map<String, Object>>
{
  public ResultSetWrapper(ResultSet rs)
    throws SQLException
  {
    super();
    Set<String> cols = new HashSet<String>();
    ResultSetMetaData rsMD = rs.getMetaData();
    for (int i = 1; i <= rsMD.getColumnCount(); i++)
      cols.add(rsMD.getColumnName(i));
    while (rs.next())
    {
      Map<String, Object> attrs = new HashMap<String, Object>();
      for (String col : cols)
        attrs.put(col, rs.getObject(col));
      add(attrs);
    }
  }

  public static ResultSetWrapper getInstance(ResultSet rs)
    throws SQLException
  {
    return new ResultSetWrapper(rs);
  }
}
