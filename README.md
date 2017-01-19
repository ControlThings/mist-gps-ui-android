# Mist GPS UI for Android

Example software for getting started with Mist Ui development. It demonstrates creating a Mist Ui as a native Android App. This software is freshly baked, expect problems. We are working on it.

## Prerequisites

* Android Studio (tested in 2.2.2)
* Wish - Peer-to-peer trustbased networking layer (https://mist.controlthings.fi/dist/Wish-v0.6.5-pre3.apk)
* Mist - IoT layer for Wish (https://mist.controlthings.fi/dist/MistUi-pre3.apk)
* Mist GPS - An android Service playing the role of a Mist Node/Device. (https://mist.controlthings.fi/dist/MistGps-pre3.apk or get source code https://github.com/ControlThings/mist-gps-android)

Download and install Wish, Mist and the MistGps service.

You need to create a user in the Mist application when you first start it up. Swipe to the Users-tab and click the plus-sign in the bottom right corner. Write a user name you want to use, and an identity will be generated for you.

## First run

1. Clone this repository and open it in Android Studio
2. Attach a phone with Android 4.4 or newer
3. Start Mist GPS and Mist
4. Verify that you see the GPS in the Systems-tab from the Mist App on your phone.
4. Deploy the Mist Gps Ui app from Android Studio
5. The Gps Ui will automatically redirect you to Mist Ui if it can't see any peers. You chould then be able to click on the GPS in the peers list to give the Ui access to the Mist GPS.

See the source code for more documentation. All relevant code is in `MainActivity.java`.

## Java API

The Java API currently does not implement the full featureset of MistApi.

### Mist commands

```java
Mist.login(LoginCb callback)
```

Log in to Mist. This registers the Ui to Mist.

```java
Mist.signals(SignalsCb callback)
```

Receive signals from Mist, like changes in available peers etc.

```java
Mist.listPeers(ListPeersCb callback)
```

List available peers.

```java
Mist.settings(SettingsCb callback)
```

The settings call is used to request Wifi commissioning, permissions etc. from Mist. 

### Control commands

All control commands require the remote peer object as the first parameter. It is obtained using `Mist.listPeers`.

```java
Control.model(Peer peer, ModelCb callback)
```

Request a model from the device. The Mist model is represented as a JSONObject.

```java
Control.follow(Peer peer, FollowCb callback)
```

Request updates from the device about values.

```java
Control.write(Peer peer, String endpointId, Boolean value, WriteCb callback)
```

Write to a boolean endpoint.

```java
Control.write(Peer peer, String endpointId, int value, WriteCb callback)
```

Write to a int endpoint.

```java
Control.write(Peer peer, String endpointId, float value, WriteCb callback)
```

Write to a float endpoint (actual internal representation is a double)

```java
Control.write(Peer peer, String endpointId, String value, WriteCb callback)
```

Write to a string endpoint.



## Known limitations

This is based on an android port of the Mist library, and is very limited in its capabilities, and you can probably break it in several ways.

* Hard coded relay servers
* Friend requests are automatically accepted

Q: Peer does not show up
A: Close all apps, and start them in the following order: Mist Ui, Mist Gps, Gps Ui



