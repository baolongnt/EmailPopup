#! /bin/bash

rm -rf bin
ant clean release
cp bin/EmailPopup-release-unsigned.apk bin/EmailPopup-release-signed.apk
jarsigner -verbose -keystore ../../Documents/projects/Email\ Popup/misc/blntsoft-android.keystore bin/EmailPopup-release-signed.apk blntsoft-android
zipalign -v 4 bin/EmailPopup-release-signed.apk bin/EmailPopup-release-zipaligned.apk
