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
@NamedQueries({
              @NamedQuery(name = "DbvTable.findAll", query = "select o from DbvTable o"),
              @NamedQuery(name = "DbvTable.findById", query = "select o from DbvTable o where o.id = :id")
  })
@Table(name = "dbv_table")
public class DbvTable implements Serializable, Comparable {
  @Column(name = "description", nullable = false)
  private String description;
  @Id
  @Column(name = "id", nullable = false)
  private int id;
  @Column(name = "label", nullable = false)
  private String label;
  @Column(name = "sql_query", nullable = false)
  private String sqlQuery;
  @Column(name = "sql_query_index")
  private String sqlQueryIndex;
  @Column(name = "tab_index", nullable = false)
  private int tabIndex;
  @ManyToOne
  @JoinColumn(name = "view_id")
  private DbvView dbvView;
  @OneToMany(mappedBy = "dbvTable")
  private List<DbvTableField> dbvTableFieldList;
  @Column(name = "filter_position")
  private String filterPosition;
  @Column(name = "toolbar_position")
  private String toolbarPosition;

  public DbvTable() {
  }

  public DbvTable(String description, String label, String sqlQuery, int tabIndex, DbvView dbvView) {
    this.description = description;
    this.label = label;
    this.sqlQuery = sqlQuery;
    this.tabIndex = tabIndex;
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

  public void setSqlQueryIndex(String sqlQueryIndex) {
    this.sqlQueryIndex = sqlQueryIndex;
  }

  public String getSqlQueryIndex() {
    return sqlQueryIndex;
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
    DbvTable t = (DbvTable) o;
    return tabIndex > t.tabIndex ? 1 : tabIndex < t.tabIndex ? -1 : 0;
  }

  public void setDbvTableFieldList(List<DbvTableField> dbvTableFieldList) {
    this.dbvTableFieldList = dbvTableFieldList;
  }

  public List<DbvTableField> getDbvTableFieldList() {
    return dbvTableFieldList;
  }

  public DbvTableField addDbvTableField(DbvTableField dbvTableField) {
    getDbvTableFieldList().add(dbvTableField);
    dbvTableField.setDbvTable(this);
    return dbvTableField;
  }

  public DbvTableField removeDbvTableField(DbvTableField dbvTableField) {
    getDbvTableFieldList().remove(dbvTableField);
    dbvTableField.setDbvTable(null);
    return dbvTableField;
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
