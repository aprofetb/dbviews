package org.dbviews.api.vo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;

import org.dbviews.api.database.Connector;
import org.dbviews.api.database.Discoverer;
import org.dbviews.api.vo.exporters.CsvExporter;
import org.dbviews.api.vo.exporters.Exporter;
import org.dbviews.api.vo.exporters.HtmlExporter;
import org.dbviews.model.DbvConnection;

public abstract class Item implements Comparable {
  private final static Logger logger = Logger.getLogger(Item.class.getName());
  private final static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
  private final static String FUNC_TOCHAR_MYSQL = "date_format(%s, '%%m/%%d/%%Y')";
  private final static String FUNC_TOCHAR_ORACLE = "to_char(%s, 'mm/dd/yyyy')";

  protected int id;
  protected String label;
  protected String description;
  protected String query;
  protected String queryIndex;
  protected String csvSeparator;
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
  protected boolean rowsFetched = false;
  protected DbvConnection dbvConnection;

  public Item(DbvConnection dbvConnection) {
    this.dbvConnection = dbvConnection;
  }

  public abstract String getType();

  public void setHeaders(List<Header> headers) {
    this.headers = headers;
  }

  @XmlTransient
  public List<Header> getHeaders() {
    return headers;
  }

  @XmlElement(name = "headers")
  public List<Header> getVisibleHeaders() {
    List<Header> vHeaders = new ArrayList<>();
    if (this.headers != null && this.headers.size() > 0)
      for (Header header : this.headers)
        if (header.isVisible())
          vHeaders.add(header);
    return vHeaders;
  }

  public void setRows(List<Map<Integer, Object>> rows) {
    this.rows = rows;
  }

  public List<Map<Integer, Object>> getRows() {
    return rows;
  }

  public void setLabel(String name) {
    this.label = name;
  }

