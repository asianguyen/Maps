package edu.brown.cs.student.main.server.handlers.census;

import edu.brown.cs.student.main.server.serializers.MapSerializer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * BroadbandHandler is called by the broadband endpoint in server, and works to retrieve the
 * specified data from the Census API, and then send the formatted response back.
 */
public class BroadbandHandler implements Route {

  // Instance of the interface type to handle the Census API
  private final BroadbandDatasource datasource;

  /**
   * Constructor for BroadbandHandler.
   *
   * @param datasource is the Census Datasource to retrieve data from.
   */
  public BroadbandHandler(BroadbandDatasource datasource) {
    this.datasource = datasource;
  }

  /**
   * The handle method is prescribed by the Route interface. Uses the request parameters to search
   * for the appropriate data in the data source.
   *
   * @param request contains the parameters of the API request.
   * @param response is an unused parameter representing the response to the query.
   * @return a hash map from strings to object values.
   */
  @Override
  public Object handle(Request request, Response response) {
    // Initialize the response format.
    Map<String, Object> responseData = new HashMap<>();

    // Store the state and county of the request.
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    // Check that two parameters were specified.
    if (request.queryParams().size() != 2) {
      responseData.put("result", "error");
      responseData.put("error_type", "Invalid number of parameters specified!");
      responseData.put("params_given", request.queryParams());
      responseData.put("params_required", List.of("state", "county"));
      return new MapSerializer().serialize(responseData);
    }

    // Add inputs to the response data.
    responseData.put("query_state", state);
    responseData.put("query_county", county);

    // Check that both path and header were given.
    if (state == null || county == null) {
      responseData.put("result", "error");
      responseData.put("error_type", "Missing parameter!");
      responseData.put("error_arg", (state == null) ? "state" : "county");
      return new MapSerializer().serialize(responseData);
    }

    try {
      // Get the data from the Datasource.
      CensusData censusData = this.datasource.getBroadbandData(state, county);

      // Add relevant fields to the result.
      responseData.put("result", "success");
      responseData.put("data", createResponseData(censusData));

    } catch (Exception e) {
      // Add descriptive error message to the result.
      responseData.put("result", "error");
      String[] parts = e.getClass().toString().split("\\.");
      responseData.put("exception", parts[parts.length - 1]);
      responseData.put("error_message", e.getMessage());
    }
    return new MapSerializer().serialize(responseData);
  }

  /**
   * Helper to create a map of the census data, with the state and county codes and the broadband
   * percentage.
   */
  private String[] createResponseData(CensusData data) {
    String[] responseData = new String[3];
    responseData[0] = data.state();
    responseData[1] = data.county();
    responseData[2] = data.broadband();
    return responseData;
  }
}
