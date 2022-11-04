package config;

import java.util.HashSet;
import java.util.Properties;

public class IOHelper {

  private static StringBuilder valueBuffer = new StringBuilder();

  /**
   * Stores a property value
   * @param propertyName
   * @param v
   */
  private static void store(String propertyName, String v) {
    valueBuffer.append(propertyName + " = " + v + "\n");
  }

  /**
   *
   * @return returns all parameters
   */
  public static String getParams() {
    return valueBuffer.toString();
  }

  /**
   * Returns a property from the config.properties
   * @param prop
   * @param propertyName
   * @param defaultValue
   * @return value
   */
  public static int getProperty(Properties prop, String propertyName, int defaultValue) {

    if (prop.getProperty(propertyName) != null) {
      int v = Integer.parseInt(prop.getProperty(propertyName));
      store(propertyName, "" + v);
      return v;
    }
    else {
      store(propertyName, "" + defaultValue);
      return defaultValue;
    }
  }

  /**
   * Returns a property from the config.properties
   * @param prop
   * @param propertyName
   * @param defaultValue
   * @return value
   */
  public static long getProperty(Properties prop, String propertyName, long defaultValue) {
    if (prop.getProperty(propertyName) != null) {
      long v = Long.parseLong(prop.getProperty(propertyName));
      store(propertyName, "" + v);
      return v;
    }
    else {
      store(propertyName, "" + defaultValue);
      return defaultValue;
    }
  }

  /**
   * Returns a property from the config.properties
   * @param prop
   * @param propertyName
   * @param defaultValues
   * @return value
   */
  public static int[] getProperty(Properties prop, String propertyName, int[] defaultValues) {
    if (prop.getProperty(propertyName) != null) {
      String s = prop.getProperty(propertyName);
      String[] token = s.split(",");
      int[] values = new int[token.length];
      String valuesS = "";
      for (int i = 0; i < token.length; i++) {
        values[i] = Integer.parseInt(token[i]);
        valuesS += values[i] + " ";
      }
      store(propertyName, "" + valuesS);
      return values;
    }
    else {
      store(propertyName, "" + defaultValues);
      return defaultValues;
    }
  }
  /**
   * Returns a property from the config.properties
   * @param prop
   * @param propertyName
   * @param defaultValues
   * @return value
   */
  public static String[] getProperty(Properties prop, String propertyName, String[] defaultValues) {
    if (prop.getProperty(propertyName) != null) {
      String s = prop.getProperty(propertyName);
      String[] values = s.split(",");
      store(propertyName, "" + s);
      return values;
    }
    else {
      store(propertyName, "" + defaultValues);
      return defaultValues;
    }
  }
  /**
   * Returns a property from the config.properties
   * @param prop
   * @param propertyName
   * @param defaultValue
   * @return value
   */
  public static double getProperty(Properties prop, String propertyName, double defaultValue) {
    if (prop.getProperty(propertyName) != null) {
      double v = Double.parseDouble(prop.getProperty(propertyName));
      store(propertyName, "" + v);
      return v;
    }
    else {
      store(propertyName, "" + defaultValue);
      return defaultValue;
    }
  }
  /**
   * Returns a property from the config.properties
   * @param prop
   * @param propertyName
   * @param defaultValue
   * @return value
   */
  public static boolean getProperty(Properties prop, String propertyName, boolean defaultValue) {
    if (prop.getProperty(propertyName) != null) {
      boolean v = false;
      if (prop.getProperty(propertyName).equals("true") || prop.getProperty(propertyName).equals("1")) v = true;
      store(propertyName, "" + v);
      return v;
    }
    else {
      store(propertyName, "" + defaultValue);
      return defaultValue;
    }
  }
  /**
   * Returns a property from the config.properties
   * @param prop
   * @param propertyName
   * @param defaultValue
   * @return value
   */
  public static String getProperty(Properties prop, String propertyName, String defaultValue) {
    if (prop.getProperty(propertyName) != null) {
      String v = prop.getProperty(propertyName);
      store(propertyName, v);
      return v;
    }
    else {
      store(propertyName, "" + defaultValue);
      return defaultValue;
    }
  }
}

