import * as React from 'react';

import { StyleSheet, View, Text, Button } from 'react-native';
import { promptForEnableLocationIfNeeded } from 'react-native-android-location-enabler';

export default function App() {
  const [result, setResult] = React.useState<string | undefined>();

  async function handleEnabledPressed() {
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

  return (
    <View style={styles.container}>
      {result && <Text>Result: {result}</Text>}
      <Button title="Enable GPS" onPress={handleEnabledPressed} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
