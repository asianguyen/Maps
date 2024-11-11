package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * AddPinHandler is called by the add pin endpoint in server, and works to set each pin to its
 * respective data.
 */
public class AddPinHandler implements Route {

  public StorageInterface storageHandler;

  public AddPinHandler(StorageInterface storageHandler) {
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
      // Collect parameters from the request.
      String uid = request.queryParams("uid");
      String longitude = request.queryParams("long");
      String latitude = request.queryParams("lat");

      Map<String, Object> data = new HashMap<>();
      String pinPos = longitude + "," + latitude;
      // Set the pin to its longitude and latitude
      data.put("pin", pinPos);

      // Get the current pin count to make a unique pinId by index.
      int pinCount = this.storageHandler.getCollection(uid, "pins").size();
      String pinId = "pin-" + pinCount;

      // Use the storage handler to add the document to the database.
      this.storageHandler.addDocument(uid, "pins", pinId, data);

      responseMap.put("response_type", "success");
      responseMap.put("pin", pinPos);
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
