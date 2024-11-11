import "mapbox-gl/dist/mapbox-gl.css";
import { useEffect, useState, useRef, Dispatch, SetStateAction } from "react";
import Map, {
  Layer,
  MapLayerMouseEvent,
  Source,
  ViewStateChangeEvent,
  MapRef,
  Popup,
  Marker,
} from "react-map-gl";
import {
  geoLayer,
  areaLayer,
  overlayData,
  broadbandLayer,
  broadbandOverlay,
} from "../utils/overlay";
import { addPin, getPins } from "../utils/api";
import mapPin from "../components/pin.png";

/**
 * A class that represents the various features of the map.
 */

const MAPBOX_API_KEY = process.env.MAPBOX_TOKEN;
if (!MAPBOX_API_KEY) {
  console.error("Mapbox API key not found. Please add it to your .env file.");
}

//Interface that combines latitude and longitude.
export interface LatLong {
  lat: number;
  long: number;
}

//Constant that represents location of Providence.
export const ProvidenceLatLong: LatLong = {
  lat: 41.824,
  long: -71.4128,
};
//Constant that represents initial zoom when map is first rendered.
const initialZoom = 10;

//Props for Mapbox features.
interface MapboxProps {
  mappedData: Map<string, string>;
  pins: LatLong[];
  setPins: Dispatch<SetStateAction<LatLong[]>>;
  flyCoords: LatLong;
  redliningData: GeoJSON.FeatureCollection | undefined;
  areaData: GeoJSON.FeatureCollection | undefined;
}

/**
 * Function that gets the center of a geometric feature.
 * @param feature the feature we want to get the center of.
 * @returns if polygon: the geometric center of our feature, if not: 0,0.
 */
export const getFeatureCenter = (feature: GeoJSON.Feature) => {
  if (feature.geometry.type === "Polygon") {
    let coords = feature.geometry.coordinates;
    let avgLat = 0;
    let avgLong = 0;
    //Loop through the feature's coordinates and sum up the latitude and longitude.
    for (let i = 0; i < coords[0].length; i++) {
      avgLat += coords[0][i][1];
      avgLong += coords[0][i][0];
    }
    //Get the average latitude and longitude.
    avgLat /= coords[0].length;
    avgLong /= coords[0].length;
    return { latitude: avgLat, longitude: avgLong };
  }
  return { latitude: 0, longitude: 0 };
};

