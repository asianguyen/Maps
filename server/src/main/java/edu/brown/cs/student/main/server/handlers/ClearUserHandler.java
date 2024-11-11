package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * ClearUserHandler is called by the clear endpoint in server, and works to clear a user from the
 * database.
 */
public class ClearUserHandler implements Route {

  public StorageInterface storageHandler;

  public ClearUserHandler(StorageInterface storageHandler) {
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

      // Remove the user from the database.
      this.storageHandler.clearUser(uid);

      responseMap.put("response_type", "success");
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
