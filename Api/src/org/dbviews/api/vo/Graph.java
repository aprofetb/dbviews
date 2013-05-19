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

public class Graph
  extends Tab
{
  private final static Logger logger = Logger.getLogger(Graph.class.getName());

  private String graphType;
  private String labelColumn;
  private String dataColumn;

  public Graph(DbvGraph g, Map<String, String> args, Map<String, String> filter, Map<String, Map<String, String>> options, String focuson)
  {
    headers = new ArrayList<Header>();
    DbvConnection dbvConn = g.getDbvView().getDbvConnection();
    Discoverer disco = new Discoverer(dbvConn.getUrl(), dbvConn.getUsername(), dbvConn.getPassword());
    columnMap = disco.getColumns(g.getSqlQuery());
    for (Map.Entry<String, String> e : columnMap.entrySet())
    {
      String id = e.getKey();
      String columnName = e.getValue();
      headers.add(new Header(id, columnName));
      if (columnName.equalsIgnoreCase(g.getLabelColumn()))
        labelColumn = id;
      if (columnName.equalsIgnoreCase(g.getDataColumn()))
        dataColumn = id;
    }
    id = g.getId();
    label = StrUtils.str4mat(g.getLabel(), args);
    description = StrUtils.str4mat(g.getDescription(), args);
    index = g.getTabIndex();
    query = StrUtils.str4mat(g.getSqlQuery(), args);
    graphType = g.getGraphType();
    this.args = args != null ? args : new HashMap<String, String>();
    this.filter = filter != null ? filter : new HashMap<String, String>();
    this.options = options != null ? options : new HashMap<String, Map<String, String>>();
    this.sortby = sortby != null ? sortby : new HashMap<String, String>();
    this.focuson = focuson;
  }

  public static Tab getInstance(DbvGraph g,
                                Map<String, String> args,
                                Map<String, String> filter,
                                Map<String, Map<String, String>> options,
                                String focuson)
  {
    DbvConnection dbvConn = g.getDbvView().getDbvConnection();
    Tab graph = new Graph(g, args, filter, options, focuson);
    return getInstance(graph, dbvConn, args, filter, options, null, 1, Integer.MAX_VALUE - 1, focuson);
  }

  public void setGraphType(String type)
  {
    this.graphType = type;
  }

  public String getGraphType()
  {
    return graphType;
  }

  public void setLabelColumn(String labelColumn)
  {
    this.labelColumn = labelColumn;
  }

  public String getLabelColumn()
  {
    return labelColumn;
  }

  public void setDataColumn(String dataColumn)
  {
    this.dataColumn = dataColumn;
  }

  public String getDataColumn()
  {
    return dataColumn;
  }

  public List<Map<String, Object>> getData()
  {
    List<Map<String, Object>> data = new LinkedList<Map<String, Object>>();
    if (StringUtils.isBlank(labelColumn) || StringUtils.isBlank(dataColumn))
      return data;
    for (Map<String, String> row : rows)
    {
      Map<String, Object> serie = new HashMap<String, Object>(2);
      serie.put("label", row.get(labelColumn));
      try
      {
        serie.put("data", Integer.valueOf(row.get(dataColumn)));
      }
      catch (NumberFormatException e)
      {
        logger.log(Level.WARNING, "Invalid data column", e);
      }
      data.add(serie);
    }
    return data;
  }

  public String getType()
  {
    return "graph";
  }
}
