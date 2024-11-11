package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class Utils {

  /**
   * Function that converts an object into a JSON.
   *
   * @param map the map to convert.
   * @return the data in JSON format.
   */
  public static String toMoshiJson(Map<String, Object> map) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    return adapter.toJson(map);
  }

  /**
   * Parses JSON data from a JsonReader and converts it to the specified target type.
   *
   * @param source The JsonReader containing the JSON data.
   * @param targetType The Class representing the target data type to convert the JSON to.
   * @param <T> The generic type of the target data.
   * @return An instance of the target data type parsed from the JSON.
   * @throws IOException if there's an error reading or parsing the JSON data.
   */
  public static <T> T fromJsonGeneral(String source, Class<T> targetType) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<T> adapter = moshi.adapter(targetType);

    return adapter.fromJson(source);
  }

  public static GeoJSONObject getRedLiningTotal() {
    String filePath = "data/geojson/fullDownload.json";
    try {
      // ***************** READING THE FILE *****************
      FileReader jsonReader = new FileReader(filePath);
      BufferedReader br = new BufferedReader(jsonReader);
      String fileString = "";
      String line = br.readLine();
      while (line != null) {
        fileString = fileString + line;
        line = br.readLine();
      }
      jsonReader.close();

      // ****************** CREATING THE ADAPTER **********
      return Utils.fromJsonGeneral(fileString, GeoJSONObject.class);
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }
}
