package edu.brown.cs.student.main.server.handlers.census.exceptions;

/** Simple exception to be thrown when there is an error with the datasource. Extends Exception. */
public class DatasourceException extends Exception {

  /**
   * Creates the error message through the superclass.
   *
   * @param message the message to be displayed.
   */
  public DatasourceException(String message) {
    super(message);
  }
}
