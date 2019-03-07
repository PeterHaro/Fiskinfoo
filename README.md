# Fiskinfo for Android

The goal of FiskInfo is to be a one-stop-shop for digital distribution of important information for the fishing fleet.

FiskInfo is developed by [SINTEF](https://www.sintef.no/) in collaboration with [Fiskeri- og havbruksnæringens Forskningsfond (FHF)](http://www.fhf.no/) and [Barentswatch](https://www.barentswatch.no/).

FiskInfo is available as a [web site](https://www.barentswatch.no/en/fishinfo/) from Barentswatch and as a mobile app for [Android](https://play.google.com/store/apps/details?id=fiskinfoo.no.sintef.fiskinfoo&hl=no) and [iOS](https://itunes.apple.com/no/app/fiskinfo/id1081341585?mt=8). 

This repository contains the open source Android app.

## Functionality
The FishInfo app offers a map with multiple layers that enable fishermen to have an update view of the seas in which they operate. Layers available include:

* Vessel postitions (AIS)
* Active fishing facilities (e.g. nets, lines, crab pots)
* Subsea facilities
* Restriction zones, e.g coral reefs
* Planned and ongoing seismic activity
* Ice concentration and ice edge

A popular feature of the app is reporting of deployment and retieval of fishing equipment, where the current GPS position can be used to speed up the process. Reports are sent to the Coast Guard (Kystvaktsentralen).

Map layers can also be downloaded for use with map plotters and other equipment.

Some of the map layers and functionality of the app is only available for users with registered fishing vessels.

The FiskInfo app is under ongoing further development, and among the plans is new functionality that provides decision support for chosing the best time and location for getting the right catch.

## Building the Andoid app
The repository contains the full source code needed to build the app in Android Studio, with one minor exception. The app uses Firebase for analytics, and require a google-services.json file to be present in the app folder to build with Firebase enabled. To build the app, create your own version of this file or disable Firebase in the app.
