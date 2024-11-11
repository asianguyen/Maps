package edu.brown.cs.student.main.server.handlers;

import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * KeywordFilterHandler is called by the filtered-keywords endpoint in server, and works to retrieve
 * the filtered GeoJSON data based on the keyword and return the formatted response back to the
 * front end.
 */
public class KeywordFilterHandler implements Route {

  private GeoJSONObject geoJSONObject;

  public KeywordFilterHandler(GeoJSONObject geoJSONObject) {
    this.geoJSONObject = geoJSONObject;
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
      String keyWord = request.queryParams("keyWord");

      GeoJSONObject filteredGeoJSONObject = geoJSONObject.filterKeyWords(keyWord);
      responseMap.put("data", filteredGeoJSONObject);
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
