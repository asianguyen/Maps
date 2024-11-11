package edu.brown.cs.student.main.server.handlers.census.exceptions;

/** Simple exception to be thrown when the state is not in the Census API. Extends Exception. */
public class StateNotFoundException extends Exception {

  /**
   * Creates an appropriate error message with the state not found.
   *
   * @param state is the state not found.
   */
  public StateNotFoundException(String state) {
    super("Could not find state: " + state);
  }
}
