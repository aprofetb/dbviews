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
@NamedQueries( { @NamedQuery(name = "DbvTableField.findAll", query = "select o from DbvTableField o") })
@Table(name = "dbv_table_field")
public class DbvTableField
  implements Serializable
{
  @Column(name = "align")
  private String align;
  @Column(name = "column_name", nullable = false)
  private String columnName;
  @Column(name = "exportable")
  private String exportable;
  @Column(name = "field_order")
  private String fieldOrder;
  @Column(name = "type", nullable = false)
  private int type;
  @Id
  @Column(name = "id", nullable = false)
  private int id;
  @Column(name = "name", nullable = false)
  private String name;
  @Column(name = "table_id", nullable = false, insertable = false, updatable = false)
  private int tableId;
  @Column(name = "valign")
  private String valign;
  @Column(name = "visible")
  private String visible;
  @Column(name = "width")
  private String width;
  @ManyToOne
  @JoinColumn(name = "table_id")
  private DbvTable dbvTable;

  public DbvTableField()
  {
  }

  public DbvTableField(String align, String dbField, String exportable, String fieldOrder, int type, String name, DbvTable dbvTable, String valign, String visible, String width)
  {
    this.align = align;
    this.columnName = dbField;
    this.exportable = exportable;
    this.fieldOrder = fieldOrder;
    this.type = type;
    this.name = name;
    this.dbvTable = dbvTable;
    this.valign = valign;
    this.visible = visible;
    this.width = width;
  }

  @XmlElement
  public String getAlign()
  {
    return align;
  }

  public void setAlign(String align)
  {
    this.align = align;
  }

  public String getColumnName()
  {
    return columnName;
  }

  public void setColumnName(String db_field)
  {
    this.columnName = db_field;
  }

  public String getExportable()
  {
    return exportable;
  }

  public void setExportable(String exportable)
  {
    this.exportable = exportable;
  }

  @XmlElement
  public String getFieldOrder()
  {
    return fieldOrder;
  }

  public void setFieldOrder(String field_order)
  {
    this.fieldOrder = field_order;
  }

  @XmlElement
  public int getType()
  {
    return type;
  }

  public void setType(int type)
  {
    this.type = type;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  @XmlElement
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public int getTableId()
  {
    return tableId;
  }

  public void setTableId(int table_id)
  {
    this.tableId = table_id;
  }

  @XmlElement
  public String getValign()
  {
    return valign;
  }

  public void setValign(String valign)
  {
    this.valign = valign;
  }

  public String getVisible()
  {
    return visible;
  }

  public void setVisible(String visible)
  {
    this.visible = visible;
  }

  @XmlElement
  public String getWidth()
  {
    return width;
  }

  public void setWidth(String width)
  {
    this.width = width;
  }

  public void setDbvTable(DbvTable dbvTable)
  {
    this.dbvTable = dbvTable;
  }

  public DbvTable getDbvTable()
  {
    return dbvTable;
  }
}
