package edu.brown.cs.student.main.server.handlers.census;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.census.exceptions.DatasourceException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import okio.Buffer;

/**
 * A class with static methods to assist the CensusAPISource in querying the census. Uses moshi to
 * deserialize output.
 */
public class CensusAPIUtilities {

  /**
   * A utility method used within CensusAPISource to query the Census, given the url endpoint and
   * specific query. Uses moshi to deserialize the census output into a List of a List of Strings.
   *
   * @param file the specific census query to be run.
   * @return a List of a List of Strings, containing the de-serialized data from the census.
   * @throws IOException
   * @throws DatasourceException
   */
  public static List<List<String>> queryCensus(String file)
      throws IOException, DatasourceException {
    URL requestURL = new URL("https", "api.census.gov", file);
    HttpURLConnection clientConnection = connect(requestURL);
    Moshi moshi = new Moshi.Builder().build();

    Type listListString = Types.newParameterizedType(List.class, List.class, String.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listListString);

    List<List<String>> results =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();

    return results;
  }

  /**
   * Private helper method to connect to client; throws IOException and DatasourceExceptions so
   * different callers can handle differently if needed.
   *
   * @param requestURL the URL used to query the census.
   * @return the established HttpURLConnection for querying the census.
   * @throws DatasourceException
   * @throws IOException
   */
  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection))
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect();
    if (clientConnection.getResponseCode() != 200)
      throw new DatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    return clientConnection;
  }
}
