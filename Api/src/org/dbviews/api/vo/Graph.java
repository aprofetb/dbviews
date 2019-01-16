package org.dbviews.api.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.dbviews.api.database.Discoverer;
import org.dbviews.model.DbvConnection;
import org.dbviews.model.DbvGraph;

public class Graph extends Item {
  private String[] graphType;
  private Integer serieColumn;
  private Integer xaxisColumn;
  private Integer yaxisColumn;
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
    headers = new ArrayList<Header>();
    DbvConnection dbvConn = g.getDbvView().getDbvConnection();
    Discoverer disco = new Discoverer(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
    columnMap = disco.getColumns(g.getSqlQuery(), args, true);
    for (Map.Entry<Integer, Map<String, Object>> e : columnMap.entrySet()) {
      Integer id = e.getKey();
      Map<String, Object> attrs = e.getValue();
      String columnName = (String) attrs.get("ColumnName");
      int columnType = (Integer) attrs.get("ColumnType");
      headers.add(new Header(id, columnName, columnType));
      if (columnName.equalsIgnoreCase(g.getSerieColumn()))
        serieColumn = id;
      if (columnName.equalsIgnoreCase(g.getXaxisColumn()))
        xaxisColumn = id;
      if (columnName.equalsIgnoreCase(g.getYaxisColumn()))
        yaxisColumn = id;
    }
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
    csvSeparator = g.getCsvSeparator();
    this.args = args != null ? args : new HashMap<String, String>();
    this.filter = filter != null ? filter : new HashMap<Integer, String>();
    this.options = options != null ? options : new HashMap<Integer, Map<String, String>>();
    this.sortby = new HashMap<Integer, String>();
    this.focuson = focuson;
  }

  public static Item getInstance(DbvGraph g, Map<String, String> args, Map<Integer, String> filter,
                                 Map<Integer, Map<String, String>> options, String focuson) {
    Item item = new Graph(g, args, filter, options, focuson);
    item.fetchFromDatabase(1, Integer.MAX_VALUE - 1, true);
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
