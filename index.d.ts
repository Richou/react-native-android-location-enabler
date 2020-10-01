interface PromptFuncOptions {
  // Set the desired interval for active location updates, in milliseconds. Default: 10000
  interval: number;
  // Explicitly set the fastest interval for location updates, in milliseconds. Default: 5000
  fastInterval: number;
}

// Error response types
export type ErrorCode =
  | 'ERR00' // user denied
  | 'ERR01' // settings change unavailable
  | 'ERR02' // failed open dialog
  | 'ERR03'; // internal error

export type PromptErrorResponse = {
  code: ErrorCode;
  message: string;
};

// The user has accepted to enable the location services
export type PromptFuncResponse =
  | 'already-enabled' // if the location services has been already enabled
  | 'enabled'; // if user has clicked on OK button in the popup

type PromptFunc = (opts: PromptFuncOptions) => Promise<PromptFuncResponse>;

export default class RNAndroidLocationEnabler {
  static promptForEnableLocationIfNeeded: PromptFunc;
}
