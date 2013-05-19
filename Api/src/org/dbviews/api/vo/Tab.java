package org.dbviews.api.vo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import org.dbviews.api.database.Connector;
import org.dbviews.model.DbvConnection;

public abstract class Tab
  implements Comparable
{
  private final static Logger logger = Logger.getLogger(Tab.class.getName());
  private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  protected int id;
  protected String label;
  protected String description;
  protected String query;
  protected List<Header> headers;
  protected Map<String, String> args;
  protected Map<String, String> filter;
  protected Map<String, Map<String, String>> options;
  protected Map<String, String> sortby;
  protected List<Map<String, String>> rows;
  protected String focuson;
  protected int offsetRow;
  protected int countRows;
  protected int totalRows;
  protected int index;
  protected long queryDelay;
  protected long initTime;
  protected Map<String, String> columnMap;

  public void setHeaders(List<Header> headers)
  {
    this.headers = headers;
  }

  @XmlTransient
  public List<Header> getHeaders()
  {
    return headers;
  }

  @XmlElement(name = "headers")
  public List<Header> getVisibleHeaders()
  {
    List<Header> vHeaders = new ArrayList<Header>();
    if (this.headers != null && this.headers.size() > 0)
      for (Header header : this.headers)
        if (header.isVisible())
          vHeaders.add(header);
    return vHeaders;
  }

  public void setRows(List<Map<String, String>> rows)
  {
    this.rows = rows;
  }

  public List<Map<String, String>> getRows()
  {
    return rows;
  }

  public void setLabel(String name)
  {
    this.label = name;
  }

  public String getLabel()
  {
    return label;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getDescription()
  {
    return description;
  }

  public void setOffsetRow(int offsetRow)
  {
    this.offsetRow = offsetRow;
  }

  public int getOffsetRow()
  {
    return offsetRow;
  }

  public void setCountRows(int countRows)
  {
    this.countRows = countRows;
  }

  public int getCountRows()
  {
    return countRows;
  }

  public void setTotalRows(int totalRows)
  {
    this.totalRows = totalRows;
  }

  public int getTotalRows()
  {
    return totalRows;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public int getId()
  {
    return id;
  }

  @XmlTransient
  public List<Header> getOrderHeaders()
  {
    List<Header> oHeaders = new ArrayList<Header>();
    for (Header header : this.headers)
      if (header.getOrder() != Order.Locked && header.getOrder() != Order.None)
        oHeaders.add(header);
    Collections.sort(oHeaders);
    return oHeaders;
  }

  public void setFilter(Map<String, String> filter)
  {
    this.filter = filter;
  }

  public Map<String, String> getFilter()
  {
    return filter;
  }

  public void setSortby(Map<String, String> sortby)
  {
    this.sortby = sortby;
  }

  public Map<String, String> getSortby()
  {
    return sortby;
  }

  public void setFocuson(String focuson)
  {
    this.focuson = focuson;
  }

  public String getFocuson()
  {
    return focuson;
  }

  public void setQuery(String query)
  {
    this.query = query;
  }

  @XmlTransient
  public String getQuery()
  {
    return query;
  }

  public void addHeader(Header header)
  {
    if (header == null)
    {
      logger.severe("Header is null");
      return;
    }
    if (this.headers == null)
      this.headers = new ArrayList<Header>();
    headers.add(header);
  }

  public void addHeader(int index, Header header)
  {
    if (header == null)
    {
      logger.severe("Header is null");
      return;
    }
    if (this.headers == null)
      this.headers = new ArrayList<Header>();
    if (index < 0 || index > headers.size())
    {
      logger.severe("Error: index out of bounds");
      return;
    }
    headers.add(index, header);
  }

  public int getHeaderCount()
  {
    return this.headers != null ? this.headers.size() : 0;
  }

  public void removeHeaders()
  {
    if (this.getHeaderCount() > 0)
      this.headers.clear();
  }

  public void setOrderBy(OrderByVO orderBy)
  {
    if (orderBy == null)
      return;
    for (Header header : this.headers)
      if (header.getOrder() != Order.Locked)
        header.setOrder(orderBy.containsField(header.getDbColumnName()) ? orderBy.getOrder(header.getDbColumnName()) : Order.None);
  }

  @XmlTransient
  public OrderByVO getOrderBy()
  {
    List<Header> oHeaders = this.getOrderHeaders();
    return oHeaders.size() > 0 ? new OrderByVO(oHeaders) : new OrderByVO("1", Order.Asc);
  }

  public void setIndex(Integer index)
  {
    this.index = index;
  }

  public Integer getIndex()
  {
    return index;
  }

  public int compareTo(Object o)
  {
    Tab table = (Tab)o;
    return (int)Math.signum(index - table.index);
  }

  public void setOptions(Map<String, Map<String, String>> options)
  {
    this.options = options;
  }

  public Map<String, Map<String, String>> getOptions()
  {
    return options;
  }

  public void setArgs(Map<String, String> args)
  {
    this.args = args;
  }

  public void setColumnMap(Map<String, String> columnMap)
  {
    this.columnMap = columnMap;
  }

  @XmlTransient
  public Map<String, String> getColumnMap()
  {
    return columnMap;
  }

  public Map<String, String> getArgs()
  {
    return args;
  }

  public void setQueryDelay(long queryDelay)
  {
    this.queryDelay = queryDelay;
  }

  public long getQueryDelay()
  {
    return queryDelay;
  }

  public void startTiming()
  {
    initTime = Calendar.getInstance().getTimeInMillis();
  }

  public void stopTiming()
  {
    queryDelay = Calendar.getInstance().getTimeInMillis() - initTime;
  }

  public static Tab getInstance(Tab tab,
                                DbvConnection dbvConn,
                                Map<String, String> args,
                                Map<String, String> filter,
                                Map<String, Map<String, String>> options,
                                Map<String, String> sortby,
                                Integer offsetRow,
                                Integer countRows,
                                String focuson)
  {
    tab.startTiming();
    boolean mysql = dbvConn.getUrl().startsWith("jdbc:mysql:");
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String queryStr = null;
    try
    {
      con = Connector.getConnection(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
      if (tab.getHeaderCount() == 0)
      {
        ps = con.prepareStatement(String.format("select * from (%s) sq1 where 1=2", tab.getQuery()));
        rs = ps.executeQuery();
        Map<String, String> columnMap = new HashMap<String, String>();
        ResultSetMetaData rsMD = rs.getMetaData();
        for (int i = 1; i <= rsMD.getColumnCount(); i++)
        {
          String id = Integer.toString(i);
          String columnName = rsMD.getColumnName(i);
          if (columnName.startsWith("h_i_d_d_e_n__"))
            continue;
          columnMap.put(id, columnName);
          tab.addHeader(new Header(id, columnName));
        }
        tab.setColumnMap(columnMap);
        Connector.relres(rs, ps);
      }

      Pattern pattern = Pattern.compile("h_i_d_d_e_n__", Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(tab.getQuery());
      tab.setQuery(matcher.replaceAll(""));
      queryStr = String.format("select * from (%s) sq1", tab.getQuery());
      List qParams = new LinkedList();
      if (filter != null)
      {
        int i = 0;
        for (Map.Entry<String, String> e : filter.entrySet())
        {
          String id = e.getKey();
          String value = e.getValue();
          for (Header header : tab.getHeaders())
          {
            if (id.equals(header.getId()))
            {
              if (StringUtils.isNotBlank(value))
              {
                boolean regex = false;
                boolean caseSensitive = false;
                if (options != null)
                {
                  Map<String, String> option = options.get(id);
                  if (option != null)
                  {
                    regex = "1".equals(option.get("Regex"));
                    caseSensitive = "1".equals(option.get("CaseSensitive"));
                  }
                }
                queryStr += i++ == 0 ? " where " : " and ";
                String columnName = tab.getColumnMap().get(id);
                if (columnName == null)
                  continue;
                String colExp = header.getType() == Type.Date ? String.format("to_char(%s, 'yyyy/mm/dd')", columnName) : columnName;
                if (regex)
                {
                  if (mysql)
                    queryStr += String.format("%s regexp %s ?", colExp, caseSensitive ? "binary" : "");
                  else
                    queryStr += String.format("regexp_like(%s, ?, '%sn')", colExp, caseSensitive ? "" : "i");
                }
                else
                {
                  if (caseSensitive)
                    queryStr += String.format("%s like ?", colExp);
                  else
                    queryStr += String.format("lower(%s) like lower(?)", colExp);
                }
    
                qParams.add(regex ? value : "%" + value + "%");
              }
              break;
            }
          }
        }
      }

      // get total of rows before build order by and limit clauses
      ps = con.prepareStatement(String.format("select count(*) from (%s) sq1", queryStr));
      for (int i = 0; i < qParams.size(); i++)
        ps.setObject(i + 1, qParams.get(i));
      rs = ps.executeQuery();
      int totalRows = rs.next() ? rs.getInt(1) : 0;
      Connector.relres(rs, ps);
      if (totalRows < offsetRow)
        offsetRow = 1;
      tab.setOffsetRow(offsetRow);
      tab.setCountRows(countRows);
      tab.setTotalRows(totalRows);

      // build order by clause
      OrderByVO tOrderBy = tab.getOrderBy();
      StringBuilder sortbySb = new StringBuilder();
      if (sortby != null && sortby.size() > 0)
      {
        sortbySb.append("order by ");
        for (Map.Entry<String, String> e : sortby.entrySet())
        {
          String id = e.getKey();
          String columnName = tab.getColumnMap().get(id);
          Order dir = Order.valueOf(e.getValue());
          sortbySb.append(mysql ? String.format("%s %s", columnName, dir) : String.format("\"%s\" %s", columnName, dir));
        }
      }

      // build limit clause
      if (mysql)
      {
        queryStr = String.format("select * from (%s %s) sq2 limit ?, ?", queryStr, sortbySb.toString());
        qParams.add(offsetRow - 1);
        qParams.add(countRows);
      }
      else
      {
        queryStr = String.format("select * from (select rownum as row_num, t.* from (%s %s) t) where row_num >= ? and row_num < ?", queryStr, sortbySb.toString());
        qParams.add(offsetRow);
        qParams.add(offsetRow + countRows);
      }

      List<Map<String, String>> rows = new LinkedList<Map<String, String>>();
      ps = con.prepareStatement(queryStr);
      for (int i = 0; i < qParams.size(); i++)
        ps.setObject(i + 1, qParams.get(i));
      rs = ps.executeQuery();
      while (rs.next())
      {
        Map<String, String> cells = new HashMap<String, String>();
        for (int h = 0; h < tab.getHeaderCount(); h++)
        {
          Header header = tab.getHeaders().get(h);
          String value = "";
          if (header.getType() == Type.Date)
          {
            Date date = rs.getDate(header.getDbColumnName());
            if (date != null)
              value = sdf.format(date);
          }
          else
          {
            value = StringUtils.defaultString(rs.getString(header.getDbColumnName()));
          }
          cells.put(header.getId(), value);
        }
        rows.add(cells);
      }
      tab.setFilter(filter != null ? filter : new HashMap<String, String>());
      tab.setOptions(options != null ? options : new HashMap<String, Map<String, String>>());
      tab.setRows(rows);
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      logger.severe(queryStr);
      return null;
    }
    finally
    {
      Connector.relres(rs, ps, con);
    }
    tab.stopTiming();
    return tab;
  }

  @XmlTransient
  public String getHtml()
  {
    return getHtml(this);
  }

  public static String getHtml(Tab table)
  {
    List<Tab> tables = new LinkedList<Tab>();
    tables.add(table);
    return getHtml(tables);
  }

  public static String getHtml(Collection<Tab> tables)
  {
    StringBuilder html = new StringBuilder();
    html.append("<table border='1' style='border:none'>");
    for (Tab table : tables)
    {
      html.append("<tr>");
      int exp = 0;
      for (Header header : table.getHeaders())
        if (header.isExportable())
          exp++;
      html.append(String.format("<th align='left' colspan='%s' style='border:none;font-size:18pt'>", exp));
      html.append(StringEscapeUtils.escapeHtml(table.getLabel()));
      html.append("</th>");
      html.append("</tr>");
  
      html.append("<tr>");
      for (Header header : table.getHeaders())
      {
        if (!header.isExportable())
          continue;
        html.append(String.format("<th align='%s' valign='%s' style='color:white;background-color:#3366FF'>", header.getAlign(), header.getVAlign()));
        html.append(StringEscapeUtils.escapeHtml(header.getColumnName()));
        html.append("</th>");
      }
      html.append("</tr>");
  
      for (Map<String, String> r : table.getRows())
      {
        html.append("<tr>");
        for (Header header : table.getHeaders())
        {
          if (!header.isExportable())
            continue;
          String value = r.get(header.getId());
          html.append(String.format("<td align='%s' valign='%s' style='color:black;background-color:#FFFFCC'>", header.getAlign(), header.getVAlign()));
          html.append(header.getType() == Type.Html ? value : StringEscapeUtils.escapeHtml(value));
          html.append("</td>");
        }
        html.append("</tr>");
      }
    }
    html.append("</table>");

    return html.toString();
  }

  public abstract String getType();
}
