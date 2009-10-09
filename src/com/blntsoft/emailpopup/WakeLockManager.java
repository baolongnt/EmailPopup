package com.blntsoft.emailpopup;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@nguyentrong.com)
 */
public class WakeLockManager {
    
    private static WakeLock partialwakeLock = null;
    private static WakeLock fullWakeLock = null;
    
    static synchronized void acquirePartialWakeLock(Context context) {
        PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        if (partialwakeLock==null) {
            partialwakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                EmailPopup.LOG_TAG);
            partialwakeLock.acquire();
            Log.d(EmailPopup.LOG_TAG, "Partial wakeLock acquired");
        }
        else {
            Log.v(EmailPopup.LOG_TAG, "Partial wakeLock was already acquired");
        }
    }

    static synchronized void acquireFullWakeLock(Context context) {
        PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        if (fullWakeLock==null) {
            fullWakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE,
                EmailPopup.LOG_TAG);
            fullWakeLock.acquire();
            Log.d(EmailPopup.LOG_TAG, "Full wakeLock acquired");
        }
        else {
            Log.v(EmailPopup.LOG_TAG, "Full wakeLock was already acquired");
        }
    }

    static synchronized void releasePartialWakeLock() {
        if (partialwakeLock!=null) {
            partialwakeLock.release();
            partialwakeLock = null;
            Log.d(EmailPopup.LOG_TAG, "Partial wakeLock released");
        }
        else {
            Log.v(EmailPopup.LOG_TAG, "Partial wakeLock was already released");
        }
    }//releasePartialWakeLock

    static synchronized void releaseAllWakeLocks() {
        releasePartialWakeLock();

        if (fullWakeLock!=null) {
            fullWakeLock.release();
            fullWakeLock = null;
            Log.d(EmailPopup.LOG_TAG, "Full wakeLock released");
        }
        else {
            Log.v(EmailPopup.LOG_TAG, "Full wakeLock was already released");
        }
    }

}//WakeLockManager
