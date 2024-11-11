package edu.brown.cs.student.main.server.serializers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.Map;

/** A class used to serialize user outputs in each handler class. */
public class MapSerializer {
  private final JsonAdapter<Map<String, Object>> adapter;

  /**
   * The constructor creates an adapter to convert from a map of strings to objects to a JSON
   * string.
   */
  public MapSerializer() {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    this.adapter = moshi.adapter(mapStringObject);
  }

  /**
   * Serializes the input Map into a JSON string.
   *
   * @param map the Map to serialize.
   * @return the JSON output.
   */
  public String serialize(Map<String, Object> map) {
    return this.adapter.toJson(map);
  }
}
