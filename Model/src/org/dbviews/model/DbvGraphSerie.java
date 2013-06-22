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
  @Column(name="data", nullable = false)
  private String data;
  @Id
  @Column(name="id", nullable = false)
  private Integer id;
  @Column(name="label")
  private String label;
  @ManyToOne
  @JoinColumn(name = "graph_id")
  private DbvGraph dbvGraph;

  public DbvGraphSerie()
  {
  }

  public DbvGraphSerie(String data, Integer id, String label, DbvGraph dbvGraph)
  {
    this.data = data;
    this.id = id;
    this.label = label;
    this.dbvGraph = dbvGraph;
  }

  public String getData()
  {
    return data;
  }

  public void setData(String data)
  {
    this.data = data;
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
}
