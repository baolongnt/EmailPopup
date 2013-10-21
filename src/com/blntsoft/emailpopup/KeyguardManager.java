package com.blntsoft.emailpopup;

import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.util.Log;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@blntsoft.com)
 */
public class KeyguardManager {

    public interface SecureReleaseCallback {
 		public void onKeyguardSecuredReleased();
    }

    private static android.app.KeyguardManager keyguardManager = null;
    private static KeyguardLock keyguardLock = null;

    private static android.app.KeyguardManager getKeyguardManager(Context context) {
        if (keyguardManager == null) {
            keyguardManager = (android.app.KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        }
        return keyguardManager;
    }

    static synchronized void disableKeyguard(Context context) {
        if (getKeyguardManager(context).inKeyguardRestrictedInputMode()) {
            keyguardLock = getKeyguardManager(context).newKeyguardLock(EmailPopup.LOG_TAG);
            keyguardLock.disableKeyguard();
            Log.d(EmailPopup.LOG_TAG, "Keyguard disabled");
        }
        else {
            Log.v(EmailPopup.LOG_TAG, "Keyguard was not enabled");
        }
    }

    static synchronized void release() {
        keyguardLock = null;
        Log.d(EmailPopup.LOG_TAG, "Keyguard released");
    }

    static synchronized void secureRelease(Context context, final SecureReleaseCallback callback) {
        if (keyguardLock != null) {
            final KeyguardLock finalKeyguardLock = keyguardLock;
            keyguardLock = null;
            Log.d(EmailPopup.LOG_TAG, "Keyguard released");

            getKeyguardManager(context).exitKeyguardSecurely(new android.app.KeyguardManager.OnKeyguardExitResult() {
                @Override
                public void onKeyguardExitResult(boolean success) {
                    Log.d(EmailPopup.LOG_TAG, "onKeyguardExitResult: " + success);
                    //Not sure why we need to reenable keyguard
                    //but it is needed for subsequent keyguard unlock to work properly
                    finalKeyguardLock.reenableKeyguard();
                    if (success) {
                        callback.onKeyguardSecuredReleased();
                    }
                }
            });
        }
        else {
            callback.onKeyguardSecuredReleased();
        }
    }

    static synchronized void reenableKeyguard() {
        if (keyguardLock!=null) {
            keyguardLock.reenableKeyguard();
            keyguardLock = null;
            Log.d(EmailPopup.LOG_TAG, "Keyguard reenabled");
        }
        else {
            Log.v(EmailPopup.LOG_TAG, "Keyguard was already reenabled");
        }
    }

    static synchronized boolean isEnabled(Context context) {
        return getKeyguardManager(context).inKeyguardRestrictedInputMode();
    }

}//KeyguardManager
