# README #


This repository contains the source code of the Stock Count app by Socket Mobile for Android. The Stock Count app is published on [Google Play Store](https://play.google.com/store/apps/details?id=com.socketmobile.stockcount).

Stock Count is a utility app that helps Socket Mobile customers for routine stock/inventory counting jobs, using Socket Mobile barcode scanners' high speed scanning capability. It allows user to quickly scan multiple SKUs in "rapid fire" fashion. The scanned data will be written into a editable text file, with quantity information automatically added to each scan. User will be able to share the file (.txt format) and import it into most inventory management systems. 


### System information ###

* Recommend Android 8 or later version
* Support both Android tablets and Android phones. 


### How to use the app ###

The app is designed to work with Socket Mobile scanners. The capability of scanning barcode with built-in camera of the device is not availalbe yet. Technically, you can use any scanner (connected with your Android device in HID mode) for barcode scanning. However, to take the advantage of the "rapid fire" capability, as well as the accurate scanning without missing characters, you will need a Socket Mobile device to be connected to your Android device in Application Mode and the Scocket Mobile Companion to be installed (We also recommend using the Companion app to connect your Socket Mobile scanners in Applicaiton Mode). It can be downloaded from [Google Play Store](https://play.google.com/store/apps/details?id=com.socketmobile.companion).  

To start, simply create a new file and start scanning the items you set up to count. By default, the app will automatically add a quantity of 1 after each scan. You can change the quantity in the text file or the default quantity in the Scan Settings. 

### Use the app as a sample ###

The app was created to inspire developers with more ideas to use scanner to solve real world problems. It is also a good demo on the implementation details for integrating Socket Mobile Capture SDK for Android:

* Where/when to initialize the scanner
* How to handle the Capture Service notification/error messages
* How Socket Mobile Companion works as part of the solution
* Properly track scanner status
* Get scanned data

### About Capture SDK ###

Capture SDK is a unified software development kit for connect and control all Socket Mobile 1D and 2D barcode scanners on mobile devices. 
Unlike most scanners that operates as a HID (human interface device) through which data transfered one character at a time, like a normal keyboard, Capture SDK takes the advantage of the Application Mode that all Socket Mobile scanners support, which allows data to be transdered in much larger chunks therefore improves the efficiency and accuracy (no more dropping characters). With that, software will also have control over scanner at any given time when it's connected with the app. Scanning event doesn't have to be tied with text object, like all HID based scanners do, which enables more streamlined user workflow, and helps reduce human errors. 

For more information about Capture SDK, check out [Socket Mobile developer site](https://www.socketmobile.com/developer). 


### License and agreement ###

The app integrates Socket Mobile Capture SDK and is free for adaptation per your own business needs under Socket Mobile license and user agreements. You will need to join Socket Mobile Developer program to access the SDK and create AppKey for your own application. The Socket Mobile Developer program is free for life with a one time registration fee. 

### About Socket Mobile ###

Founded in 1992, Socket Mobile is a leading provider of data capture and delivery solutions for enhanced productivity in workforce mobilization. Socket Mobile’s revenue is primarily driven by the deployment of third party barcode enabled mobile applications that integrate Socket Mobile’s cordless barcode scanners and contactless reader/writers. Mobile Applications servicing the specialty retailer, field service, transportation, and manufacturing markets are the primary revenue drivers. Socket Mobile has a network of thousands of developers who use its software developer tools to add sophisticated data capture to their mobile applications. 

### Contact us ###

For inquiries regarding the development of this app as well as Capture SDK integration, please email developers@socketmobile.com. 
