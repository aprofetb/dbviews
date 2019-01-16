package org.dbviews.commons;

import java.util.Map;
import java.util.TreeMap;

public class Constants
{
  public static final Map<String, String> LANGUAGES;

  static
  {
    LANGUAGES = new TreeMap<String, String>();
    LANGUAGES.put("en", "English");
    LANGUAGES.put("es", "Spanish");
  }
}
