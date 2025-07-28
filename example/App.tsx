/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */
import * as React from 'react';

import { StyleSheet, View, StatusBar, useColorScheme, Text, Button, Platform } from 'react-native';
import {
  isLocationEnabled,
  promptForEnableLocationIfNeeded,
} from 'react-native-android-location-enabler';

function App() {
  const isDarkMode = useColorScheme() === 'dark';
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
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
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
  },
});

export default App;
