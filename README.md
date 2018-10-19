# README #


This repository contains the source code of the Stock Count app by Socket Mobile for Android. The Stock Count app is published in [Google Play Store](https://play.google.com/store/apps/details?id=com.socketmobile.stockCount). 

Stock Count is a utility app to help Socket Mobile customers leverage Socket Mobile barcode scanners' high speed scanning capability for routine stock/inventory counting jobs. It allows user to quickly scan multiple SKUs in "rapid fire" fashion, using Socket Mobile barcode scanners. The scanned data will be written into a editable text file, with quantity information automatically added to each scan. User will be able to share the file (.txt format) and import it into most inventory management systems. 


### System information ###

* Requires Android 8 or later version
* Support both Android tablets and Android phones. 


### How to use the app ###

The app is designed to work with a Socket Mobile device. The capability of scanning barcode with built-in camera of the device is not availalbe yet. Technically, you can use any scanner (connected with your Android device in HID mode) for barcode scanning. However, to take the advantage of the "rapid fire" capability, as well as the accurate scanning without missing characters, you will need a Socket Mobile device to be connected to your Android device in Application Mode and the Scocket Mobile Companion to be installed (We also recommend using Socket Mobile Companion app to connect your Socket Mobile scanners in Applicaiton Mode). The Companion app can be downloaded from [Google Play Store](https://play.google.com/store/apps/details?id=com.socketmobile.companion).  

To start using the app, simply create a new file and start scanning the items you set up to count. By default, the app will automatically add a quantity of 1 after each scan. You can change the quantity in the text file or the default quantity in the Scan Settings. 

### Use the app as a sample ###

The app was created to inspire developers with more ideas to use scanner to solve real world problems. It is also a good demo on the implementation details for:

* Where/when to initialize the scanner
* How to handle the Capture Service notification/error messages
* How Socket Mobile Companion works as part of the solution
* Properly track scanner status
* Get scanned data



### License and agreement ###

The app integrates Socket Mobile Capture SDK and is free for adaptation per your own business needs under Socket Mobile license and user agreements. You will need to join Socket Mobile Developer program to access the SDK and create AppKey for your own application. The Socket Mobile Developer program is free for life with a one time registration fee. 


### Contact us ###

For inquiries regarding the development of this app as well as Capture SDK integration, please email developers@socketmobile.com. 
