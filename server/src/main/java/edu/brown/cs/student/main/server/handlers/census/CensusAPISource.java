package edu.brown.cs.student.main.server.handlers.census;

import edu.brown.cs.student.main.server.handlers.census.exceptions.CountyNotFoundException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.StateNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements BroadbandDatasource for use in the BroadbandHandler, and accesses the
 * Census API directly to retrieve broadband data for the specified state and county.
 */
public class CensusAPISource implements BroadbandDatasource {
  private Map<String, String> stateCodes; // Will store a map of state to state code.
  private boolean haveStateCodes; // Will track if the state codes have been retrieved yet.

  /** Constructor for the CensusAPISource. Initialize instance variables. */
  public CensusAPISource() {
    this.stateCodes = new HashMap<>();
    this.haveStateCodes = false;
  }

  /**
   * Override the getBroadbandData method from the BroadbandDatasource interface. Retrieves the data
   * directly from the Census API and formats it as CensusData.
   *
   * @param state is the state to be searched.
   * @param county is the county within the state to be searched.
   * @return a CensusData object of the data from the Census API.
   * @throws IOException
   * @throws StateNotFoundException
   * @throws CountyNotFoundException
   * @throws DatasourceException
   */
  @Override
  public CensusData getBroadbandData(String state, String county)
      throws IOException, StateNotFoundException, CountyNotFoundException, DatasourceException {

    // Get state and county code, and use them to query the census.
    List<String> codes = this.getCodes(state, county);
    List<List<String>> results =
        CensusAPIUtilities.queryCensus(
            "/data/2021/acs/acs1/subject/variables?"
                + "get=NAME,S2802_C03_022E&for=county:"
                + codes.get(1)
                + "&in=state:"
                + codes.get(0));

    // Construct a CensusData object with the results and return it.
    List<String> data = results.get(1);
    return new CensusData(data.get(0), data.get(1) + "%", data.get(2), data.get(3), this.getTime());
  }

  /**
   * Helper method to retrieve the state and country codes to be queried by in the Census API.
   *
   * @param state to find the state code of.
   * @param county to find the county code of.
   * @return a list of the state and county codes.
   * @throws IOException
   * @throws StateNotFoundException
   * @throws CountyNotFoundException
   * @throws DatasourceException
   */
  private List<String> getCodes(String state, String county)
      throws IOException, StateNotFoundException, CountyNotFoundException, DatasourceException {

    // Check and load state codes.
    if (!this.haveStateCodes) {
      this.fetchAllStateCodes();
      this.haveStateCodes = true;
    }

    // Get State code from stateCodes instance variable.
    if (!this.stateCodes.containsKey(state)) {
      throw new StateNotFoundException(state);
    }
    String stateCode = this.stateCodes.get(state);

    // Use CensusAPIUtilities to query the census.
    List<List<String>> counties =
        CensusAPIUtilities.queryCensus(
            "/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode);

    // Look for the county with the corresponding name.
    String countyCode = "";
    for (List<String> list : counties) {
      if (list.get(0).equals(county + ", " + state)) {
        countyCode = list.get(2);
      }
    }

    // Make sure county was found.
    if (!(countyCode.length() > 0)) {
      throw new CountyNotFoundException(county);
    }

    // Return a list of the county and state codes.
    return List.of(stateCode, countyCode);
  }

  /**
   * Private method called with the first census query. Gets the state codes for all U.S. States and
   * stores them in a HashMap from String (name) to int (code).
   *
   * @throws IOException
   * @throws DatasourceException
   */
  private void fetchAllStateCodes() throws IOException, DatasourceException {
    // Query the census for the state codes.
    List<List<String>> results =
        CensusAPIUtilities.queryCensus("/data/2010/dec/sf1?get=NAME&for=state:*");

    // Convert the list to an instance variable hashmap and remove the header.
    for (List<String> list : results) {
      this.stateCodes.put(list.get(0), list.get(1));
    }
    this.stateCodes.remove("NAME");
  }

  /**
   * Simple method used to get the current local time.
   *
   * @return the time in "yyyy-MM-dd HH:mm:ss" format.
   */
  private String getTime() {
    LocalDateTime dateAndTime = LocalDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return dateAndTime.format(format);
  }
}
