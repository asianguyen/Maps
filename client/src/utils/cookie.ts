import Cookies from "js-cookie";

/**
 * Function that adds the login cookie for a user.
 * @param uid 
 */
export function addLoginCookie(uid: string): void {
  Cookies.set("uid", uid);
}

/**
 * Function that removes the login cookie for a user.
 */
export function removeLoginCookie(): void {
  Cookies.remove("uid");
}

/**
 * Function that gets the login cookie for a user.
 * @returns 
 */
export function getLoginCookie(): string | undefined {
  return Cookies.get("uid");;
}
