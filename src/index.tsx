import AndroidLocationEnabler from './NativeAndroidLocationEnabler';

export type AndroidLocationEnablerResult = 'enabled' | 'already-enabled';

export type AndroidLocationEnablerOptions = {
  interval: number;
  waitForAccurate?: boolean;
};

export function promptForEnableLocationIfNeeded(
  options?: AndroidLocationEnablerOptions
): Promise<AndroidLocationEnablerResult> {
  return AndroidLocationEnabler.promptForEnableLocationIfNeeded(options);
}

export function isLocationEnabled(): Promise<boolean> {
  return AndroidLocationEnabler.isLocationEnabled();
}
