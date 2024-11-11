package edu.brown.cs.student.main.server.handlers.census.caching;

import edu.brown.cs.student.main.server.handlers.census.BroadbandDatasource;
import edu.brown.cs.student.main.server.handlers.census.CensusData;
import edu.brown.cs.student.main.server.handlers.census.exceptions.CountyNotFoundException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.StateNotFoundException;
import java.io.IOException;

/**
 * CachingCensusSource wraps a BroadbandDatasource with a GenericCache to create a more time
 * efficient search of the Census API.
 */
public class CachingCensusSource implements BroadbandDatasource {
  private final BroadbandDatasource original; // The original census datasource.
  private final GenericCache<String, CensusData>
      censusCache; // A generic cache to store CensusData.

  /**
   * Constructor initializes the instance variables.
   *
   * @param original is the original BroadbandDatasource being wrapped.
   */
  public CachingCensusSource(BroadbandDatasource original) {
    this.original = original;

    // The cache will hold 25 items for up to 10 minutes each.
    this.censusCache = new GenericCache<>(true, 10, true, 10);
  }

  /**
   * Override of the getBroadbandData method from the BroadbandDatasource interface. This
   * implementation first checks if the data is in the cache before defaulting to the original
   * datasource's implementation of this method if it is not.
   *
   * @param state is the state to be searched.
   * @param county is the county within the state to be searched.
   * @return the CensusData object if found.
   * @throws IOException
   * @throws StateNotFoundException
   * @throws CountyNotFoundException
   * @throws DatasourceException
   */
  @Override
  public CensusData getBroadbandData(String state, String county)
      throws IOException, StateNotFoundException, CountyNotFoundException, DatasourceException {

    CensusData censusData;
    if ((censusData = this.censusCache.get((state + county)))
        == null) { // Tries to get data from cache.
      censusData = this.original.getBroadbandData(state, county); // Queries the Census API.
      this.censusCache.put((state + county), censusData);
    }
    return censusData;
  }
}
