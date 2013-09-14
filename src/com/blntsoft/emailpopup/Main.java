package com.blntsoft.emailpopup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

    Button testOneButton;
    Button testMultipleButton;

    EditText emailEditText;
    Button searchByEmailButton;

    EditText nameEditText;
    Button searchByNameButton;

    Button viewEmailButton;

    Button testFromSelfButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        testOneButton = (Button)findViewById(R.id.test_one_button);
        testOneButton.setOnClickListener(this);
        
        testMultipleButton = (Button)findViewById(R.id.test_multiple_button);
        testMultipleButton.setOnClickListener(this);

        emailEditText = (EditText)findViewById(R.id.email_edit_text);
        searchByEmailButton = (Button)findViewById(R.id.search_by_email_button);
        searchByEmailButton.setOnClickListener(this);

        nameEditText = (EditText)findViewById(R.id.name_edit_text);
        searchByNameButton = (Button)findViewById(R.id.search_by_name_button);
        searchByNameButton.setOnClickListener(this);

        viewEmailButton = (Button)findViewById(R.id.view_email_button);
        viewEmailButton.setOnClickListener(this);

        testFromSelfButton = (Button)findViewById(R.id.test_from_self_button);
        testFromSelfButton.setOnClickListener(this);
    }//onCreate

    @Override
    public void onClick(View view) {
        //final String prefix = "com.fsck.k9.intent.";
        final String prefix = "com.android.email.intent.";

        if (view==testOneButton) {
            Thread t = new Thread() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(2500);
                    }
                    catch (InterruptedException e) {
                        Log.w(prefix+EmailPopup.LOG_TAG, null, e);
                    }
                    

                    Uri uri = Uri.parse("email://messages/1/Inbox/123");
                    Intent intent = new Intent(prefix+EmailPopup.ACTION_EMAIL_RECEIVED, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(prefix+EmailPopup.EXTRA_FROM, "Anne N. <anne@blntsoft.com>");
                    intent.putExtra(prefix+EmailPopup.EXTRA_FOLDER, "Inbox");
                    intent.putExtra(prefix+EmailPopup.EXTRA_ACCOUNT, "Personal");
                    intent.putExtra(prefix+EmailPopup.EXTRA_SUBJECT, "Are we on tonight?");
                    intent.putExtra(prefix+EmailPopup.EXTRA_AUTO_CLOSE, false);
                    getApplicationContext().sendBroadcast(intent);
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
                        Log.e(prefix+EmailPopup.LOG_TAG, null, e);
                    }

                    for (int i=0; i<5; i++) {
                        try {
                            Thread.sleep(500);
                        }
                        catch (InterruptedException e) {
                            Log.e(prefix+EmailPopup.LOG_TAG, null, e);
                        }

                        Uri uri = Uri.parse("email://messages/2/Inbox/"+1);
                        Intent intent = new Intent(prefix+EmailPopup.ACTION_EMAIL_RECEIVED, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(prefix+EmailPopup.EXTRA_ACCOUNT, "TEST");
                        intent.putExtra(prefix+EmailPopup.EXTRA_FROM, "May the Force be with you <email-popup@blntsoft.com>");
                        intent.putExtra(prefix+EmailPopup.EXTRA_FOLDER, "Inbox");
                        intent.putExtra(prefix+EmailPopup.EXTRA_SUBJECT, "Email Popup Rocks! #" + i);
                        intent.putExtra(prefix+EmailPopup.EXTRA_SENT_DATE, new Date());
                        Main.this.sendBroadcast(intent);
                        Log.e(prefix+EmailPopup.LOG_TAG, i + "th broadcast sent");
                    }
                }//run
            };//Thread
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();
            finish();
        }
        else if (view==searchByEmailButton) {
            String email = emailEditText.getText().toString();
            Log.d(prefix+EmailPopup.LOG_TAG, "Email: " + email);

            long contactId = ContactUtils.getIdByEmailAddress(this, email);
            Toast.makeText(this, "Contact id: " + contactId, Toast.LENGTH_SHORT).show();

            if (contactId!=-1) {
                boolean starred = ContactUtils.isContactStarred(this, contactId);
                Toast.makeText(this, "Contact Starred: " + starred, Toast.LENGTH_SHORT).show();
            }
        }
        else if (view==searchByNameButton) {
            String name = nameEditText.getText().toString();
            Log.d(prefix+EmailPopup.LOG_TAG, "Name: " + name);

            long contactId = ContactUtils.getIdByName(this, name);
            Toast.makeText(this, "Contact id: " + contactId, Toast.LENGTH_SHORT).show();

            if (contactId!=-1) {
                boolean starred = ContactUtils.isContactStarred(this, contactId);
                Toast.makeText(this, "Contact Starred: " + starred, Toast.LENGTH_SHORT).show();
            }
        }
        else if (view==viewEmailButton) {
            //Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("email://messages/0/INBOX/1255020785.890845.m1gemini00-01.prod.mesa1.1091393888"));
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("email://messages/0/INBOX/1255020785.890845.m1gemini00-01.prod.mesa1.109139388"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }
        else if (view==testFromSelfButton) {
            Thread t = new Thread() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(2500);
                    }
                    catch (InterruptedException e) {
                        Log.w(prefix+EmailPopup.LOG_TAG, null, e);
                    }


                    Uri uri = Uri.parse("email://messages/1/Inbox/123");
                    Intent intent = new Intent(prefix+EmailPopup.ACTION_EMAIL_RECEIVED, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(prefix+EmailPopup.EXTRA_FROM, "Anne N. <anne@blntsoft.com>");
                    intent.putExtra(prefix+EmailPopup.EXTRA_FOLDER, "Inbox");
                    intent.putExtra(prefix+EmailPopup.EXTRA_ACCOUNT, "Personal");
                    intent.putExtra(prefix+EmailPopup.EXTRA_SUBJECT, "Are we on tonight?");
                    intent.putExtra(prefix+EmailPopup.EXTRA_FROM_SELF, true);
                    getApplicationContext().sendBroadcast(intent);
                }
            };
            t.start();
            finish();
        }//if
    }
}
