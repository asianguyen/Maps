package edu.brown.cs.student.main.server.handlers.census;

/**
 * A record representing the information that we would like to retain from the census report on
 * broadband internet access.
 *
 * @param name - the State and County name for the query.
 * @param broadband - the percent of broadband households.
 * @param state - the state code
 * @param county - the county code
 */
public record CensusData(
    String name, String broadband, String state, String county, String date_time) {}
