package org.dbviews.api.vo;

import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.TreeMap;

public class OrderByVO
{
  Map<Integer, Map.Entry<String, Order>> orderFields;

  public OrderByVO()
  {
    orderFields = new TreeMap<Integer, Map.Entry<String, Order>>();
  }

  public OrderByVO(String field)
  {
    this();
    orderFields.put(0, new HashMap.SimpleEntry<String, Order>(field, Order.Asc));
  }

  public OrderByVO(String field, Order order)
  {
    this();
    orderFields.put(0, new HashMap.SimpleEntry<String, Order>(field, order));
  }

  public OrderByVO(List<Header> headers)
  {
    this();
    for (Header header : headers)
      orderFields.put(header.getOrderPriority(), new HashMap.SimpleEntry<String, Order>(header.getDbColumnName(), header.getOrder()));
  }

  public void setOrderFields(Map<Integer, Map.Entry<String, Order>> orderFields)
  {
    this.orderFields = orderFields;
  }

  public Map<Integer, Map.Entry<String, Order>> getOrderFields()
  {
    return orderFields;
  }

  public int size()
  {
    return orderFields.size();
  }

  public boolean containsField(String field)
  {
    for (Integer priority : orderFields.keySet())
      if (orderFields.get(priority).getKey().equals(field))
        return true;
    return false;
  }

  public Order getOrder(String field)
  {
    for (Integer priority : orderFields.keySet())
    {
      Map.Entry<String, Order> fOrder = orderFields.get(priority);
      if (fOrder.getKey().equals(field))
        return fOrder.getValue();
    }
    return null;
  }

  public int getPriority(String field)
  {
    for (Integer priority : orderFields.keySet())
      if (orderFields.get(priority).getKey().equals(field))
        return priority;
    return Integer.MAX_VALUE - 1;
  }

  @Override
  public boolean equals(Object obj)
  {
    OrderByVO o = obj != null && obj instanceof OrderByVO ? (OrderByVO) obj : null;
    return o != null && this.hashCode() == o.hashCode();
  }

  @Override
  public int hashCode()
  {
    int hc = 0;
    for (Integer priority : orderFields.keySet())
    {
      Map.Entry<String, Order> fOrder = orderFields.get(priority);
      hc ^= fOrder.getKey().hashCode() ^ fOrder.getValue().hashCode();
    }
    return hc;
  }

  @Override
  public String toString()
  {
    String oby = "";
    int i = 0;
    for (Integer priority : orderFields.keySet())
    {
      Map.Entry<String, Order> fOrder = orderFields.get(priority);
      oby += String.format("%s%s %s", i++ == 0 ? "" : ",", fOrder.getKey(), fOrder.getValue());
    }
    return oby;
  }

  public static OrderByVO getOrderBy(String parameter)
  {
    if (parameter == null)
      return null;
    String[] splc = parameter.split("\\s*,\\s*");
    OrderByVO oby = new OrderByVO();
    for (int i = 0; i < splc.length; i++)
    {
      String[] spls = splc[i].split("\\s+");
      oby.orderFields.put(i + 1, new HashMap.SimpleEntry<String, Order>(spls[0], spls.length == 2 ? Order.valueOf(spls[1]) : Order.Asc));
    }
    return oby.orderFields.size() > 0 ? oby : null;
  }

  public static OrderByVO getOrderBy(String parameter, String defField, Order defOrder)
  {
    String[] spl = parameter == null ? new String[] { } : parameter.split("\\s+");
    String field = spl.length > 0 ? spl[0] : defField;
    Order order = spl.length == 2 ? Order.valueOf(spl[1]) : defOrder;
    return new OrderByVO(field, order);
  }
}
