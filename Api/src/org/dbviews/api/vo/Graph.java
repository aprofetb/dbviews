package org.dbviews.api.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import org.dbviews.api.database.Discoverer;
import org.dbviews.commons.utils.StrUtils;
import org.dbviews.model.DbvConnection;
import org.dbviews.model.DbvGraph;
import org.dbviews.model.DbvGraphSerie;

public class Graph
  extends Tab
{
  private final static Logger logger = Logger.getLogger(Graph.class.getName());

  private String graphType;
  private Integer labelColumn;
  private List<Integer> dataColumns;
  private Map<String, List<Integer>> series;

  public Graph(DbvGraph g, Map<String, String> args, Map<Integer, String> filter, Map<Integer, Map<String, String>> options, String focuson)
  {
    headers = new ArrayList<Header>();
    dataColumns = new ArrayList<Integer>();
    String[] dataCols = g.getDataColumn().split(",");
    series = new HashMap<String, List<Integer>>();
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

      if (columnName.equalsIgnoreCase(g.getLabelColumn()))
        labelColumn = id;
      for (String dataCol : dataCols)
        if (columnName.equalsIgnoreCase(dataCol))
          dataColumns.add(id);
    }

    /* for (DbvGraphSerie serie : g.getDbvGraphSerieList())
    {
      String[] cols = serie.getData().split(",");
      List<Integer> data = new ArrayList<Integer>();
      for (String col : cols)
      {
        for (Map.Entry<Integer, Map<String, Object>> e : columnMap.entrySet())
        {
          Integer id = e.getKey();
          Map<String, Object> attrs = e.getValue();
          String columnName = (String)attrs.get("ColumnName");
          if (columnName.equalsIgnoreCase(col))
            data.add(id);
        }
      }
      series.put(serie.getLabel(), data);
    } */
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

  public static Tab getInstance(DbvGraph g,
                                Map<String, String> args,
                                Map<Integer, String> filter,
                                Map<Integer, Map<String, String>> options,
                                String focuson)
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

  public List getData()
  {
    /* Object[][] dataValues = new Object[rows.size()][];
    if (this.series.size() == 0)
      return dataValues;
    int r = 0;
    for (Map<Integer, Object> row : rows)
    {
      Object[] values = new Object[this.series.size()];
      for (int i = 0; i < this.series.size(); i++)
        values[i] = row.get(this.series.get(i));
      dataValues[r++] = values;
    } */

    List series = new ArrayList();
    for (Map<Integer, Object> row : rows)
    {
      if ("pie".equals(graphType))
      {
        Map<String, Object> serie = new HashMap<String, Object>(2);
        serie.put("label", row.get(labelColumn));
        serie.put("data", row.get(dataColumns.get(0)));
        series.add(serie);
      }
      else if ("bars".equals(graphType))
      {
        Object[] values = new Object[dataColumns.size()];
        for (int i = 0; i < dataColumns.size(); i++)
          values[i] = row.get(dataColumns.get(i));
        series.add(values);
      }
    }
    return series;
  }

  public String getType()
  {
    return "graph";
  }
}
