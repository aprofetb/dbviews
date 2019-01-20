package org.dbviews.api.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Properties;
import java.util.logging.Logger;

public class Connector
{
  private final static Logger logger = Logger.getLogger(Connector.class.getName());

  public static Connection getConnection(String url, Properties props)
    throws SQLException
  {
    //logger.warning(String.format("getConnection(%s, %s)", url, props));
    return DriverManager.getConnection(url, props);
  }

  public static Connection getConnection(String url, String user, String password)
    throws SQLException
  {
    Properties props = new Properties();
    props.put("user", user);
    props.put("password", password);
    return getConnection(url, props);
  }

  public static void relres(Object... resources)
  {
    if (resources == null || resources.length == 0)
      return;
    for (Object res : resources)
    {
      if (res == null)
        continue;
      if (res instanceof ResultSet)
      {
        try
        {
          ResultSet rs = (ResultSet)res;
          rs.close();
        }
        catch (Exception e)
        {
          logger.warning(e.getMessage());
        }
      }
      else if (res instanceof Statement)
      {
        try
        {
          Statement st = (Statement)res;
          st.close();
        }
        catch (Exception e)
        {
          logger.warning(e.getMessage());
        }
      }
      else if (res instanceof PreparedStatement)
      {
        try
        {
          PreparedStatement ps = (PreparedStatement)res;
          ps.close();
        }
        catch (Exception e)
        {
          logger.warning(e.getMessage());
        }
      }
      else if (res instanceof Connection)
      {
        try
        {
          Connection con = (Connection)res;
          if (!con.isClosed())
            con.close();
        }
        catch (Exception e)
        {
          logger.warning(e.getMessage());
        }
      }
    }
  }
}