  public String getLabel() {
    return label;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setOffsetRow(int offsetRow) {
    this.offsetRow = offsetRow;
  }

  public int getOffsetRow() {
    return offsetRow;
  }

  public void setCountRows(int countRows) {
    this.countRows = countRows;
  }

  public int getCountRows() {
    return countRows;
  }

  public void setTotalRows(int totalRows) {
    this.totalRows = totalRows;
  }

  public int getTotalRows() {
    return totalRows;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @XmlTransient
  public List<Header> getOrderHeaders() {
    List<Header> oHeaders = new ArrayList<>();
    for (Header header : this.headers)
      if (header.getOrder() != Order.Locked && header.getOrder() != Order.None)
        oHeaders.add(header);
    Collections.<Header>sort(oHeaders);
    return oHeaders;
  }

  public void setFilter(Map<Integer, String> filter) {
    this.filter = filter;
  }

  public Map<Integer, String> getFilter() {
    return filter;
  }

  public void setSortby(Map<Integer, String> sortby) {
    this.sortby = sortby;
  }

  public Map<Integer, String> getSortby() {
    return sortby;
  }

  public void setFocuson(String focuson) {
    this.focuson = focuson;
  }

  public String getFocuson() {
    return focuson;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  @XmlTransient
  public String getQuery() {
    return query;
  }

  public void setQueryIndex(String queryIndex) {
    this.queryIndex = queryIndex;
  }

  @XmlTransient
  public String getQueryIndex() {
    return queryIndex;
  }

  public void setCsvSeparator(String csvSeparator) {
    this.csvSeparator = csvSeparator;
  }

  public String getCsvSeparator() {
    return csvSeparator;
  }

  public void setRowsFetched(boolean rowsFetched) {
    this.rowsFetched = rowsFetched;
  }

  @XmlTransient
  public boolean isRowsFetched() {
    return rowsFetched;
  }

  public void addHeader(Header header) {
    if (header == null) {
      logger.severe("Header is null");
      return;
    }
    if (this.headers == null)
      this.headers = new ArrayList<Header>();
    headers.add(header);
  }

  public void addHeader(int index, Header header) {
    if (header == null) {
      logger.severe("Header is null");
      return;
    }
    if (this.headers == null)
      this.headers = new ArrayList<Header>();
    if (index < 0 || index > headers.size()) {
      logger.severe("Error: index out of bounds");
      return;
    }
    headers.add(index, header);
  }

  @XmlTransient
  public int getHeaderCount() {
    return this.headers != null ? this.headers.size() : 0;
  }

  public void removeHeaders() {
    if (this.getHeaderCount() > 0)
      this.headers.clear();
  }

  public void setOrderBy(OrderByVO orderBy) {
    if (orderBy == null)
      return;
    for (Header header : this.headers)
      if (header.getOrder() != Order.Locked)
        header.setOrder(orderBy.containsField(header.getDbColumnName()) ? orderBy.getOrder(header.getDbColumnName()) :
                        Order.None);
  }

  @XmlTransient
  public OrderByVO getOrderBy() {
    List<Header> oHeaders = this.getOrderHeaders();
    return oHeaders.size() > 0 ? new OrderByVO(oHeaders) : new OrderByVO("1", Order.Asc);
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  public Integer getIndex() {
    return index;
  }

  public int compareTo(Object o) {
    Item item = (Item) o;
    return (int) Math.signum(index - item.index);
  }

  public void setOptions(Map<Integer, Map<String, String>> options) {
    this.options = options;
  }

  public Map<Integer, Map<String, String>> getOptions() {
    return options;
  }

  public void setArgs(Map<String, String> args) {
    this.args = args;
  }

  public Map<String, String> getArgs() {
    return args;
  }

  public void setColumnMap(Map<Integer, Map<String, Object>> columnMap) {
    this.columnMap = columnMap;
  }

  @XmlTransient
  public Map<Integer, Map<String, Object>> getColumnMap() {
    return columnMap;
  }

  public void setQueryDelay(long queryDelay) {
    this.queryDelay = queryDelay;
  }

  public long getQueryDelay() {
    return queryDelay;
  }

  public void startTiming() {
    initTime = Calendar.getInstance().getTimeInMillis();
  }

  public void stopTiming() {
    queryDelay = Calendar.getInstance().getTimeInMillis() - initTime;
  }

  public void setDbvConnection(DbvConnection dbvConnection) {
    this.dbvConnection = dbvConnection;
  }

  @XmlTransient
  public DbvConnection getDbvConnection() {
    return dbvConnection;
  }

  public void fetchFromDatabase(Integer offsetRow, Integer countRows, boolean fetchRows) {
    Connection con = null;
    String queryStr = null;
    try {
      queryStr = getQuery();
      DbvConnection dbvConn = getDbvConnection();
      con = Connector.getConnection(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
      con.setReadOnly(true);
      if (getHeaderCount() == 0) {
        Discoverer disco = new Discoverer(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
        Map<Integer, Map<String, Object>> columnMap =
          disco.getColumns(con, String.format("select * from (%s) sq1 where 1=2", queryStr), getArgs(), false);
        for (Map.Entry<Integer, Map<String, Object>> e : columnMap.entrySet()) {
          Integer id = e.getKey();
          Map<String, Object> attrs = e.getValue();
          addHeader(new Header(id, (String) attrs.get("ColumnName"), (Integer) attrs.get("ColumnType")));
        }
        setColumnMap(columnMap);
      }
      if (fetchRows) {
        this.fetchRows(offsetRow, countRows, con, null);
      } else {
        setOffsetRow(offsetRow);
        setCountRows(countRows);
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.severe(e.getMessage());
      logger.severe(queryStr);
    } finally {
      Connector.relres(con);
    }
    stopTiming();
  }

  public void fetchRows(Integer offsetRow, Integer countRows, Connection con, RowWriter rowWriter) {
    setRowsFetched(true);
    Map<String, String> args = getArgs();
    Map<Integer, String> filter = getFilter();
    Map<Integer, Map<String, String>> options = getOptions();
    Map<Integer, String> sortby = getSortby();
    boolean createConnection = con == null;
    DbvConnection dbvConn = getDbvConnection();
    boolean mysql = dbvConn.getUrl().startsWith("jdbc:mysql:");
    PreparedStatement ps = null;
    ResultSet rs = null;
    String queryStr = null;
    try {
      if (createConnection) {
        con = Connector.getConnection(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
        con.setReadOnly(true);
      }
      startTiming();
      queryStr = getQuery();
      Map.Entry<String, List<?>> queryArgs = Discoverer.processArgs(queryStr, args);
      queryStr = queryArgs.getKey();
      List qParams = queryArgs.getValue();

      Pattern pattern = Pattern.compile("h_i_d_d_e_n__", Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(queryStr);
      setQuery(matcher.replaceAll(""));
      queryStr = String.format("select * from (%s) sq1", queryStr);
      if (filter != null && filter.size() > 0) {
        int i = 0;
        for (Map.Entry<Integer, String> e : filter.entrySet()) {
          Integer id = e.getKey();
          String value = e.getValue();
          for (Header header : getHeaders()) {
            if (id.equals(header.getId())) {
              if (StringUtils.isNotBlank(value)) {
                boolean regex = false;
                boolean caseSensitive = false;
                if (options != null) {
                  Map<String, String> option = options.get(id);
                  if (option != null) {
                    regex = "1".equals(option.get("Regex"));
                    caseSensitive = "1".equals(option.get("CaseSensitive"));
                  }
                }
                queryStr += i++ == 0 ? " where " : " and ";
                Map<String, Object> attrs = getColumnMap().get(id);
                String columnName = (String) attrs.get("ColumnName");
                if (columnName == null)
                  continue;
                String colExp = String.format("\"%s\"", columnName);
                if (header.getType() == Types.DATE || header.getType() == Types.TIMESTAMP)
                  colExp = String.format(mysql ? FUNC_TOCHAR_MYSQL : FUNC_TOCHAR_ORACLE, colExp);
                if (regex) {
                  if (mysql)
                    queryStr += String.format("\"%s\" regexp %s ?", colExp, caseSensitive ? "binary" : "");
                  else
                    queryStr += String.format("regexp_like(\"%s\", ?, '%sn')", colExp, caseSensitive ? "" : "i");
                } else {
                  if (caseSensitive)
                    queryStr += String.format("\"%s\" like ?", colExp);
                  else
                    queryStr +=
                      String.format("lower(%s) like lower(?)", colExp);
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
      setOffsetRow(offsetRow);
      setCountRows(countRows == -1 ? totalRows : countRows);
      setTotalRows(totalRows);

      // build order by clause
      StringBuilder sortbySb = new StringBuilder();
      if (sortby != null && sortby.size() > 0) {
        sortbySb.append("order by ");
        for (Map.Entry<Integer, String> e : sortby.entrySet()) {
          Integer id = e.getKey();
          Map<String, Object> attrs = getColumnMap().get(id);
          String columnName = (String) attrs.get("ColumnName");
          Order dir = Order.valueOf(e.getValue());
          sortbySb.append(mysql ? String.format("%s %s", columnName, dir) :
                          String.format("\"%s\" %s", columnName, dir));
        }
      }

      // build limit clause
      if (mysql) {
        queryStr = String.format("select * from (%s %s) sq2 limit ?, ?", queryStr, sortbySb.toString());
        qParams.add(offsetRow - 1);
        qParams.add(countRows);
      } else {
        if (StringUtils.isNotBlank(getQueryIndex())) {
          queryStr =
            MessageFormat.format("select * from ({2} {1}) where ({3}) in (select {3} from (select rownum as row_num, t.* from ({0} {1}) t where rownum < ?) where row_num >= ?)",
                                 queryStr, sortbySb.toString(), getQuery(), getQueryIndex());
        } else {
          queryStr =
            MessageFormat.format("select * from (select rownum as row_num, t.* from ({0} {1}) t where rownum < ?) where row_num >= ?",
                                 queryStr, sortbySb.toString());
        }
        qParams.add(offsetRow + countRows);
        qParams.add(offsetRow);
      }

      List<Map<Integer, Object>> rows = new LinkedList<Map<Integer, Object>>();
      ps = con.prepareStatement(queryStr);
      for (int i = 0; i < qParams.size(); i++)
        ps.setObject(i + 1, qParams.get(i));
      rs = ps.executeQuery();
      while (rs.next()) {
        Map<Integer, Object> cells = new HashMap<Integer, Object>();
        for (int h = 0; h < getHeaderCount(); h++) {
          Header header = getHeaders().get(h);
          Object value = rs.getObject(header.getDbColumnName());
          if (value instanceof Date || value instanceof Timestamp)
            value = sdf.format(value);
          cells.put(header.getId(), value);
        }
        if (rowWriter == null) {
          rows.add(cells);
        } else {
          rowWriter.write(cells);
        }
      }
      if (filter == null)
        setFilter(new HashMap<Integer, String>());
      if (options == null)
        setOptions(new HashMap<Integer, Map<String, String>>());
      setRows(rows);
    } catch (Exception e) {
      e.printStackTrace();
      logger.severe(e.getMessage());
      logger.severe(queryStr);
    } finally {
      Connector.relres(rs, ps);
      if (createConnection)
        Connector.relres(con);
    }
    stopTiming();
  }

  public static interface RowWriter {
    void write(Map<Integer, Object> row) throws IOException;
  }


  @XmlTransient
  public String getHtml() throws IOException {
    return getHtml(this);
  }

  public static String getHtml(Item item) throws IOException {
    return getHtml(Arrays.asList(item));
  }

  public static String getHtml(Collection<Item> items) throws IOException {
    StringBuilder html = new StringBuilder();
    Exporter exporter = new HtmlExporter(html);
    exporter.writeItems(items);
    return html.toString();
  }


  @XmlTransient
  public String getCsv() throws IOException {
    return getCsv(this);
  }

  public static String getCsv(Item item) throws IOException {
    return getCsv(Arrays.asList(item));
  }

  public static String getCsv(Collection<Item> items) throws IOException {
    StringBuilder csv = new StringBuilder();
    Exporter exporter = new CsvExporter(csv);
    exporter.writeItems(items);
    return csv.toString();
  }


  public StreamingOutput getStreamingOutput(Class<? extends Exporter> exporterType) {
    return getStreamingOutput(this, exporterType);
  }

  public static StreamingOutput getStreamingOutput(Item item, Class<? extends Exporter> exporterType) {
    return getStreamingOutput(Arrays.asList(item), exporterType);
  }

  public static StreamingOutput getStreamingOutput(Collection<Item> items, Class<? extends Exporter> exporterType) {
    return new StreamingOutputImpl(items, exporterType);
  }


  @XmlTransient
  public StreamingOutput getHtmlStreamingOutput() {
    return getHtmlStreamingOutput(this);
  }

  public static StreamingOutput getHtmlStreamingOutput(Item item) {
    return getHtmlStreamingOutput(Arrays.asList(item));
  }

  public static StreamingOutput getHtmlStreamingOutput(Collection<Item> items) {
    return new StreamingOutputImpl(items, HtmlExporter.class);
  }


  @XmlTransient
  public StreamingOutput getCsvStreamingOutput() {
    return getCsvStreamingOutput(this);
  }

  public static StreamingOutput getCsvStreamingOutput(Item item) {
    return getCsvStreamingOutput(Arrays.asList(item));
  }

  public static StreamingOutput getCsvStreamingOutput(Collection<Item> items) {
    return new StreamingOutputImpl(items, CsvExporter.class);
  }


  static class StreamingOutputImpl implements StreamingOutput {
    Collection<Item> items;
    Class<? extends Exporter> exporterType;

    public StreamingOutputImpl(Collection<Item> items, Class<? extends Exporter> exporterType) {
      if (items == null)
        throw new IllegalArgumentException("Argument 'items' cannot be null");
      if (exporterType == null)
        throw new IllegalArgumentException("Argument 'exporterType' cannot be null");
      this.items = items;
      this.exporterType = exporterType;
    }

    @Override
    public void write(OutputStream os) throws IOException, WebApplicationException {
      Writer writer = new BufferedWriter(new OutputStreamWriter(os));
      Exporter exporter = null;
      try {
        exporter = exporterType.getConstructor(Appendable.class).newInstance(writer);
      } catch (Exception e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
        return;
      }
      exporter.writeItems(items);
      writer.flush();
    }
  }
}
