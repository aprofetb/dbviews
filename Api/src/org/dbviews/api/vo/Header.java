package org.dbviews.api.vo;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;

import org.dbviews.model.DbvTableField;

public class Header implements Comparable<Header> {
  private Integer id;
  private String columnName;
  private String dbColumnName;
  private int type;
  private String width;
  private Align align;
  private VAlign vAlign;
  private Order order;
  private int orderPriority = 0;
  private boolean visible = true;
  private boolean exportable = true;
  private boolean filterable = true;
  private boolean sortable = true;

  public Header(DbvTableField f) {
    this.id = f.getId();
    this.columnName = f.getName();
    this.dbColumnName = f.getColumnName();
    this.type = f.getType();
    this.width = f.getWidth();
    this.align = StringUtils.isNotBlank(f.getAlign()) ? Align.valueOf(f.getAlign()) : Align.Left;
    this.vAlign = StringUtils.isNotBlank(f.getValign()) ? VAlign.valueOf(f.getValign()) : VAlign.Top;
    String[] os = StringUtils.isNotBlank(f.getFieldOrder()) ? f.getFieldOrder().split("::") : null;
    this.order = os == null ? Order.None : Order.valueOf(os[os.length == 2 ? 1 : 0]);
    this.orderPriority = os != null && os.length == 2 ? Integer.valueOf(os[0]) : 0;
    this.visible = "Y".equals(f.getVisible());
    this.exportable = "Y".equals(f.getExportable());
    this.filterable = "Y".equals(f.getFilterable());
    this.sortable = "Y".equals(f.getSortable());
  }

  public Header(int id, Header f) {
    this.id = id;
    this.columnName = f.columnName;
    this.dbColumnName = f.dbColumnName;
    this.type = f.type;
    this.width = f.width;
    this.align = f.align;
    this.vAlign = f.vAlign;
    this.order = f.order;
    this.orderPriority = f.orderPriority;
    this.visible = f.visible;
    this.exportable = f.exportable;
    this.filterable = f.filterable;
    this.sortable = f.sortable;
  }

  public Header(Integer id, String columnName, int type) {
    this.id = id;
    this.columnName = columnName;
    this.dbColumnName = columnName;
    this.type = type;
    this.width = "";
    this.align = Align.Left;
    this.vAlign = VAlign.Top;
    this.order = Order.None;
  }

  public Header(Integer id, String columnName, String dbColumnName, int type, String width, Align align,
                VAlign vAlign) {
    this.id = id;
    this.columnName = columnName;
    this.dbColumnName = dbColumnName;
    this.type = type;
    this.width = width;
    this.align = align;
    this.vAlign = vAlign;
    this.order = Order.None;
  }

  public Header(Integer id, String columnName, String dbColumnName, int type, String width, Align align, VAlign vAlign,
                Order order, boolean visible, boolean exportable) {
    this.id = id;
    this.columnName = columnName;
    this.dbColumnName = dbColumnName;
    this.type = type;
    this.width = width;
    this.align = align;
    this.vAlign = vAlign;
    this.order = order;
    this.visible = visible;
    this.exportable = exportable;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Header))
      return false;
    if (this == obj)
      return true;
    Header header = (Header) obj;
    return header != null && columnName.equals(header.columnName);
  }

  public int hashCode() {
    return this.columnName.hashCode();
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setWidth(String width) {
    this.width = width;
  }

  public String getWidth() {
    return width;
  }

  public void setDbColumnName(String dbColumnName) {
    this.dbColumnName = dbColumnName;
  }

  @XmlTransient
  public String getDbColumnName() {
    return dbColumnName;
  }

  public void setAlign(Align align) {
    this.align = align;
  }

  public Align getAlign() {
    return align;
  }

  public void setVAlign(VAlign vAlign) {
    this.vAlign = vAlign;
  }

  public VAlign getVAlign() {
    return vAlign;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getType() {
    return type;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrderPriority(int orderPosition) {
    this.orderPriority = orderPosition;
  }

  public int getOrderPriority() {
    return orderPriority;
  }

  public boolean isOrdered() {
    return this.order != Order.Locked && this.order != Order.None;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setExportable(boolean exportable) {
    this.exportable = exportable;
  }

  public boolean isExportable() {
    return exportable;
  }

  public void setFilterable(boolean filterable) {
    this.filterable = filterable;
  }

  public boolean isFilterable() {
    return filterable;
  }

  public void setSortable(boolean sortable) {
    this.sortable = sortable;
  }

  public boolean isSortable() {
    return sortable;
  }

  @XmlTransient
  public int getHashCode() {
    return dbColumnName.hashCode();
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return id;
  }

  @Override
  public int compareTo(Header h) {
    return this.getOrderPriority() > h.getOrderPriority() ? 1 : this.getOrderPriority() < h.getOrderPriority() ? -1 : 0;
  }
}
