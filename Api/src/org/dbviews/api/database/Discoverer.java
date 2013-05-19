package org.dbviews.api.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

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

  public Map<String, String> getColumns(String sqlQuery)
  {
    Map<String, String> columnMap = new TreeMap<String, String>();
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      con = Connector.getConnection(url, username, password);
      ps = con.prepareStatement(String.format("select * from (%s) sq1", sqlQuery));
      rs = ps.executeQuery();
      ResultSetMetaData rsMD = rs.getMetaData();
      for (int i = 1; i <= rsMD.getColumnCount(); i++)
        columnMap.put(Integer.toString(i), rsMD.getColumnName(i));
    }
    catch (SQLException e)
    {
      logger.severe(e.getMessage());
    }
    finally
    {
      Connector.relres(rs, ps, con);
    }
    return columnMap;
  }
}
