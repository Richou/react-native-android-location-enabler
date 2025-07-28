import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export type AndroidLocationEnablerResult = 'enabled' | 'already-enabled';

export type AndroidLocationEnablerOptions = {
  interval: number;
  waitForAccurate?: boolean;
};

export interface Spec extends TurboModule {
  promptForEnableLocationIfNeeded(
    option?: AndroidLocationEnablerOptions
  ): Promise<AndroidLocationEnablerResult>;
  isLocationEnabled(): Promise<boolean>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('AndroidLocationEnabler');
