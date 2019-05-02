declare module 'react-native-android-location-enabler' {
  export type sucess = 'already-enabled' | 'enabled';

  /**
   * codes :
   *   {ERR00} : The user has clicked on Cancel button in the popup
   *   {ERR01} : If the Settings change are unavailable
   *   {ERR02} : If the popup has failed to open
   */
  export type errorCode = 'ERR00' | 'ERR01' | 'ERR02';

  export interface ReadableMap {
    interval: number;
    fastInterval: number;
  }

  export interface Failure {
    code: errorCode;
    message: string;
  }

  export interface IRNAndroidLocationEnabler {
    /**
     *Allow to display a GoogleMap like android popup to ask for user to enable location services if disabled
     *
     * @memberof RNAndroidLocationEnabler
     * @param param Object with both Location Interval Duration Params Key and Location Fast Interval Duratin Params Key
     * @returns {Promise<success>} promise with a sucess message if it resolves or an {Failure} object if it rejects.
     */
    promptForEnableLocationIfNeeded: (param: ReadableMap) => Promise<sucess>;
  }
  const RNAndroidLocationEnabler: IRNAndroidLocationEnabler;
  export default RNAndroidLocationEnabler;
  
}
