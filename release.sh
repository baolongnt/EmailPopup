#! /bin/bash

ant clean release
cp bin/EmailPopup-unsigned.apk bin/EmailPopup-signed.apk
jarsigner -verbose -keystore ../../projects/Email\ Popup/misc/blntsoft-android.keystore bin/EmailPopup-signed.apk blntsoft-android
zipalign -v 4 bin/EmailPopup-signed.apk bin/EmailPopup-zipaligned.apk
