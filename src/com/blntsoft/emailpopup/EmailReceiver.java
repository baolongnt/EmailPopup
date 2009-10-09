package com.blntsoft.emailpopup;

import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;

public class EmailReceiver extends BroadcastReceiver  {

    private static KeyguardLock keyguardLock;

    private static final String[] IGNORE_FOLDER_NAMES = new String[] {
        "Trash",
        "Deleted Items",
        "Sent",
        "Sent Items",
        "Drafts",
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(EmailPopup.ACTION_EMAIL_RECEIVED)) {
            WakeLockManager.acquirePartialWakeLock(context);

            String fromEmailList = intent.getStringExtra(EmailPopup.EXTRA_FROM);
            String folderName = intent.getStringExtra(EmailPopup.EXTRA_FOLDER);
            String subject = intent.getStringExtra(EmailPopup.EXTRA_SUBJECT);
            StringBuilder sb = new StringBuilder();
            sb
                .append("Email received from ")
                .append(fromEmailList)
                .append(": ")
                .append(subject);
            String logMessage = sb.toString();
            Log.d(EmailPopup.LOG_TAG, "Received intent: " + logMessage);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (!preferences.getBoolean(Preferences.ON_OFF_SWITCH_PREF_KEY, true)) {
                Log.d(EmailPopup.LOG_TAG, "Email Popup is 'Off' --> No popup");
                WakeLockManager.releasePartialWakeLock();
                return;
            }

            for (String f : IGNORE_FOLDER_NAMES) {
                if (f.equalsIgnoreCase(folderName)) {
                    Log.d(EmailPopup.LOG_TAG, "Ignoring email from folder: " + folderName);
                    WakeLockManager.releasePartialWakeLock();
                    return;
                }
            }

            String contactFiltering = preferences.getString(Preferences.CONTACT_FILTERING_PREF_KEY, Preferences.ALL_FILTERING_PREF_VALUE);

            Address fromAddress = Address.parseUnencoded(fromEmailList)[0];
            Log.v(EmailPopup.LOG_TAG, "fromAddress.mPersonal: " + fromAddress.mPersonal);
            
            long contactId = ContactUtils.getIdByEmailAddress(context, fromAddress.mAddress);
            if (contactId==-1
                && fromAddress.mPersonal!=null) {
                contactId = ContactUtils.getIdByName(context, fromAddress.mPersonal);
            }

            Log.d(EmailPopup.LOG_TAG, "contactId: " + contactId);
            if (Preferences.CONTACT_FILTERING_PREF_VALUE.equals(contactFiltering)
                && contactId==-1) {
                Log.d(EmailPopup.LOG_TAG, "Contact only --> No popup");
                WakeLockManager.releasePartialWakeLock();
                return;
            }

            Bitmap photo;
            if (contactId!=-1) {
                photo = ContactUtils.getContactPhotoById(context, contactId);
            }
            else {
                photo = null;
            }
            
            if (Preferences.PHOTO_FILTERING_PREF_VALUE.equals(contactFiltering)
                && photo==null) {
                Log.d(EmailPopup.LOG_TAG, "Contact with photo only --> No popup");
                return;
            }

            EmailMessage message = new EmailMessage();
            message.subject = subject;
            message.senderName = fromAddress.mPersonal;
            message.senderEmail = fromAddress.mAddress;
            message.contactId = contactId;

            KeyguardManager.disableKeyguard(context);

            Intent i = new Intent(EmailPopup.ACTION_NOTIFICATION_VIEW, intent.getData(), context, EmailNotification.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra(EmailNotification.EMAIL_MESSAGE_EXTRA, message);
            context.startActivity(i);
        }//if intent action
    }//onReceive

}//EmailReceiver
