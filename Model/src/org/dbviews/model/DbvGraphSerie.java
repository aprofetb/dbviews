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

@Entity
@NamedQueries({
  @NamedQuery(name = "DbvGraphSerie.findAll", query = "select o from DbvGraphSerie o")
})
@Table(name = "dbv_graph_serie")
public class DbvGraphSerie
  implements Serializable
{
  @Id
  @Column(name="id", nullable = false)
  private Integer id;
  @Column(name="label")
  private String label;
  @Column(name="xaxis", nullable = false)
  private String xaxis;
  @Column(name="yaxis", nullable = false)
  private String yaxis;
  @ManyToOne
  @JoinColumn(name = "graph_id")
  private DbvGraph dbvGraph;

  public DbvGraphSerie()
  {
  }

  public DbvGraphSerie(Integer id, String label, String xaxis, String yaxis, DbvGraph dbvGraph)
  {
    this.id = id;
    this.label = label;
    this.xaxis = xaxis;
    this.yaxis = yaxis;
    this.dbvGraph = dbvGraph;
  }

  public String getXaxis()
  {
    return xaxis;
  }

  public void setXaxis(String data)
  {
    this.xaxis = data;
  }

  public Integer getId()
  {
    return id;
  }

  public void setId(Integer id)
  {
    this.id = id;
  }

  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
  }

  public void setDbvGraph(DbvGraph dbvGraph)
  {
    this.dbvGraph = dbvGraph;
  }

  public DbvGraph getDbvGraph()
  {
    return dbvGraph;
  }

  public void setYaxis(String yaxis)
  {
    this.yaxis = yaxis;
  }

  public String getYaxis()
  {
    return yaxis;
  }
}
