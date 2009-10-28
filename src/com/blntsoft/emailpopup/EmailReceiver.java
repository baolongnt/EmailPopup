package com.blntsoft.emailpopup;

import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.Date;

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

            if (preferences.getBoolean(Preferences.KEYGUARD_FILTERING_PREF_KEY, false)
                && !KeyguardManager.isEnabled(context)) {
                Log.d(EmailPopup.LOG_TAG, "Email Popup only if keyguard is enabled --> No popup");
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
            
            if (Preferences.CONTACTS_FILTERING_PREF_VALUE.equals(contactFiltering)
                && contactId==-1) {
                Log.d(EmailPopup.LOG_TAG, "Contact only --> No popup");
                WakeLockManager.releasePartialWakeLock();
                return;
            }

            if (Preferences.STARRED_CONTACT_FILTERING_PREF_VALUE.equals(contactFiltering)
                && !ContactUtils.isContactStarred(context, contactId)) {
                Log.d(EmailPopup.LOG_TAG, "Not starred contact --> No popup");
                return;
            }

            EmailMessage message = new EmailMessage();
            message.account = intent.getStringExtra(EmailPopup.EXTRA_ACCOUNT);
            message.uriString = intent.getData().toString();
            message.subject = subject;
            message.senderName = fromAddress.mPersonal;
            message.senderEmail = fromAddress.mAddress;
            message.contactId = contactId;
            message.autoClose = intent.getBooleanExtra(EmailPopup.EXTRA_AUTO_CLOSE, true);

            Intent i = new Intent(context, EmailPopupService.class);
            i.putExtra(EmailPopup.EMAIL_MESSAGE_EXTRA, message);
            context.startService(i);
        }//if intent action
    }//onReceive

}//EmailReceiver
