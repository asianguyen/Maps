package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.handlers.AddPinHandler;
import edu.brown.cs.student.main.server.handlers.ClearUserHandler;
import edu.brown.cs.student.main.server.handlers.FilteredDataHandler;
import edu.brown.cs.student.main.server.handlers.GeoJSONObject;
import edu.brown.cs.student.main.server.handlers.KeywordFilterHandler;
import edu.brown.cs.student.main.server.handlers.ListPinsHandler;
import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.handlers.census.BroadbandHandler;
import edu.brown.cs.student.main.server.handlers.census.CensusAPISource;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.io.IOException;
import spark.Filter;
import spark.Spark;

/** Top Level class for our project, utilizes spark to create and maintain our server. */
public class Server {

  public static void setUpServer() {
    int port = 3232;
    Spark.port(port);

    after(
        (Filter)
            (request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
            });

    StorageInterface firebaseUtils;
    GeoJSONObject geoJSONObject;
    try {
      firebaseUtils = new FirebaseUtilities();
      geoJSONObject = Utils.getRedLiningTotal();

      Spark.get("add-pin", new AddPinHandler(firebaseUtils));
      Spark.get("list-pins", new ListPinsHandler(firebaseUtils));
      Spark.get("clear-user", new ClearUserHandler(firebaseUtils));
      Spark.get("filtered-geojson", new FilteredDataHandler(geoJSONObject));
      Spark.get("filtered-keywords", new KeywordFilterHandler(geoJSONObject));
      Spark.get("broadband", new BroadbandHandler(new CensusAPISource()));

      Spark.notFound(
          (request, response) -> {
            response.status(404); // Not Found
            System.out.println("ERROR");
            return "404 Not Found - The requested endpoint does not exist.";
          });
      Spark.init();
      Spark.awaitInitialization();

      System.out.println("Server started at http://localhost:" + port);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println(
          "Error: Could not initialize Firebase. Likely due to firebase_config.json not being found. Exiting.");
      System.exit(1);
    }
  }

  /**
   * Runs Server.
   *
   * @param args none
   */
  public static void main(String[] args) {
    setUpServer();
  }
}