export default function Mapbox(props: MapboxProps) {
  //Sets the intial state of the map to Providence.
  const [viewState, setViewState] = useState({
    latitude: ProvidenceLatLong.lat,
    longitude: ProvidenceLatLong.long,
    zoom: initialZoom,
  });

  const mapRef = useRef<MapRef>(null);

  //Create a state variable to store redlining overlay.
  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(
    undefined
  );

  //Create a state variable to store broadband overlay.
  const [broadband, setBroadband] = useState<
    GeoJSON.FeatureCollection | undefined
  >(undefined);

  //Create a state variable to store area description overlay.
  const [areaOverlay, setAreaOverlay] = useState<
    GeoJSON.FeatureCollection | undefined
  >(undefined);

  /**
   * Function that defines what happens when user clicks on map — adding pins.
   * @param e the mouse event.
   */
  async function onMapClick(e: MapLayerMouseEvent) {
    // Add the pin to the pins array.
    props.setPins([...props.pins, { lat: e.lngLat.lat, long: e.lngLat.lng }]);
    await addPin(e.lngLat.lng.toString(), e.lngLat.lat.toString());
  }

  //Create a state variable to store data for the popup.
  const [popupHover, setPopupHover] = useState<{
    longitude: number;
    latitude: number;
    data: any;
  } | null>(null);

  //Create a state variable to store whether the h key has been pressed.
  const [hPressed, setHPressed] = useState(false);

  /**
   * A function that moves the maps current state to the inputted location.
   * @param latitude latitude of location to move to.
   * @param longitude longitude of location to move to.
   */
  const flyToLocation = (latitude: number, longitude: number) => {
    const map = mapRef.current?.getMap();
    if (map) {
      map.flyTo({
        center: [longitude, latitude],
        essential: true,
        speed: 0.8,
        zoom: 9,
      });
    }
  };

  /**
   * Function that defines what happens when a user hover's over a feature with a broadband overlay.
   * @param event the mouse event.
   */
  const onHover = (event: MapLayerMouseEvent) => {
    setBroadband(broadbandOverlay(props.mappedData));
    //Get the map's current broadband features.
    const features = mapRef.current?.queryRenderedFeatures(event.point, {
      layers: ["broadband_data"],
    });
    const feature = features && features[0];
    if (feature && feature.geometry.type === "Polygon") {
      //Get the center of the feature and set the popup to be there.
      let { latitude: avgLat, longitude: avgLong } = getFeatureCenter(feature);
      setPopupHover({
        longitude: avgLong,
        latitude: avgLat,
        data: feature.properties,
      });
    } else {
      setPopupHover(null);
    }
  };

  const onLeave = () => {
    //When we are no longer hovering over a feature.
    setPopupHover(null);
  };

  useEffect(() => {
    //Fly to the location and highlight county broadband data when a user inputs data.
    if (props.flyCoords) {
      flyToLocation(props.flyCoords.lat, props.flyCoords.long);
      setBroadband(broadbandOverlay(props.mappedData));
    }
  }, [props.flyCoords]);

  useEffect(() => {
    if (props.redliningData) {
      setOverlay(overlayData(props.redliningData));
    }
    if (props.areaData) {
      setAreaOverlay(overlayData(props.areaData));
    }
    setBroadband(broadbandOverlay(props.mappedData));

    getPins().then((data) => {
      // Loop through data.pins and split by comma, then convert to LatLong:
      let pinsConverted: LatLong[] = [];
      if (data.pins != undefined){
        data.pins.forEach((pin: string) => {
          let [long, lat] = pin.split(",");
          pinsConverted.push({ lat: parseFloat(lat), long: parseFloat(long) });
        });
        props.setPins(pinsConverted);
      }
      });

    // Hide the popup when the "h" key is pressed:
    const hDown = (ev: KeyboardEvent) => {
      if (ev.key === "h") {
        setHPressed(true);
      }
    };
    const hUp = (ev: KeyboardEvent) => {
      if (ev.key === "h") {
        setHPressed(false);
      }
    };

    // Add event listeners for the key presses.
    window.addEventListener("keydown", hDown);
    window.addEventListener("keyup", hUp);

    // Remove event listeners when the component is unmounted.
    return () => {
      window.removeEventListener("keydown", hDown);
      window.removeEventListener("keyup", hUp);
    };
  }, [props.mappedData, props.redliningData, props.areaData]);

  return (
    <div className="map">
      {/* Sets the styling of the map */}
      <Map
        mapboxAccessToken={MAPBOX_API_KEY}
        {...viewState}
        projection={{
          name: "globe",
        }}
        fog={{
          color: "#9ee1e8",
          "horizon-blend": 0.02,
          "high-color": "#113385",
          "space-color": "#000000",
          "star-intensity": 0.5,
        }}
        style={{ width: "100%", height: window.innerHeight }}
        mapStyle={"mapbox://styles/mapbox/satellite-streets-v12"}
        onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)}
        onClick={(ev: MapLayerMouseEvent) => onMapClick(ev)}
        onMouseMove={onHover}
        onMouseLeave={onLeave}
        ref={mapRef}
      >
        {" "}
        {/* Sets the redlining overlay */}
        {props.pins.map((pin, index) => (
          <Marker
            key={index}
            longitude={pin.long}
            latitude={pin.lat}
            anchor="bottom"
          >
            <img src={mapPin} alt="Marker" className="pin-image" />
          </Marker>
          
        ))}
        
        {props.redliningData && (
          <Source id="geo_data" type="geojson" data={overlay}>
            <Layer {...geoLayer} />
          </Source>
        )}
        {/* Sets the area description overlay */}
        {props.areaData && (
          <Source id="area_data" type="geojson" data={areaOverlay}>
            <Layer {...areaLayer} />
          </Source>
        )}
        {/* Sets the broadband overlay */}
        <Source id="broadband_data" type="geojson" data={broadband}>
          <Layer {...broadbandLayer} />
        </Source>
        {popupHover && !hPressed && (
          <Popup
            longitude={popupHover.longitude}
            latitude={popupHover.latitude}
            closeButton={false}
            closeOnClick={false}
            anchor="center"
          >
            {/* Render the content of the popup */}
            <div>
              {/* Display properties of the broadband feature */}
              <p>{"County Name: " + popupHover.data.NAME}</p>
              <p>
                {"\nBroadband: " +
                  props.mappedData.get(
                    popupHover.data.STATEFP + popupHover.data.COUNTYFP
                  )}
              </p>
            </div>
          </Popup>
        )}
        {/* Render each pin at its location with an image */}
      </Map>
    </div>
  );
}
