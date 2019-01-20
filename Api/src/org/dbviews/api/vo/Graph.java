package org.dbviews.api.vo;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.dbviews.api.database.Connector;
import org.dbviews.model.DbvConnection;
import org.dbviews.model.DbvGraph;

public class Graph extends Item {
  private final static Logger logger = Logger.getLogger(Graph.class.getName());

  private String[] graphType;
  private Integer serieColumn;
  private Integer xaxisColumn;
  private Integer yaxisColumn;
  private String serieColumnName;
  private String xaxisColumnName;
  private String yaxisColumnName;
  private String xmode;
  private String ymode;
  private int width;
  private int height;
  private String legendPosition;
  private String filterPosition;
  private String toolbarPosition;

  public Graph(DbvGraph g, Map<String, String> args, Map<Integer, String> filter,
               Map<Integer, Map<String, String>> options, String focuson) {
    super(g.getDbvView().getDbvConnection());
    id = g.getId();
    label = g.getLabel();
    description = g.getDescription();
    index = g.getTabIndex();
    query = g.getSqlQuery();
    graphType = g.getGraphType().split(",");
    xmode = g.getXmode();
    ymode = g.getYmode();
    width = g.getWidth();
    height = g.getHeight();
    legendPosition = g.getLegendPosition();
    filterPosition = g.getFilterPosition();
    toolbarPosition = g.getToolbarPosition();
    serieColumnName = g.getSerieColumn();
    xaxisColumnName = g.getXaxisColumn();
    yaxisColumnName = g.getYaxisColumn();
    csvSeparator = g.getCsvSeparator();
    this.args = args != null ? args : new HashMap<String, String>();
    this.filter = filter != null ? filter : new HashMap<Integer, String>();
    this.options = options != null ? options : new HashMap<Integer, Map<String, String>>();
    this.sortby = new HashMap<Integer, String>();
    this.focuson = focuson;
  }

  @Override
  public void fetchFromDatabase(Integer offsetRow, Integer countRows, boolean fetchRows) {
    Connection con = null;
    try {
      DbvConnection dbvConn = getDbvConnection();
      con = Connector.getConnection(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
      con.setReadOnly(true);
      fetchFromDatabase(con, offsetRow, countRows, fetchRows);
      headers = new ArrayList<Header>();
      for (Map.Entry<Integer, Map<String, Object>> e : columnMap.entrySet()) {
        Integer id = e.getKey();
        Map<String, Object> attrs = e.getValue();
        String columnName = (String) attrs.get("ColumnName");
        int columnType = (Integer) attrs.get("ColumnType");
        headers.add(new Header(id, columnName, columnType));
        if (columnName.equalsIgnoreCase(serieColumnName))
          serieColumn = id;
        if (columnName.equalsIgnoreCase(xaxisColumnName))
          xaxisColumn = id;
        if (columnName.equalsIgnoreCase(yaxisColumnName))
          yaxisColumn = id;
      }
    } catch (Exception e) {
      logger.severe(e.getMessage());
      logger.severe(getQuery());
    } finally {
      Connector.relres(con);
    }
  }

  public static Item getInstance(DbvGraph g, Map<String, String> args, Map<Integer, String> filter,
                                 Map<Integer, Map<String, String>> options, String focuson, boolean fetchFromDatabase,
                                 boolean fetchRows) {
    Graph item = new Graph(g, args, filter, options, focuson);
    if (fetchFromDatabase)
      item.fetchFromDatabase(1, Integer.MAX_VALUE - 1, fetchRows);
    return item;
  }

  public String getType() {
    return "graph";
  }

  public void setGraphType(String[] type) {
    this.graphType = type;
  }

  public String[] getGraphType() {
    return graphType;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getWidth() {
    return width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getHeight() {
    return height;
  }

  public void setSerieColumn(Integer serieColumn) {
    this.serieColumn = serieColumn;
  }

  public Integer getSerieColumn() {
    return serieColumn;
  }

  public void setXaxisColumn(Integer xaxisColumn) {
    this.xaxisColumn = xaxisColumn;
  }

  public Integer getXaxisColumn() {
    return xaxisColumn;
  }

  public void setYaxisColumn(Integer yaxisColumn) {
    this.yaxisColumn = yaxisColumn;
  }

  public Integer getYaxisColumn() {
    return yaxisColumn;
  }

  public void setXmode(String xmode) {
    this.xmode = xmode;
  }

  public String getXmode() {
    return xmode;
  }

  public void setYmode(String ymode) {
    this.ymode = ymode;
  }

  public String getYmode() {
    return ymode;
  }

  public void setLegendPosition(String legendPosition) {
    this.legendPosition = legendPosition;
  }

  public String getLegendPosition() {
    return legendPosition;
  }

  public void setFilterPosition(String filterPosition) {
    this.filterPosition = filterPosition;
  }

  public String getFilterPosition() {
    return filterPosition;
  }

  public void setToolbarPosition(String toolbarPosition) {
    this.toolbarPosition = toolbarPosition;
  }

  public String getToolbarPosition() {
    return toolbarPosition;
  }
}
