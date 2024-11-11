import Mapbox from "./Mapbox";
import {
  getBroadband,
  clearUser,
  getFilteredRedlining,
  getKeywords,
} from "../utils/api";
import { useState, useEffect } from "react";
import { LatLong, ProvidenceLatLong } from "../components/Mapbox";
import { broadbandOverlay } from "../utils/overlay";
import { getFeatureCenter } from "./Mapbox";

/**
 * A class that sets up the data for the map.
 * @returns the layout of our UI.
 */
export default function MapsGearup() {
  //Create a state variable to store state input
  const [stateInput, setStateInput] = useState("");
  //Create a state variable to store county input
  const [countyInput, setCountyInput] = useState("");
  //Create a state variable to store area description
  const [areaInput, setAreaInput] = useState("");
  //Create a state variable to store the coordinates to fly to
  const [flyCoords, setFlyCoords] = useState<LatLong>(ProvidenceLatLong);
  //Create a state variable to store the redlining data
  const [redliningData, setRedliningData] =
    useState<GeoJSON.FeatureCollection>();
  //Create a state variable to store the area description data
  const [areaData, setAreaData] = useState<GeoJSON.FeatureCollection>();

  useEffect(() => {
    // Asynchronously fetch redlining data
    async function fetchRedliningData() {
      try {
        let json = await getFilteredRedlining(20.0, 50.0, -130.0, -70.0); //coords of entire US
        setRedliningData(json.data);
        console.log("Fetched redlining data:", json.data);
      } catch (error) {
        console.error("Failed to fetch redlining data:", error); // Handle error appropriately
      }
    }
    fetchRedliningData();
  }, []);

  /**
   * Function that handles inputting an area description.
   */
  async function handleAreaClick() {
    setAreaInput("");
    try {
      let json = await getKeywords(areaInput); //get the features that have the area description
      setAreaData(json.data);
      console.log("Fetched area data:", json.data);
    } catch (error) {
      console.error("Failed to fetch area data:", error);
    }
  }

  /**
   * Function that handles inputting a state and county for the broadband.
   */
  async function handleBroadbandClick() {
    setCountyInput("");
    setStateInput("");
    let res = await getBroadband(stateInput, countyInput);
    if (res.data !== undefined) {
      // Add the state + county as a key and broadband as the value
      mappedData.set(res.data[0] + res.data[1], res.data[2]);
      // Make map with just the new data.
      let newMap = new Map();
      newMap.set(res.data[0] + res.data[1], res.data[2]);
      let features = broadbandOverlay(newMap);
      if (features !== undefined) {
        // Get the center of the feature.
        let center = getFeatureCenter(features.features[0]);
        if (center) {
          //Fly to the location
          setFlyCoords({ lat: center.latitude, long: center.longitude });
        }
      }
    }
  }

  // Create a state variable to store the broadband data in a map.
  const [mappedData, setMappedData] = useState<Map<string, string>>(new Map());
  // Create a state variable to store the pins.
  const [pins, setPins] = useState<LatLong[]>([]);

  return (
    <div className="repl">
      <div className="App-header">
        <label className="title">🎀 Mapette 🎀</label>
        <label className="label-state">State:</label>
        <input
          className="input-state"
          aria-label="state-input"
          id="state"
          type="text"
          value={stateInput}
          placeholder="Enter state here!"
          onChange={(ev) => setStateInput(ev.target.value)}
        />
        <br />
        <label className="label-county">County:</label>
        <input
          className="input-county"
          aria-label="county-input"
          id="county"
          type="text"
          value={countyInput}
          placeholder="Enter county here!"
          onChange={(ev) => setCountyInput(ev.target.value)}
        />
        <br />
        {/* Add a button that, when clicked, queries the backend for broadband data */}
        <button
          aria-label="broadband-button"
          className="broadbandbutton"
          onClick={async () => {
            await handleBroadbandClick();
          }}
        >
          Get Broadband Data!
        </button>
        <br />
        <label className="label-area">Keyword:</label>
        <input
          className="input-area"
          aria-label="area-input"
          id="area"
          type="text"
          value={areaInput}
          placeholder="Enter area keyword!"
          onChange={(ev) => setAreaInput(ev.target.value)}
        />
        <br />
        {/* Add a button that, when clicked, queries the backend for area description data */}
        <button
          aria-label="area-button"
          className="areabutton"
          onClick={async () => {
            await handleAreaClick();
          }}
        >
          Get Area Data!
        </button>
        <br />
        {/* Add a button that, when clicked, clears the user's pins and broadband data */}
        <button
          aria-label="clear-button"
          className="clearbutton"
          onClick={async () => {
            setCountyInput("");
            setStateInput("");
            setMappedData(new Map());
            await clearUser();
            setPins([]);
          }}
        >
          Clear All User Data!
        </button>
      </div>
      {
        <div className="repl-history">
          <Mapbox
            mappedData={mappedData}
            pins={pins}
            setPins={setPins}
            flyCoords={flyCoords}
            redliningData={redliningData}
            areaData={areaData}
          />
        </div>
      }
    </div>
  );
}
