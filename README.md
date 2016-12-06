
# react-native-android-location-enabler

## Getting started

`$ npm install react-native-android-location-enabler --save`

### Mostly automatic installation

`$ react-native link react-native-android-location-enabler`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.heanoria.library.reactnative.locationenabler.RNAndroidLocationEnablerPackage;` to the imports at the top of the file
  - Add `new RNAndroidLocationEnablerPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-android-location-enabler'
  	project(':react-native-android-location-enabler').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-android-location-enabler/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-android-location-enabler')
  	```

## Usage
```javascript
import RNAndroidLocationEnabler from 'react-native-android-location-enabler';

RNAndroidLocationEnabler.promptForEnableLocationIfNeeded({interval: 10000})
  .then(data => {
    // The user has accepted to enable the location services
  }).catch(err => {
    // The user has not accepted to enable the location services or something went wrong during the process
  });
```
  
