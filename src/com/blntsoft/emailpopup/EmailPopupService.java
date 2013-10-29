package com.blntsoft.emailpopup;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@blntsoft.com)
 */
public class EmailPopupService
    extends Service
    implements Runnable {

    private Thread workerThread;

    @Override
    public  IBinder onBind(Intent intent) {
        return  null;
    }

    public void handleCommand(Intent intent) {
        try {
            Log.e(EmailPopup.LOG_TAG, "onStart()");

            EmailMessageQueue emailMessageQueue = EmailMessageQueue.getInstance();
            synchronized (emailMessageQueue) {
                if (intent != null) {
                    emailMessageQueue.add((EmailMessage)intent.getSerializableExtra(EmailPopup.EMAIL_MESSAGE_EXTRA));
                    Log.e(EmailPopup.LOG_TAG, "Message added to queue");

                    if (workerThread ==null
                        || !workerThread.isAlive()) {
                        workerThread = new Thread(this);
                        workerThread.start();
                        Log.e(EmailPopup.LOG_TAG, "Worker thread started");
                    }

                    WakeLockManager.acquirePartialWakeLock(this);
                    KeyguardManager.disableKeyguard(this);
                }
                else {
                    //Should not happen anymore
                    //See http://crashes.to/s/ff42bdba8d7
                    //See http://stackoverflow.com/questions/5856861/why-android-service-crashes-with-nullpointerexception
                    Crashlytics.log("Intent passed to service is null!");
                }
            }
        }
        catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    // This is the old onStart method that will be called on the pre-2.0
    // platform.  On 2.0 or later we override onStartCommand() so this
    // method will not be called.
    @Override
    public void onStart(Intent intent, int startId) {
        Crashlytics.start(this);

        handleCommand(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Crashlytics.start(this);

        handleCommand(intent);

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    public void run() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EmailPopupService.this);
        int displayTime = Integer.parseInt(preferences.getString(Preferences.TIME_DISPLAY_PREF_KEY, getString(R.string.time_display_preference_default)));

        EmailMessageQueue emailMessageQueue = EmailMessageQueue.getInstance();
        while (true) {
            EmailMessage emailMessage = null;
            synchronized(emailMessageQueue) {
                int size = emailMessageQueue.size();
                if (size==0) {
                    WakeLockManager.releaseAllWakeLocks();
                    KeyguardManager.reenableKeyguard();

                    EmailPopupService.this.stopSelf();
                    return;
                }
                else {
                    emailMessage = emailMessageQueue.remove(0);
                }
            }

            WakeLockManager.acquireFullWakeLock(this);

            Intent i = new Intent(EmailPopup.ACTION_NOTIFICATION_VIEW, Uri.parse(emailMessage.uriString), EmailPopupService.this, EmailNotification.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra(EmailPopup.EMAIL_MESSAGE_EXTRA, emailMessage);
            EmailPopupService.this.startActivity(i);

            synchronized(emailMessageQueue) {
                try {
                    emailMessageQueue.wait(displayTime * 1000);
                }
                catch (InterruptedException e) {
                    Log.e(EmailPopup.LOG_TAG, null, e);
                }
            }
        }
    }//run

}//EmailPopupService

