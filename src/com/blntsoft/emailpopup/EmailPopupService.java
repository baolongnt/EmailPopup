package com.blntsoft.emailpopup;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@nguyentrong.com)
 */
public class EmailPopupService
    extends Service
    implements Runnable {

    private static final List<EmailMessage> emailMessageQueue = new ArrayList<EmailMessage>();
    private Thread workerThead;

    @Override
    public  IBinder onBind(Intent intent) {
        return  null;
    }

    @Override
    public void onStart (Intent intent, int startId) {
        Log.e(EmailPopup.LOG_TAG, "onStart()");
        synchronized (emailMessageQueue) {
            emailMessageQueue.add((EmailMessage)intent.getSerializableExtra(EmailPopup.EMAIL_MESSAGE_EXTRA));
            Log.e(EmailPopup.LOG_TAG, "Message added to queue");

            if (workerThead==null
                || !workerThead.isAlive()) {
                workerThead = new Thread(this);
                workerThead.start();
                Log.e(EmailPopup.LOG_TAG, "Worker thread started");
            }
            
            WakeLockManager.acquirePartialWakeLock(this);
            KeyguardManager.disableKeyguard(this);
        }
    }

    public void run() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EmailPopupService.this);
        int displayTime = Integer.parseInt(preferences.getString(Preferences.TIME_DISPLAY_PREF_KEY, getString(R.string.time_display_preference_default)));

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
            try {
                Thread.sleep(displayTime * 1000);
            }
            catch (InterruptedException e) {
                Log.e(EmailPopup.LOG_TAG, null, e);
            }
        }
    }//run

}//EmailPopupService

