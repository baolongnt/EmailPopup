package com.blntsoft.emailpopup;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

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
            Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode(emailAddress.trim()));
            Cursor cursor = context.getContentResolver().query(
                    uri,
                    new String[] {
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID
                    },
                    null,
                    null,
                    null
            );

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
                ContactsContract.Contacts.CONTENT_URI,
                new String[] {
                    ContactsContract.Contacts._ID
                },
                ContactsContract.Contacts.DISPLAY_NAME + " = ?",
                new String[] { name.trim() },
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

    public static Bitmap getContactPhotoById(Context context, long id, int defaultPhotoId) {
        Log.d(EmailPopup.LOG_TAG, "getContactPhotoById(): " + id);
        if (id==-1) {
            return null;
        }
        else {
            try {
                InputStream in;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    in = ContactsContract.Contacts.openContactPhotoInputStream(
                            context.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id),
                            true
                    );
                }
                else {
                    in = ContactsContract.Contacts.openContactPhotoInputStream(
                            context.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id)
                    );
                }
                if (in == null) {
                    return null;
                }
                else {
                    Bitmap photo = BitmapFactory.decodeStream(in);
                    in.close();
                    return photo;
                }
            }
            catch (IOException e) {
                Log.d(EmailPopup.LOG_TAG, e.toString());
                return null;
            }
        }
    }//getContactPhotoById

    public static boolean isContactStarred(Context context, long id) {
        int isStarred;
        Cursor cursor = context.getContentResolver().query(
            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id),
            new String[] { ContactsContract.Contacts.STARRED },
            null,
            null,
            null);

        if (cursor != null) {
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    isStarred = Integer.valueOf(cursor.getInt(0));
                    Log.d(EmailPopup.LOG_TAG, "Is contact starred: " + id + "=" + isStarred);
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