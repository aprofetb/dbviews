package org.dbviews.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import javax.xml.bind.annotation.XmlElement;

@Entity
@NamedQueries({
              @NamedQuery(name = "DbvGraph.findAll", query = "select o from DbvGraph o"),
              @NamedQuery(name = "DbvGraph.findById", query = "select o from DbvGraph o where o.id = :id")
  })
@Table(name = "dbv_graph")
public class DbvGraph implements Serializable, Comparable {
  @Column(name = "description", nullable = false)
  private String description;
  @Id
  @Column(name = "id", nullable = false)
  private int id;
  @Column(name = "label", nullable = false)
  private String label;
  @Column(name = "sql_query", nullable = false)
  private String sqlQuery;
  @Column(name = "tab_index", nullable = false)
  private int tabIndex;
  @ManyToOne
  @JoinColumn(name = "view_id")
  private DbvView dbvView;
  @Column(name = "graph_type", nullable = false)
  private String graphType;
  @Column(name = "serie_column")
  private String serieColumn;
  @Column(name = "xaxis_column")
  private String xaxisColumn;
  @Column(name = "yaxis_column")
  private String yaxisColumn;
  @Column(name = "xmode")
  private String xmode;
  @Column(name = "ymode")
  private String ymode;
  @Column(name = "width", nullable = false)
  private int width;
  @Column(name = "height", nullable = false)
  private int height;
  @Column(name = "legend_position")
  private String legendPosition;
  @Column(name = "filter_position")
  private String filterPosition;
  @Column(name = "toolbar_position")
  private String toolbarPosition;
  @Column(name = "csv_separator")
  private String csvSeparator;

  public DbvGraph() {
  }

  public DbvGraph(String description, String label, String sql_query, int tab_index, DbvView dbvView) {
    this.description = description;
    this.label = label;
    this.sqlQuery = sql_query;
    this.tabIndex = tab_index;
    this.dbvView = dbvView;
  }

  @XmlElement
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @XmlElement
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @XmlElement
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getSqlQuery() {
    return sqlQuery;
  }

  public void setSqlQuery(String sqlQuery) {
    this.sqlQuery = sqlQuery;
  }

  @XmlElement
  public int getTabIndex() {
    return tabIndex;
  }

  public void setTabIndex(int tabIndex) {
    this.tabIndex = tabIndex;
  }

  public DbvView getDbvView() {
    return dbvView;
  }

  public void setDbvView(DbvView dbvView) {
    this.dbvView = dbvView;
  }

  public int compareTo(Object o) {
    DbvGraph t = (DbvGraph) o;
    return tabIndex > t.tabIndex ? 1 : tabIndex < t.tabIndex ? -1 : 0;
  }

  public void setSerieColumn(String labelColumn) {
    this.serieColumn = labelColumn;
  }

  public String getSerieColumn() {
    return serieColumn;
  }

  public void setXaxisColumn(String dataColumn) {
    this.xaxisColumn = dataColumn;
  }

  public String getXaxisColumn() {
    return xaxisColumn;
  }

  public void setGraphType(String type) {
    this.graphType = type;
  }

  public String getGraphType() {
    return graphType;
  }

  public void setYaxisColumn(String yaxisColumn) {
    this.yaxisColumn = yaxisColumn;
  }

  public String getYaxisColumn() {
    return yaxisColumn;
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

  public void setCsvSeparator(String csvSeparator) {
    this.csvSeparator = csvSeparator;
  }

  public String getCsvSeparator() {
    return csvSeparator;
  }
}
