package edu.brown.cs.student.main.server.handlers.census;

import edu.brown.cs.student.main.server.handlers.census.exceptions.CountyNotFoundException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.census.exceptions.StateNotFoundException;
import java.io.IOException;

/**
 * This interface prescribes the getBroadbandData method, which returns requested data. This
 * interface is used to enable polymorphism, in part to help with testing via a 'mocked' broadband
 * datasource.
 */
public interface BroadbandDatasource {

  /**
   * This method returns a CensusData object for the given state and county specified in the input
   * parameters.
   *
   * @param state is the state to be searched.
   * @param county is the county within the state to be searched.
   * @return the Census Data for the specified state and county.
   * @throws IOException
   * @throws StateNotFoundException
   * @throws CountyNotFoundException
   * @throws DatasourceException
   */
  CensusData getBroadbandData(String state, String county)
      throws IOException, StateNotFoundException, CountyNotFoundException, DatasourceException;
}
