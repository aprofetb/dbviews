package org.dbviews.commons.utils;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

public class StrUtils
{
  private final static Logger logger = Logger.getLogger(StrUtils.class.getName());

  public static String str4mat(String str, Map<String, String> args)
  {
    return str4mat(str, args, null, null);
  }

  public static String str4mat(String str, Map<String, String> args, String defaultValue)
  {
    return str4mat(str, args, defaultValue, null);
  }

  public static String str4mat(String str, Map<String, String> args, String defaultValue, String validator)
  {
    if (StringUtils.isBlank(str) || args == null || args.size() == 0)
      return str;

    for (Map.Entry<String, String> e : args.entrySet())
    {
      String key = e.getKey();
      String value = e.getValue();

      // avoid SQL injection specifying a correct validator
      if (StringUtils.isNotBlank(validator) && !value.matches(validator))
      {
        logger.warning(String.format("Invalid argument value! [key: '%s', value: '%s'] ", key, value));
        continue;
      }

      String regex = String.format("([^\\\\]?(\\\\{2})*)\\{%s\\}", key);
      str = str.replaceAll(regex, "$1" + StringUtils.defaultString(value, defaultValue));
    }

    return str;
  }

  public static String str4mat(String str, String... entries)
  {
    if (entries.length % 2 != 0)
      return str;
    for (int i = 0; i < entries.length; i += 2)
    {
      String key = entries[i];
      String value = entries[i + 1];
      String regex = String.format("([^\\\\]?(\\\\{2})*)\\{%s\\}", key);
      str = str.replaceAll(regex, "$1" + StringUtils.defaultString(value));
    }    
    return str;
  }
}
