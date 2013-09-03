package org.dbviews.model;

import java.io.Serializable;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@NamedQueries( { @NamedQuery(name = "DbvConnection.findAll", query = "select o from DbvConnection o"),
                 @NamedQuery(name = "DbvConnection.findById", query = "select o from DbvConnection o where o.id = :id") })
@Table(name = "dbv_connection")
public class DbvConnection
  implements Serializable
{
  @Column(name = "description", nullable = false)
  private String description;
  @Id
  @Column(name = "id", nullable = false)
  private int id;
  @Column(name = "password", nullable = false)
  private String password;
  @Column(name = "url", nullable = false)
  private String url;
  @Column(name = "username", nullable = false)
  private String username;
  @OneToMany(mappedBy = "dbvConnection")
  private List<DbvView> dbvViewList;

  public DbvConnection()
  {
  }

  public DbvConnection(String description, String password, String url, String username)
  {
    this.description = description;
    this.password = password;
    this.url = url;
    this.username = username;
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

  @XmlElement
  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  @XmlElement
  public String getUrl()
  {
    return url;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  @XmlElement
  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }


  public DbvView addDbvView(DbvView dbvView)
  {
    getDbvViewList().add(dbvView);
    dbvView.setDbvConnection(this);
    return dbvView;
  }

  public DbvView removeDbvView(DbvView dbvView)
  {
    getDbvViewList().remove(dbvView);
    dbvView.setDbvConnection(null);
    return dbvView;
  }

  public List<DbvView> getDbvViewList()
  {
    return dbvViewList;
  }

  public void setDbvViewList(List<DbvView> dbvViewList)
  {
    this.dbvViewList = dbvViewList;
  }
}
