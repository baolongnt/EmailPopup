package com.blntsoft.emailpopup;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.util.Log;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@nguyentrong.com)
 */
public class ContactUtils {

    public static long getIdByEmailAddress(Context context, String emailAddress) {
        Log.d(EmailPopup.LOG_TAG, "getIdByEmailAddress(): " + emailAddress);
        if (emailAddress==null
            || emailAddress.equals("")) {
            return -1;
        }
        else {
            long contactId;
            Cursor cursor = context.getContentResolver().query(
                Contacts.ContactMethods.CONTENT_URI,
                new String[] { Contacts.ContactMethods.PERSON_ID },
                "contact_methods.data = ?",
                new String[] { emailAddress.trim() },
                null);

            if (cursor != null) {
                try {
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        contactId = Long.valueOf(cursor.getLong(0));
                        Log.d(EmailPopup.LOG_TAG, "Found contactId by email address: " + contactId);
                    }
                    else {
                        Log.v(EmailPopup.LOG_TAG, "Count = 0");
                        contactId = -1;
                    }
                }
                finally {
                    cursor.close();
                }
            }
            else {
                Log.v(EmailPopup.LOG_TAG, "Cursor is null");
                contactId = -1;
            }

            return contactId;
        }
    }//getIdByEmailAddress

    public static long getIdByName(Context context, String name) {
        Log.d(EmailPopup.LOG_TAG, "getIdByName(): " + name);
        if (name==null
            || name.equals("")) {
            return -1;
        }
        else {
            long contactId;
            Cursor cursor = context.getContentResolver().query(
                Uri.withAppendedPath(Contacts.People.CONTENT_FILTER_URI, name),
                new String[] { Contacts.People._ID },
                null,
                null,
                null
            );

            if (cursor != null) {
                try {
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        contactId = Long.valueOf(cursor.getLong(0));
                        Log.d(EmailPopup.LOG_TAG, "Found contactId by name: " + name);
                    }
                    else {
                        Log.v(EmailPopup.LOG_TAG, "Count = 0");
                        contactId = -1;
                    }
                }
                finally {
                    cursor.close();
                }
            }
            else {
                Log.v(EmailPopup.LOG_TAG, "Cursor is null");
                contactId = -1;
            }

            return contactId;
        }
    }//getIdByName

    public static Bitmap getContactPhotoById(Context context, long id) {
        Log.d(EmailPopup.LOG_TAG, "getContactPhotoById(): " + id);
        if (id==-1) {
            return null;
        }
        else {
            return Contacts.People.loadContactPhoto(
                context,
                Uri.withAppendedPath(Contacts.People.CONTENT_URI, String.valueOf(id)),
                android.R.drawable.ic_dialog_info,
                null
            );
        }
    }//getContactPhotoById

    public static boolean isContactStarred(Context context, long id) {
        int isStarred;
        Cursor cursor = context.getContentResolver().query(
            ContentUris.withAppendedId(People.CONTENT_URI, id),
            new String[] { Contacts.PeopleColumns.STARRED },
            null,
            null,
            null);

        if (cursor != null) {
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    isStarred = Integer.valueOf(cursor.getInt(0));
                    Log.d(EmailPopup.LOG_TAG, "Is contact starred: " + id + "=> " + isStarred);
                }
                else {
                    Log.v(EmailPopup.LOG_TAG, "Count = 0");
                    isStarred = -1;
                }
            }
            finally {
                cursor.close();
            }
        }
        else {
            Log.v(EmailPopup.LOG_TAG, "Cursor is null");
            isStarred = -1;
        }

        if (isStarred==1) {
            return true;
        }
        else {
            return false;
        }
    }//isContactStarred

}//ContactUtils
