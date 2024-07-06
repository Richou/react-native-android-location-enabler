import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-android-location-enabler' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const AndroidLocationEnabler = NativeModules.AndroidLocationEnabler
  ? NativeModules.AndroidLocationEnabler
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

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

export function hasPlayServices(): Promise<boolean> {
  return AndroidLocationEnabler.hasPlayServices();
}
