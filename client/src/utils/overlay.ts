import { FeatureCollection } from "geojson";
import { FillLayer } from "react-map-gl";
import county_state from "../geodata/counties.json";

const propertyName = "holc_grade";
/**
 * Coloring the overlay data.
 */
export const geoLayer: FillLayer = {
  id: "geo_data",
  type: "fill",
  paint: {
    "fill-color": [
      "match",
      ["get", propertyName],
      "A",
      "#5bcc04",
      "B",
      "#04b8cc",
      "C",
      "#e9ed0e",
      "D",
      "#d11d1d",
      "#ccc",
    ],
    "fill-opacity": 0.4,
  },
};

/**
 * Coloring the area description layer.
 */
export const areaLayer: FillLayer = {
    id: "area_data",
    type: "fill",
    paint: {
      "fill-color": "#ffffff",
      "fill-opacity": 0.6,
    },
  };

/**
 * Coloring the broadband layer.
 */
export const broadbandLayer: FillLayer = {
    id: "broadband_data",
    type: "fill",
    paint: {
      "fill-color": "#000000",
      "fill-opacity": 0.4,
    },
};

/**
 * Function that filters the GeoJson data.
 * @param geojsonData the GeoJSON data
 * @param data a map that has state and county keys and broadband data values.
 * @returns a GeoJSON object with the filtered features.
 */
export function filterGeoJSONData(geojsonData: GeoJSON.FeatureCollection, data: Map<string, string>) {
  // Filter the features.
  const filteredFeatures = geojsonData.features.filter((feature) => {
    const statefp = feature.properties?.STATEFP;
    const countyfp = feature.properties?.COUNTYFP;

    // Check if this feature matches any state-county pair.
    return data.has(statefp + countyfp);
  });

  // Return new GeoJSON object
  return {
    ...geojsonData,
    features: filteredFeatures,
  };
}

/**
 * Function that determines whether a feature collection is valid.
 * @param json the json we are looking at.
 * @returns true or false.
 */
function isFeatureCollection(json: any): json is FeatureCollection {
  return json.type === "FeatureCollection";
}

/**
 * Function that checks if a json conforms to the feature collection structure.
 * @param json the json we are looking at.
 * @returns the json if it is valid.
 */
export function overlayData(json: any): GeoJSON.FeatureCollection | undefined {
  return isFeatureCollection(json) ? json : undefined;
}
/**
 * Function that gets the broadband overlay using the broadband json data.
 * @param data a map that has state and county keys and broadband data values.
 * @returns the filtered broadband json
 */
export function broadbandOverlay(data: Map<string, string>): GeoJSON.FeatureCollection | undefined {
  return isFeatureCollection(county_state)
    ? filterGeoJSONData(county_state, data)
    : undefined;
}
