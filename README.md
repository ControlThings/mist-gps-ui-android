# Mist GPS UI for Android

Example software for getting started with Mist Ui development. It demonstrates creating a Mist Ui as a native Android App. This software is freshly baked, expect problems. We are working on it.

## Prerequisites

* Android Studio (tested in 2.2.2)
* Wish - Peer-to-peer platform (download .apk)
* Mist - IoT layer for Wish (download .apk)
* Mist GPS - An android Service playing the role of a Mist Node/Device. (download .apk or get source code)

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

## Known limitations

This is based on an android port of the Mist library, and is very limited in its capabilities, and you can probably break it is several ways.

Q: Peer does not show up
A: Close all apps, and start them in the following order: Mist Ui, Mist Gps, Gps Ui



