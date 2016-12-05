
# react-native-android-location-enabler

## Getting started

`$ npm install react-native-android-location-enabler --save`

### Mostly automatic installation

`$ react-native link react-native-android-location-enabler`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-android-location-enabler` and add `RNAndroidLocationEnabler.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNAndroidLocationEnabler.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNAndroidLocationEnablerPackage;` to the imports at the top of the file
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

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNAndroidLocationEnabler.sln` in `node_modules/react-native-android-location-enabler/windows/RNAndroidLocationEnabler.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Cl.Json.RNAndroidLocationEnabler;` to the usings at the top of the file
  - Add `new RNAndroidLocationEnablerPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNAndroidLocationEnabler from 'react-native-android-location-enabler';

// TODO: What do with the module?
RNAndroidLocationEnabler;
```
  