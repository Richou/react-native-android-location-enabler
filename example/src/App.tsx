import * as React from 'react';

import { StyleSheet, View, Text, Button, Platform } from 'react-native';
import {
  isLocationEnabled,
  promptForEnableLocationIfNeeded,
} from 'react-native-android-location-enabler';

export default function App() {
  const [result, setResult] = React.useState<string | undefined>();
  const [enabled, setEnabled] = React.useState<boolean | undefined>();

  async function handleEnabledPressed() {
    if (Platform.OS === 'android') {
      try {
        const enableResult = await promptForEnableLocationIfNeeded();
        console.log('enableResult', enableResult);
        setResult(enableResult);
      } catch (error: unknown) {
        if (error instanceof Error) {
          setResult(error.message);
        }
      }
    }
  }

  async function handleCheckPressed() {
    if (Platform.OS === 'android') {
      const checkEnabled = await isLocationEnabled();
      console.log('checkEnabled', checkEnabled);
      setEnabled(checkEnabled);
    }
  }

  return (
    <View style={styles.container}>
      <Button title="Check if GPS Enabled" onPress={handleCheckPressed} />
      {enabled !== undefined && (
        <Text>Location Enabled : {enabled ? 'Yes' : 'No'}</Text>
      )}
      <Button title="Enable GPS" onPress={handleEnabledPressed} />
      {result && <Text>Result: {result}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    gap: 10,
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
