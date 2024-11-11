package edu.brown.cs.student.main.server.handlers.census.exceptions;

/** Simple exception to be thrown when the county is not in the Census API. Extends Exception. */
public class CountyNotFoundException extends Exception {

  /**
   * Creates an appropriate error message with the county not found.
   *
   * @param county is the county not found.
   */
  public CountyNotFoundException(String county) {
    super("Could not find county: " + county);
  }
}
