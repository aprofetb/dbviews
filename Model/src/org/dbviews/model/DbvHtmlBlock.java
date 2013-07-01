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
  @NamedQuery(name = "DbvHtmlBlock.findAll", query = "select o from DbvHtmlBlock o"),
  @NamedQuery(name = "DbvHtmlBlock.findById", query = "select o from DbvHtmlBlock o where o.id = :id")
})
@Table(name = "dbv_html_block")
public class DbvHtmlBlock
  implements Serializable
{
  @Column(name="description", nullable = false)
  private String description;
  @Id
  @Column(name="id", nullable = false)
  private Integer id;
  @Column(name="label", nullable = false)
  private String label;
  @Column(name="sql_query", nullable = false)
  private String sqlQuery;
  @Column(name="tab_index", nullable = false)
  private Integer tabIndex;
  @ManyToOne
  @JoinColumn(name = "view_id")
  private DbvView dbvView;

  public DbvHtmlBlock()
  {
  }

  public DbvHtmlBlock(String description, Integer id, String label, String sql_query, Integer tab_index, DbvView dbvView)
  {
    this.description = description;
    this.id = id;
    this.label = label;
    this.sqlQuery = sql_query;
    this.tabIndex = tab_index;
    this.dbvView = dbvView;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
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

  public String getSqlQuery()
  {
    return sqlQuery;
  }

  public void setSqlQuery(String sql_query)
  {
    this.sqlQuery = sql_query;
  }

  public Integer getTabIndex()
  {
    return tabIndex;
  }

  public void setTabIndex(Integer tab_index)
  {
    this.tabIndex = tab_index;
  }

  public void setDbvView(DbvView dbvView)
  {
    this.dbvView = dbvView;
  }

  public DbvView getDbvView()
  {
    return dbvView;
  }
}
