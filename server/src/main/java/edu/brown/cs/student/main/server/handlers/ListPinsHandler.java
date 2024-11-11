package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * ListPinsHandler is called by the list-pins endpoint in server, and works to retrieve the list of
 * pins associated with the user who created them.
 */
public class ListPinsHandler implements Route {

  public StorageInterface storageHandler;

  public ListPinsHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Invoked when a request is made on this route's corresponding path
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return The content to be set in the response
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String uid = request.queryParams("uid");

      // Get all the pins for the database
      List<String> allpins = this.storageHandler.getAllPins();

      // original functionality (getting pins for a specified user)
      List<Map<String, Object>> vals = this.storageHandler.getCollection(uid, "pins");
      List<String> pins = vals.stream().map(pin -> pin.get("pin").toString()).toList();

      responseMap.put("response_type", "success");
      responseMap.put("pins", allpins);
    } catch (Exception e) {
      // Error likely occurred in the storage handler.
      responseMap.put("response_type", "error");
      String[] parts = e.getClass().toString().split("\\.");
      responseMap.put("exception", parts[parts.length - 1]);
      responseMap.put("error_message", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
