package com.blntsoft.emailpopup;

import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import com.crashlytics.android.Crashlytics;

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
        Crashlytics.start(context);

        Log.d(EmailPopup.LOG_TAG, intent.getDataString());

        String action = intent.getAction();
        if (action.endsWith(EmailPopup.ACTION_EMAIL_RECEIVED)) {
            String extraPrefix = action.substring(0, action.indexOf(EmailPopup.ACTION_EMAIL_RECEIVED));

            WakeLockManager.acquirePartialWakeLock(context);

            String fromEmailList = intent.getStringExtra(extraPrefix+EmailPopup.EXTRA_FROM);
            String folderName = intent.getStringExtra(extraPrefix+EmailPopup.EXTRA_FOLDER);
            String subject = intent.getStringExtra(extraPrefix+EmailPopup.EXTRA_SUBJECT);
            StringBuilder sb = new StringBuilder();
            sb
                .append("Email received from ")
                .append(fromEmailList)
                .append(": ")
                .append(subject);
            String logMessage = sb.toString();
            Log.d(EmailPopup.LOG_TAG, "Received intent: " + logMessage);

            boolean isFromSelf = intent.getBooleanExtra(extraPrefix+EmailPopup.EXTRA_FROM_SELF, false);
            if (isFromSelf) {
                Log.d(EmailPopup.LOG_TAG, "Email from self --> No popup");
                WakeLockManager.releasePartialWakeLock();
                return;
            }

            Date emailDate = (Date)intent.getSerializableExtra(extraPrefix+EmailPopup.EXTRA_SENT_DATE);
            if (emailDate!=null) {
                //TODO: Make this a preference
                if (System.currentTimeMillis()-emailDate.getTime() > 8*60*60*1000) {
                    Log.d(EmailPopup.LOG_TAG, "Email older than 8h --> No popup");
                    WakeLockManager.releasePartialWakeLock();
                    return;
                }
            }
            else {
                Log.d(EmailPopup.LOG_TAG, "Missing date extra");
            }

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

            Address[] fromAddressList = Address.parseUnencoded(fromEmailList);
            Address fromAddress = null;
            if (fromAddressList.length>0) {
                fromAddress = fromAddressList[0];
            }
            else {
                fromAddress = new Address("", null);
            }
            Log.v(EmailPopup.LOG_TAG, "fromAddress.mPersonal: " + fromAddress.mPersonal);
            
            long contactId = ContactUtils.getIdByEmailAddress(context, fromAddress.mAddress);
            if (contactId==-1
                && fromAddress.mPersonal!=null) {
                contactId = ContactUtils.getIdByName(context, fromAddress.mPersonal);
            }
            Log.d(EmailPopup.LOG_TAG, "contactId: " + contactId);
            
            if (Preferences.CONTACTS_FILTERING_PREF_VALUE.equals(contactFiltering)) {
                if (contactId==-1) {
                    Log.d(EmailPopup.LOG_TAG, "Contact only --> No popup");
                    WakeLockManager.releasePartialWakeLock();
                    return;
                }
                else {
                    //nothing
                }
            }
            else if (Preferences.STARRED_CONTACT_FILTERING_PREF_VALUE.equals(contactFiltering)) {
                if (contactId==-1) {
                    Log.d(EmailPopup.LOG_TAG, "Not a contact (can't be starred) --> No popup");
                    WakeLockManager.releasePartialWakeLock();
                    return;
                }
                else if (contactId!=-1
                    && !ContactUtils.isContactStarred(context, contactId)) {
                    Log.d(EmailPopup.LOG_TAG, "Not a starred contact --> No popup");
                    WakeLockManager.releasePartialWakeLock();
                    return;
                }
                else {
                    //nothing
                }
            }
            else {
                //No contact filtering
            }

            EmailMessage message = new EmailMessage();
            message.account = intent.getStringExtra(extraPrefix+EmailPopup.EXTRA_ACCOUNT);
            message.uriString = intent.getData().toString();
            message.subject = subject;
            message.senderName = fromAddress.mPersonal;
            message.senderEmail = fromAddress.mAddress;
            message.contactId = contactId;
            message.autoClose = intent.getBooleanExtra(extraPrefix+EmailPopup.EXTRA_AUTO_CLOSE, true);

            Intent i = new Intent(context, EmailPopupService.class);
            i.putExtra(EmailPopup.EMAIL_MESSAGE_EXTRA, message);
            context.startService(i);
        }//if intent action
    }//onReceive

}//EmailReceiver
