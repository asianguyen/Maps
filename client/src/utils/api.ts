import { getLoginCookie } from "./cookie";

const HOST = "http://localhost:3232";

/**
 * Function that queries backend API.
 * @param endpoint the endpoint of what we are querying.
 * @param query_params parameters of the endpoint.
 * @returns the response map returned by the query.
 */
async function queryAPI(
  endpoint: string,
  query_params: Record<string, string>
) {
  const paramsString = new URLSearchParams(query_params).toString();
  const url = `${HOST}/${endpoint}?${paramsString}`;
  const response = await fetch(url);
  if (!response.ok) {
    console.error(response.status, response.statusText);
  }
  return response.json();
}

/**
 * Function that gets the redlining data by querying with the filtered-geojson endpoint.
 * @param minLat minimum latitude of the area.
 * @param maxLat maximum latitude of the area.
 * @param minLong minimum longitude of the area.
 * @param maxLong maximum longitude of the area.
 * @returns the filtered redlining data.
 */
export async function getFilteredRedlining(minLat: number, maxLat: number, minLong: number, maxLong: number) {
  return await queryAPI("filtered-geojson", {
    minLat: minLat.toString(),
    maxLat: maxLat.toString(),
    minLong: minLong.toString(),
    maxLong: maxLong.toString(),
  });
}

/**
 * Function that gets the areas that have the keyword in their descriptions.
 * @param areaData the keyword.
 * @returns the area description data.
 */
export async function getKeywords(areaData: string) {
  return await queryAPI("filtered-keywords", {
    keyWord: areaData,
  });
}

/**
 * Function that gets the broadband data of the inputted state and county. 
 * @param stateIn inputted state.
 * @param countyIn inputted county.
 * @returns the state + county's broadband data.
 */
export async function getBroadband(stateIn: string, countyIn: string) {
  return await queryAPI("broadband", {
    county: countyIn,
    state: stateIn,
  });
}

/**
 * Function that adds a pin.
 * @param longIn longitude coord of the pin.
 * @param latIn latitude coord of the pin.
 * @returns data associated with the pin we want to add.
 */
export async function addPin(longIn: string, latIn: string) {
  return await queryAPI("add-pin", {
    uid: getLoginCookie() || "",
    long: longIn,
    lat: latIn,
  });
}

/**
 * Function that gets the list of pins associated with each user.
 * @returns the list of pins.
 */
export async function getPins() {
  return await queryAPI("list-pins", {
    uid: getLoginCookie() || "",
  });
}

/**
 * Function that clears the data associated with each user.
 * @param uid the user's id.
 * @returns clear user data.
 */
export async function clearUser(uid: string = getLoginCookie() || "") {
  return await queryAPI("clear-user", {
    uid: uid,
  });
}
