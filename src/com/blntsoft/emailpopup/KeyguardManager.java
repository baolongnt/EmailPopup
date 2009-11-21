package com.blntsoft.emailpopup;

import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.util.Log;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@nguyentrong.com)
 */
public class KeyguardManager {

    private static KeyguardLock keyguardLock = null;

    static synchronized void disableKeyguard(Context context) {
        if (keyguardLock==null) {
            android.app.KeyguardManager keyguardManager = (android.app.KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager.inKeyguardRestrictedInputMode()) {
                keyguardLock = keyguardManager.newKeyguardLock(EmailPopup.LOG_TAG);
                keyguardLock.disableKeyguard();
                Log.d(EmailPopup.LOG_TAG, "Keyguard disabled");
            }
            else {
                Log.v(EmailPopup.LOG_TAG, "Keyguard was not enabledd");
            }
        }
        else {
            Log.v(EmailPopup.LOG_TAG, "Keyguard was already disabled");
        }
    }

    static synchronized void release() {
        keyguardLock = null;
        Log.d(EmailPopup.LOG_TAG, "Keyguard released");
    }

    static synchronized void reenableKeyguard() {
        if (keyguardLock!=null) {
            keyguardLock.reenableKeyguard();
            keyguardLock = null;
            Log.d(EmailPopup.LOG_TAG, "Keyguard reenabled");
        }
        else {
            Log.v(EmailPopup.LOG_TAG, "Keyguard was alredy reenabled");
        }
    }

    static synchronized boolean isEnabled(Context context) {
        if (keyguardLock!=null) {
            return true;
        }
        else {
            android.app.KeyguardManager keyguardManager = (android.app.KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
            return keyguardManager.inKeyguardRestrictedInputMode();
        }
    }

}//KeyguardManager
