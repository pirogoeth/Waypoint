package me.pirogoeth.waypoint.util;

import java.util.logging.Logger;

public class RegistryException extends Exception
{
  protected static String error;
  public Logger log = Logger.getLogger("Minecraft");

  public RegistryException() {
    error = "An unknown error occurred.";
    this.log.warning(error);
  }
  public RegistryException(String err) {
    super(err);
    error = err;
    this.log.warning(error);
  }
  public static String getError() {
    return error;
  }
}
