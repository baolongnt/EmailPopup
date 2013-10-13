package com.blntsoft.emailpopup;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class Main
    extends Activity
    implements OnClickListener {

    private final String INTENT_PREFIX = "com.fsck.k9.intent.";

    EditText accountNumberEditText;
    EditText nameEditText;
    EditText emailEditText;

    Button testOneButton;
    Button testMultipleButton;
    Button testFromSelfButton;

    Button searchByEmailButton;
    Button searchByNameButton;

    Button viewEmailButton;
    Button popupEmailButton;
    Button deleteEmailButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        accountNumberEditText = (EditText)findViewById(R.id.account_number_edit_text);
        nameEditText = (EditText)findViewById(R.id.name_edit_text);
        emailEditText = (EditText)findViewById(R.id.email_edit_text);

        testOneButton = (Button)findViewById(R.id.test_one_button);
        testOneButton.setOnClickListener(this);
        
        testMultipleButton = (Button)findViewById(R.id.test_multiple_button);
        testMultipleButton.setOnClickListener(this);

        testFromSelfButton = (Button)findViewById(R.id.test_from_self_button);
        testFromSelfButton.setOnClickListener(this);

        searchByEmailButton = (Button)findViewById(R.id.search_by_email_button);
        searchByEmailButton.setOnClickListener(this);

        searchByNameButton = (Button)findViewById(R.id.search_by_name_button);
        searchByNameButton.setOnClickListener(this);

        viewEmailButton = (Button)findViewById(R.id.view_email_button);
        viewEmailButton.setOnClickListener(this);

        popupEmailButton = (Button)findViewById(R.id.popup_email_button);
        popupEmailButton.setOnClickListener(this);

        deleteEmailButton = (Button)findViewById(R.id.delete_email_button);
        deleteEmailButton.setOnClickListener(this);
    }//onCreate

    @Override
    public void onClick(View view) {
        if (view==testOneButton) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2500);
                    }
                    catch (InterruptedException e) {
                        Log.w(INTENT_PREFIX+EmailPopup.LOG_TAG, null, e);
                    }

                    Uri uri = Uri.parse("email://messages/" + accountNumberEditText.getText().toString() + "/Inbox/123");
                    Intent intent = new Intent(INTENT_PREFIX+EmailPopup.ACTION_EMAIL_RECEIVED, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_FROM, nameEditText.getText().toString() + " <" + emailEditText.getText().toString() + ">");
                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_FOLDER, "Inbox");
                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_ACCOUNT, "Personal");
                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_SUBJECT, "Are we on tonight?");
                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_AUTO_CLOSE, false);
                    Main.this.sendBroadcast(intent);
                }
            };
            t.start();
            finish();
        }
        else if (view==testMultipleButton) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2500);
                    }
                    catch (InterruptedException e) {
                        Log.e(INTENT_PREFIX+EmailPopup.LOG_TAG, null, e);
                    }

                    for (int i=0; i<5; i++) {
                        try {
                            Thread.sleep(500);
                        }
                        catch (InterruptedException e) {
                            Log.e(INTENT_PREFIX+EmailPopup.LOG_TAG, null, e);
                        }

                        Uri uri = Uri.parse("email://messages/" + accountNumberEditText.getText().toString() + "/Inbox/" + i);
                        Intent intent = new Intent(INTENT_PREFIX+EmailPopup.ACTION_EMAIL_RECEIVED, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_ACCOUNT, "TEST");
                        intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_FROM, nameEditText.getText().toString() + " <" + emailEditText.getText().toString() + ">");
                        intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_FOLDER, "Inbox");
                        intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_SUBJECT, "Email Popup Rocks! #" + i);
                        intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_SENT_DATE, new Date());
                        Main.this.sendBroadcast(intent);
                        Log.e(INTENT_PREFIX+EmailPopup.LOG_TAG, i + "th broadcast sent");
                    }
                }//run
            };//Thread
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();
            finish();
        }
        else if (view==testFromSelfButton) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2500);
                    }
                    catch (InterruptedException e) {
                        Log.w(INTENT_PREFIX+EmailPopup.LOG_TAG, null, e);
                    }

                    Uri uri = Uri.parse("email://messages/" + accountNumberEditText.getText().toString() + "/Inbox/123");
                    Intent intent = new Intent(INTENT_PREFIX+EmailPopup.ACTION_EMAIL_RECEIVED, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_FROM, nameEditText.getText().toString() + " <" + emailEditText.getText().toString() + ">");
                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_ACCOUNT, "Personal");
                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_SUBJECT, "Are we on tonight?");
                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_FROM_SELF, true);
                    Main.this.sendBroadcast(intent);
                }
            };
            t.start();
            finish();
        }
        else if (view==searchByEmailButton) {
            String email = emailEditText.getText().toString();
            Log.d(INTENT_PREFIX+EmailPopup.LOG_TAG, "Email: " + email);

            long contactId = ContactUtils.getIdByEmailAddress(this, email);
            Toast.makeText(this, "Contact id: " + contactId, Toast.LENGTH_SHORT).show();

            if (contactId!=-1) {
                boolean starred = ContactUtils.isContactStarred(this, contactId);
                Toast.makeText(this, "Contact Starred: " + starred, Toast.LENGTH_SHORT).show();

                Bitmap photo = ContactUtils.getContactPhotoById(this, contactId, R.drawable.ic_contact_picture);
                Toast.makeText(this, "Contact photo found: " + (photo!=null), Toast.LENGTH_SHORT).show();
            }
        }
        else if (view==searchByNameButton) {
            String name = nameEditText.getText().toString();
            Log.d(INTENT_PREFIX+EmailPopup.LOG_TAG, "Name: " + name);

            long contactId = ContactUtils.getIdByName(this, name);
            Toast.makeText(this, "Contact id: " + contactId, Toast.LENGTH_SHORT).show();

            if (contactId!=-1) {
                boolean starred = ContactUtils.isContactStarred(this, contactId);
                Toast.makeText(this, "Contact Starred: " + starred, Toast.LENGTH_SHORT).show();

                Bitmap photo = ContactUtils.getContactPhotoById(this, contactId, R.drawable.ic_contact_picture);
                Toast.makeText(this, "Contact photo found: " + (photo!=null), Toast.LENGTH_SHORT).show();
            }
        }
        else if (view==viewEmailButton) {
            new EmailLookupTask().execute("view");
        }
        else if (view==popupEmailButton) {
            new EmailLookupTask().execute("popup");
        }
        else if (view==deleteEmailButton) {
            new EmailLookupTask().execute("delete");
        }//if
    }//onClick

    class EmailLookupTask extends AsyncTask<String, String, Intent> {

        private static final int MAX_EMAIL_INDEX = 50;

        protected Intent doInBackground(String... params) {
            String action = params[0];

            String accountNumber = accountNumberEditText.getText().toString();
            if (accountNumber==null
                    || accountNumber.equals("")) {
                return null;
            }

            //K-9 mail content provider does not do filtering so we have to do it below in our code
            Uri uri = Uri.parse("content://com.fsck.k9.messageprovider/inbox_messages/");
            String[] projectionArray = new String[] { "account", "accountNumber", "sender", "subject", "uri", "delUri", "date", "senderAddress" };
            Cursor cursor = getContentResolver().query(
                    uri,
                    projectionArray,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                try {
                    if (cursor.getCount() > 0) {
                        int i=0;
                        cursor.moveToFirst();
                        while(cursor.moveToNext()) {
                            StringBuilder sb = new StringBuilder();
                            for (int j=0; j<projectionArray.length; j++) {
                                sb.append(cursor.getString(j)).append(' ');
                            }
                            Log.d(EmailPopup.LOG_TAG, sb.toString());
                            if (accountNumber.equals(cursor.getString(1))) {
                                publishProgress("Got email: " + cursor.getString(2) + ": " + cursor.getString(3));
                                if ("view".equals(action)) {
                                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(cursor.getString(4)));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    return intent;
                                }
                                else if ("popup".equals(action)) {
                                    Intent intent = new Intent(INTENT_PREFIX+EmailPopup.ACTION_EMAIL_RECEIVED, Uri.parse(cursor.getString(4)));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_ACCOUNT, "TEST");
                                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_FROM, cursor.getString(2) + "<" + cursor.getString(7) + ">");
                                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_FOLDER, "Inbox");
                                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_SUBJECT, cursor.getString(3));
                                    intent.putExtra(INTENT_PREFIX+EmailPopup.EXTRA_SENT_DATE, new Date(cursor.getLong(6)));
                                    Main.this.sendBroadcast(intent);
                                    publishProgress("Popup intent broadcast sent");
                                }
                                else if ("delete".equals(action)) {
                                    getContentResolver().delete(Uri.parse(cursor.getString(5)), null, null);
                                    publishProgress("Email deleted");
                                }
                                else {
                                    publishProgress("Unknown action: " + action);
                                }
                                return null;
                            }
                            i++;
                            if (i > MAX_EMAIL_INDEX) {
                                break;
                            }
                        }
                        publishProgress("No email found in top " + MAX_EMAIL_INDEX);
                    }
                    else {
                        publishProgress("No record found");
                    }

                }
                finally {
                    cursor.close();
                }
            }
            else {
                publishProgress("Null cursor");
            }

            return null;
        }//doInBackground

        protected void onProgressUpdate(String... progress) {
            Toast.makeText(Main.this, progress[0], Toast.LENGTH_SHORT).show();
        }//onProgressUpdate

        protected void onPostExecute(Intent intent) {
            if (intent != null) {
                startActivity(intent);
            }
        }
    }//EmailLookupTask

}//Main
