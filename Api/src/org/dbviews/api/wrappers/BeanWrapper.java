package org.dbviews.api.wrappers;

import java.beans.BeanInfo;

import java.beans.Introspector;

import java.beans.PropertyDescriptor;

import java.lang.reflect.Method;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;

public class BeanWrapper
  extends HashMap<String, Object>
{
  public BeanWrapper(Object obj)
  {
    super();
    try
    {
      BeanInfo info = Introspector.getBeanInfo(obj.getClass());
      for (PropertyDescriptor pd : info.getPropertyDescriptors())
      {
        Method reader = pd.getReadMethod();
        if (reader != null && reader.isAnnotationPresent(XmlElement.class))
          put(pd.getName(), reader.invoke(obj));
      }
    }
    catch (Exception e)
    {
    }
  }

  public static BeanWrapper getInstance(Object obj)
  {
    return new BeanWrapper(obj);
  }
}
