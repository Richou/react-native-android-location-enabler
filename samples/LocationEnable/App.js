/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Fragment} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  TouchableOpacity,
  Platform,
} from 'react-native';

import {
  Colors,
} from 'react-native/Libraries/NewAppScreen';
import RNAndroidLocationEnabler from 'react-native-android-location-enabler';

const onLocationEnablePressed = () => {
  if (Platform.OS === 'android') {
    RNAndroidLocationEnabler.promptForEnableLocationIfNeeded({interval: 10000, fastInterval: 5000})
    .then(data => {
      alert(data);
    }).catch(err => {
      // The user has not accepted to enable the location services or something went wrong during the process
      // "err" : { "code" : "ERR00|ERR01|ERR02", "message" : "message"}
      // codes : 
      //  - ERR00 : The user has clicked on Cancel button in the popup
      //  - ERR01 : If the Settings change are unavailable
      //  - ERR02 : If the popup has failed to open
      alert("Error " + err.message + ", Code : " + err.code);
    });
  }
}

const App = () => {
  return (
    <Fragment>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView style={styles.container}>
        <View style={styles.body}>
          <TouchableOpacity onPress={onLocationEnablePressed}><Text>Click here !</Text></TouchableOpacity>
        </View>
      </SafeAreaView>
    </Fragment>
  );
};

const styles = StyleSheet.create({
  body: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  container: {
    flex: 1,
  },
});

export default App;
