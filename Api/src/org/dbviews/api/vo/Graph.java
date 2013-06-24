package org.dbviews.api.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.dbviews.api.database.Discoverer;
import org.dbviews.commons.utils.StrUtils;
import org.dbviews.model.DbvConnection;
import org.dbviews.model.DbvGraph;

public class Graph
  extends Tab
{
  private String graphType;
  private Integer serieColumn;
  private Integer xaxisColumn;
  private Integer yaxisColumn;

  public Graph(DbvGraph g, Map<String, String> args, Map<Integer, String> filter, Map<Integer, Map<String, String>> options, String focuson)
  {
    headers = new ArrayList<Header>();
    DbvConnection dbvConn = g.getDbvView().getDbvConnection();
    Discoverer disco = new Discoverer(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
    columnMap = disco.getColumns(g.getSqlQuery(), true);
    for (Map.Entry<Integer, Map<String, Object>> e : columnMap.entrySet())
    {
      Integer id = e.getKey();
      Map<String, Object> attrs = e.getValue();
      String columnName = (String)attrs.get("ColumnName");
      int columnType = (Integer)attrs.get("ColumnType");
      headers.add(new Header(id, columnName, columnType));

      if (columnName.equalsIgnoreCase(g.getSerieColumn()))
        serieColumn = id;
      if (columnName.equalsIgnoreCase(g.getXaxisColumn()))
        xaxisColumn = id;
      if (columnName.equalsIgnoreCase(g.getYaxisColumn()))
        yaxisColumn = id;
    }
    id = g.getId();
    label = StrUtils.str4mat(g.getLabel(), args);
    description = StrUtils.str4mat(g.getDescription(), args);
    index = g.getTabIndex();
    query = StrUtils.str4mat(g.getSqlQuery(), args);
    graphType = g.getGraphType();
    this.args = args != null ? args : new HashMap<String, String>();
    this.filter = filter != null ? filter : new HashMap<Integer, String>();
    this.options = options != null ? options : new HashMap<Integer, Map<String, String>>();
    this.sortby = sortby != null ? sortby : new HashMap<Integer, String>();
    this.focuson = focuson;
  }

  public static Tab getInstance(DbvGraph g, Map<String, String> args, Map<Integer, String> filter, Map<Integer, Map<String, String>> options, String focuson)
  {
    DbvConnection dbvConn = g.getDbvView().getDbvConnection();
    Tab graph = new Graph(g, args, filter, options, focuson);
    return getInstance(graph, dbvConn, filter, options, null, 1, Integer.MAX_VALUE - 1);
  }

  public void setGraphType(String type)
  {
    this.graphType = type;
  }

  public String getGraphType()
  {
    return graphType;
  }

  public void setSerieColumn(Integer serieColumn)
  {
    this.serieColumn = serieColumn;
  }

  public Integer getSerieColumn()
  {
    return serieColumn;
  }

  public void setXaxisColumn(Integer xaxisColumn)
  {
    this.xaxisColumn = xaxisColumn;
  }

  public Integer getXaxisColumn()
  {
    return xaxisColumn;
  }

  public void setYaxisColumn(Integer yaxisColumn)
  {
    this.yaxisColumn = yaxisColumn;
  }

  public Integer getYaxisColumn()
  {
    return yaxisColumn;
  }

  public List getData()
  {
    List series = new ArrayList();
    Map<String, Object> seriesMap = new HashMap<String, Object>();
    for (Map<Integer, Object> row : rows)
    {
      if ("pie".equals(graphType))
      {
        Map<String, Object> serie = new HashMap<String, Object>(2);
        if (serieColumn != null)
          serie.put("label", row.get(serieColumn));
        List<Object[]> points = (List<Object[]>)serie.get("data");
        if (points == null)
        {
          points = new ArrayList<Object[]>();
          serie.put("data", points);
        }
        points.add(new Object[] { row.get(xaxisColumn), row.get(yaxisColumn) });
        series.add(serie);
      }
      else if ("bars".equals(graphType) || "lines".equals(graphType) || "points".equals(graphType))
      {
        String label = serieColumn != null && row.containsKey(serieColumn) ? String.valueOf(row.get(serieColumn)) : "";
        List<Object[]> points = (List<Object[]>)seriesMap.get(label);
        if (points == null)
        {
          points = new ArrayList<Object[]>();
          seriesMap.put(label, points);
        }
        Object[] point = new Object[] { Integer.valueOf(String.valueOf(row.get(xaxisColumn))), row.get(yaxisColumn) };
        points.add(point);
      }
    }
    if (serieColumn == null)
    {
      List<Object[]> points = (List<Object[]>)seriesMap.get("");
      return points;
    }
    for (Map.Entry<String, Object> e : seriesMap.entrySet())
    {
      String label = e.getKey();
      List<Object[]> points = (List<Object[]>)e.getValue();
      Map<String, Object> serie = new HashMap<String, Object>(2);
      serie.put("label", label);
      serie.put("data", points);
      series.add(serie);
    }
    return series;
  }

  public String getType()
  {
    return "graph";
  }
}
