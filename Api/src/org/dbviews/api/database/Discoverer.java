package org.dbviews.api.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

public class Discoverer
{
  private final static Logger logger = Logger.getLogger(Discoverer.class.getName());

  String url;
  String username;
  String password;

  public Discoverer(String url, String username, String password)
  {
    this.url = url;
    this.username = username;
    this.password = password;
  }

  public ResultSetWrapper getTables(String schemaPattern)
  {
    return getTables("", schemaPattern, "%");
  }

  public ResultSetWrapper getTables(String schemaPattern, String tableNamePattern)
  {
    return getTables("", schemaPattern, tableNamePattern);
  }

  public ResultSetWrapper getTables(String catalog, String schemaPattern, String tableNamePattern)
  {
    ResultSetWrapper rsw = null;
    Connection con = null;
    ResultSet rs = null;
    try
    {
      con = Connector.getConnection(url, username, password);
      DatabaseMetaData dbMD = con.getMetaData();
      rs = dbMD.getTables(catalog, StringUtils.upperCase(schemaPattern), StringUtils.upperCase(tableNamePattern), null);
      rsw = new ResultSetWrapper(rs);
    }
    catch (SQLException e)
    {
      logger.severe(e.getMessage());
    }
    finally
    {
      Connector.relres(rs, con);
    }
    return rsw;
  }

  public ResultSetWrapper getColumns(String schemaPattern, String tableNamePattern)
  {
    return getColumns("", schemaPattern, tableNamePattern, null);
  }

  public ResultSetWrapper getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
  {
    ResultSetWrapper rsw = null;
    Connection con = null;
    ResultSet rs = null;
    try
    {
      con = Connector.getConnection(url, username, password);
      DatabaseMetaData dbMD = con.getMetaData();
      rs = dbMD.getColumns(catalog, StringUtils.upperCase(schemaPattern), StringUtils.upperCase(tableNamePattern), StringUtils.upperCase(columnNamePattern));
      rsw = new ResultSetWrapper(rs);
    }
    catch (SQLException e)
    {
      logger.severe(e.getMessage());
    }
    finally
    {
      Connector.relres(rs, con);
    }
    return rsw;
  }

  public ResultSetWrapper getRows(String sqlQuery)
  {
    ResultSetWrapper rsw = null;
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      con = Connector.getConnection(url, username, password);
      ps = con.prepareStatement(String.format("select * from (%s) sq1", sqlQuery));
      rs = ps.executeQuery();
      rsw = new ResultSetWrapper(rs);
    }
    catch (SQLException e)
    {
      logger.severe(e.getMessage());
    }
    finally
    {
      Connector.relres(rs, ps, con);
    }
    return rsw;
  }

  public ResultSetWrapper findBy(String sqlQuery, String column, String value)
  {
    ResultSetWrapper rsw = null;
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      con = Connector.getConnection(url, username, password);
      ps = con.prepareStatement(String.format("select * from (%s) sq1 where %s='%s'", sqlQuery, column, value));
      rs = ps.executeQuery();
      rsw = new ResultSetWrapper(rs);
    }
    catch (SQLException e)
    {
      logger.severe(e.getMessage());
    }
    finally
    {
      Connector.relres(rs, ps, con);
    }
    return rsw;
  }

  public Map<Integer, Map<String, Object>> getColumns(Connection con, String sqlQuery, boolean showHidden)
  {
    Map<Integer, Map<String, Object>> columnMap = new TreeMap<Integer, Map<String, Object>>();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      ps = con.prepareStatement(String.format("select * from (%s) sq1", sqlQuery));
      rs = ps.executeQuery();
      ResultSetMetaData rsMD = rs.getMetaData();
      for (int i = 1; i <= rsMD.getColumnCount(); i++)
      {
        String columnName = rsMD.getColumnName(i);
        if (!showHidden && columnName.startsWith("h_i_d_d_e_n__"))
          continue;
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put("CatalogName", rsMD.getCatalogName(i));
        attrs.put("ColumnClassName", rsMD.getColumnClassName(i));
        attrs.put("ColumnDisplaySize", rsMD.getColumnDisplaySize(i));
        attrs.put("ColumnLabel", rsMD.getColumnLabel(i));
        attrs.put("ColumnName", columnName);
        attrs.put("ColumnType", rsMD.getColumnType(i));
        attrs.put("ColumnTypeName", rsMD.getColumnTypeName(i));
        attrs.put("Precision", rsMD.getPrecision(i));
        attrs.put("Scale", rsMD.getScale(i));
        attrs.put("SchemaName", rsMD.getSchemaName(i));
        attrs.put("TableName", rsMD.getTableName(i));
        columnMap.put(i, attrs);
      }
    }
    catch (SQLException e)
    {
      logger.severe(e.getMessage());
    }
    finally
    {
      Connector.relres(rs, ps);
    }
    return columnMap;
  }

  public Map<Integer, Map<String, Object>> getColumns(String sqlQuery, boolean showHidden)
  {
    Connection con = null;
    try
    {
      con = Connector.getConnection(url, username, password);
      return getColumns(con, sqlQuery, showHidden);
    }
    catch (SQLException e)
    {
      logger.severe(e.getMessage());
    }
    finally
    {
      Connector.relres(con);
    }
    return new HashMap<Integer, Map<String, Object>>();
  }
}
