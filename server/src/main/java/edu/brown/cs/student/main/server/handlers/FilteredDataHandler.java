package edu.brown.cs.student.main.server.handlers;

import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * FilteredDataHandler is called by the filtered-geojson endpoint in server, and works to retrieve
 * the filtered GeoJSON data and return the formatted response back to the front end.
 */
public class FilteredDataHandler implements Route {

  public GeoJSONObject geoJSONObject;

  public FilteredDataHandler(GeoJSONObject geoJSONObject) {
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
      Double minLat = Double.parseDouble(request.queryParams("minLat"));
      Double maxLat = Double.parseDouble(request.queryParams("maxLat"));
      Double minLong = Double.parseDouble(request.queryParams("minLong"));
      Double maxLong = Double.parseDouble(request.queryParams("maxLong"));

      // If no parameters given, return the full GeoJSON data.
      if (request.queryParams().isEmpty()) {
        responseMap.put("data", geoJSONObject);
        return Utils.toMoshiJson(responseMap);
      }

      // Filter the GeoJSON based on the parameters.
      GeoJSONObject filteredGeoJSONObject =
          geoJSONObject.filterData(minLat, maxLat, minLong, maxLong);

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
