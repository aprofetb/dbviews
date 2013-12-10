package org.dbviews.api.vo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.Timestamp;

import java.sql.Types;

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
import org.dbviews.api.database.Discoverer;
import org.dbviews.model.DbvConnection;

public abstract class Item
  implements Comparable
{
  private final static Logger logger = Logger.getLogger(Item.class.getName());
  private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
  private final static String FUNC_TOCHAR_MYSQL = "date_format(%s, '%%d/%%m/%%Y')";
  private final static String FUNC_TOCHAR_ORACLE = "to_char(%s, 'dd/mm/yyyy')";

  protected int id;
  protected String label;
  protected String description;
  protected String query;
  protected List<Header> headers;
  protected Map<String, String> args;
  protected Map<Integer, String> filter;
  protected Map<Integer, Map<String, String>> options;
  protected Map<Integer, String> sortby;
  protected List<Map<Integer, Object>> rows;
  protected String focuson;
  protected int offsetRow;
  protected int countRows;
  protected int totalRows;
  protected int index;
  protected long queryDelay;
  protected long initTime;
  protected Map<Integer, Map<String, Object>> columnMap;

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

  public void setRows(List<Map<Integer, Object>> rows)
  {
    this.rows = rows;
  }

  public List<Map<Integer, Object>> getRows()
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

  public void setFilter(Map<Integer, String> filter)
  {
    this.filter = filter;
  }

  public Map<Integer, String> getFilter()
  {
    return filter;
  }

  public void setSortby(Map<Integer, String> sortby)
  {
    this.sortby = sortby;
  }

  public Map<Integer, String> getSortby()
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
    Item item = (Item)o;
    return (int)Math.signum(index - item.index);
  }

  public void setOptions(Map<Integer, Map<String, String>> options)
  {
    this.options = options;
  }

  public Map<Integer, Map<String, String>> getOptions()
  {
    return options;
  }

  public void setArgs(Map<String, String> args)
  {
    this.args = args;
  }

  public void setColumnMap(Map<Integer, Map<String, Object>> columnMap)
  {
    this.columnMap = columnMap;
  }

  @XmlTransient
  public Map<Integer, Map<String, Object>> getColumnMap()
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

  public static Item getInstance(Item item,
                                 DbvConnection dbvConn,
                                 Map<String, String> args,
                                 Map<Integer, String> filter,
                                 Map<Integer, Map<String, String>> options,
                                 Map<Integer, String> sortby,
                                 Integer offsetRow,
                                 Integer countRows)
  {
    item.startTiming();
    boolean mysql = dbvConn.getUrl().startsWith("jdbc:mysql:");
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String queryStr = null;
    try
    {
      queryStr = item.getQuery();
      con = Connector.getConnection(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
      if (item.getHeaderCount() == 0)
      {
        Discoverer disco = new Discoverer(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
        Map<Integer, Map<String, Object>> columnMap = disco.getColumns(con, String.format("select * from (%s) sq1 where 1=2", queryStr), args, false);
        for (Map.Entry<Integer, Map<String, Object>> e : columnMap.entrySet())
        {
          Integer id = e.getKey();
          Map<String, Object> attrs = e.getValue();
          item.addHeader(new Header(id, (String)attrs.get("ColumnName"), (Integer)attrs.get("ColumnType")));
        }
        item.setColumnMap(columnMap);
      }

      Map.Entry<String, List> queryArgs = Discoverer.processArgs(queryStr, args);
      queryStr = queryArgs.getKey();
      List qParams = queryArgs.getValue();

      Pattern pattern = Pattern.compile("h_i_d_d_e_n__", Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(queryStr);
      item.setQuery(matcher.replaceAll(""));
      queryStr = String.format("select * from (%s) sq1", queryStr);
      if (filter != null && filter.size() > 0)
      {
        int i = 0;
        for (Map.Entry<Integer, String> e : filter.entrySet())
        {
          Integer id = e.getKey();
          String value = e.getValue();
          for (Header header : item.getHeaders())
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
                Map<String, Object> attrs = item.getColumnMap().get(id);
                String columnName = (String)attrs.get("ColumnName");
                if (columnName == null)
                  continue;
                String colExp = header.getType() == Types.DATE || header.getType() == Types.TIMESTAMP ? String.format(mysql ? FUNC_TOCHAR_MYSQL : FUNC_TOCHAR_ORACLE, columnName) : columnName;
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
      item.setOffsetRow(offsetRow);
      item.setCountRows(countRows);
      item.setTotalRows(totalRows);

      // build order by clause
      StringBuilder sortbySb = new StringBuilder();
      if (sortby != null && sortby.size() > 0)
      {
        sortbySb.append("order by ");
        for (Map.Entry<Integer, String> e : sortby.entrySet())
        {
          Integer id = e.getKey();
          Map<String, Object> attrs = item.getColumnMap().get(id);
          String columnName = (String)attrs.get("ColumnName");
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

      List<Map<Integer, Object>> rows = new LinkedList<Map<Integer, Object>>();
      ps = con.prepareStatement(queryStr);
      for (int i = 0; i < qParams.size(); i++)
        ps.setObject(i + 1, qParams.get(i));
      rs = ps.executeQuery();
      while (rs.next())
      {
        Map<Integer, Object> cells = new HashMap<Integer, Object>();
        for (int h = 0; h < item.getHeaderCount(); h++)
        {
          Header header = item.getHeaders().get(h);
          Object value = rs.getObject(header.getDbColumnName());
          if (value instanceof Date || value instanceof Timestamp)
            value = sdf.format(value);
          cells.put(header.getId(), value);
        }
        rows.add(cells);
      }
      item.setFilter(filter != null ? filter : new HashMap<Integer, String>());
      item.setOptions(options != null ? options : new HashMap<Integer, Map<String, String>>());
      item.setRows(rows);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      logger.severe(e.getMessage());
      logger.severe(queryStr);
      return null;
    }
    finally
    {
      Connector.relres(rs, ps, con);
    }
    item.stopTiming();
    return item;
  }

  @XmlTransient
  public String getHtml()
  {
    return getHtml(this);
  }

  public static String getHtml(Item item)
  {
    List<Item> items = new LinkedList<Item>();
    items.add(item);
    return getHtml(items);
  }

  public static String getHtml(Collection<Item> items)
  {
    StringBuilder html = new StringBuilder();
    html.append("<table border='1' style='border:none'>");
    for (Item item : items)
    {
      html.append("<tr>");
      int exp = 0;
      for (Header header : item.getHeaders())
        if (header.isExportable())
          exp++;
      html.append(String.format("<th align='left' colspan='%s' style='border:none;font-size:18pt'>", exp));
      html.append(StringEscapeUtils.escapeHtml(item.getLabel()));
      html.append("</th>");
      html.append("</tr>");
  
      html.append("<tr>");
      for (Header header : item.getHeaders())
      {
        if (!header.isExportable())
          continue;
        html.append(String.format("<th align='%s' valign='%s' style='color:white;background-color:#3366FF'>", header.getAlign(), header.getVAlign()));
        html.append(StringEscapeUtils.escapeHtml(header.getColumnName()));
        html.append("</th>");
      }
      html.append("</tr>");
  
      for (Map<Integer, Object> r : item.getRows())
      {
        html.append("<tr>");
        for (Header header : item.getHeaders())
        {
          if (!header.isExportable())
            continue;
          Object value = r.get(header.getId());
          html.append(String.format("<td align='%s' valign='%s' style='color:black;background-color:#FFFFCC'>", header.getAlign(), header.getVAlign()));
          //html.append(header.getType() == Type.Html ? value : StringEscapeUtils.escapeHtml(value.toString()));
          if (value != null)
            html.append(StringEscapeUtils.escapeHtml(value.toString()));
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
