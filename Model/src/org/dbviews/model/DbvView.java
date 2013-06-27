package org.dbviews.model;

import java.io.Serializable;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import javax.xml.bind.annotation.XmlElement;

@Entity
@NamedQueries( { @NamedQuery(name = "DbvView.findAll", query = "select o from DbvView o"),
                 @NamedQuery(name = "DbvView.findById", query = "select o from DbvView o where o.id = :id") })
@Table(name = "dbv_view")
public class DbvView
  implements Serializable
{
  @Column(name = "description", nullable = false)
  private String description;
  @Id
  @Column(name = "id", nullable = false)
  private int id;
  @Column(name = "auth_principals", nullable = false)
  private String authPrincipals;
  @OneToMany(mappedBy = "dbvView")
  private List<DbvGraph> dbvGraphList;
  @OneToMany(mappedBy = "dbvView")
  private List<DbvTable> dbvTableList;
  @ManyToOne
  @JoinColumn(name = "connection_id")
  private DbvConnection dbvConnection;

  public DbvView()
  {
  }

  public DbvView(DbvConnection dbvConnection, String description)
  {
    this.dbvConnection = dbvConnection;
    this.description = description;
  }

  @XmlElement
  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  @XmlElement
  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public List<DbvGraph> getDbvGraphList()
  {
    return dbvGraphList;
  }

  public void setDbvGraphList(List<DbvGraph> dbvGraphList)
  {
    this.dbvGraphList = dbvGraphList;
  }

  public DbvGraph addDbvGraph(DbvGraph dbvGraph)
  {
    getDbvGraphList().add(dbvGraph);
    dbvGraph.setDbvView(this);
    return dbvGraph;
  }

  public DbvGraph removeDbvGraph(DbvGraph dbvGraph)
  {
    getDbvGraphList().remove(dbvGraph);
    dbvGraph.setDbvView(null);
    return dbvGraph;
  }

  public List<DbvTable> getDbvTableList()
  {
    return dbvTableList;
  }

  public void setDbvTableList(List<DbvTable> dbvTableList)
  {
    this.dbvTableList = dbvTableList;
  }

  public DbvTable addDbvTable(DbvTable dbvTable)
  {
    getDbvTableList().add(dbvTable);
    dbvTable.setDbvView(this);
    return dbvTable;
  }

  public DbvTable removeDbvTable(DbvTable dbvTable)
  {
    getDbvTableList().remove(dbvTable);
    dbvTable.setDbvView(null);
    return dbvTable;
  }

  public DbvConnection getDbvConnection()
  {
    return dbvConnection;
  }

  public void setDbvConnection(DbvConnection dbvConnection)
  {
    this.dbvConnection = dbvConnection;
  }

  public void setAuthPrincipals(String authPrincipals)
  {
    this.authPrincipals = authPrincipals;
  }

  public String getAuthPrincipals()
  {
    return authPrincipals;
  }
}
