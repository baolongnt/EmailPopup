package com.blntsoft.emailpopup;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main
    extends Activity
    implements OnClickListener {

    Button testButton;
    Button searchButton;
    Button viewEmailButton;
    EditText emailEditText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        testButton = (Button)findViewById(R.id.test_button);
        testButton.setOnClickListener(this);

        searchButton = (Button)findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);

        viewEmailButton = (Button)findViewById(R.id.view_email_button);
        viewEmailButton.setOnClickListener(this);
        
        emailEditText = (EditText)findViewById(R.id.email_edit_text);

    }

    @Override
    public void onClick(View view) {
        if (view==testButton) {

            Uri uri;
            Intent intent;

            uri = Uri.parse("email://messages/1/Inbox/123");
            intent = new Intent(EmailPopup.ACTION_EMAIL_RECEIVED, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EmailPopup.EXTRA_FROM, "anhthu@nguyentrong.com");
            intent.putExtra(EmailPopup.EXTRA_FOLDER, "Inbox");
            intent.putExtra(EmailPopup.EXTRA_SUBJECT, "TEST SUBJECT");
            this.sendBroadcast(intent);
            Log.e(EmailPopup.LOG_TAG, "1st broadcast sent");

            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2500);
                    }
                    catch (InterruptedException e) {
                        Log.e(EmailPopup.LOG_TAG, null, e);
                    }

                    for (int i=0; i<5; i++) {
                        try {
                            Thread.sleep(500);
                        }
                        catch (InterruptedException e) {
                            Log.e(EmailPopup.LOG_TAG, null, e);
                        }

                        Uri uri = Uri.parse("email://messages/2/Inbox/"+1);
                        Intent intent = new Intent(EmailPopup.ACTION_EMAIL_RECEIVED, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(EmailPopup.EXTRA_ACCOUNT, "TEST");
                        intent.putExtra(EmailPopup.EXTRA_FROM, "TEST SENDER " + i + " <test@test.com>");
                        intent.putExtra(EmailPopup.EXTRA_FOLDER, "Inbox");
                        intent.putExtra(EmailPopup.EXTRA_SUBJECT, "TEST SUBJECT " + i);
                        Main.this.sendBroadcast(intent);
                        Log.e(EmailPopup.LOG_TAG, i + "th broadcast sent");
                    }
                }//run
            };//Thread
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();
        }
        else if (view==searchButton) {
            String email = emailEditText.getText().toString();
            Log.d(EmailPopup.LOG_TAG, "Email: " + email);

            long contactId = ContactUtils.getIdByEmailAddress(this, email);
            Toast.makeText(this, "Contact id: " + contactId, Toast.LENGTH_SHORT).show();

            boolean starred = ContactUtils.isContactStarred(this, contactId);
            Toast.makeText(this, "Contact Starred: " + starred, Toast.LENGTH_SHORT).show();
        }
        else {
            //Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("email://messages/0/INBOX/1255020785.890845.m1gemini00-01.prod.mesa1.1091393888"));
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("email://messages/0/INBOX/1255020785.890845.m1gemini00-01.prod.mesa1.109139388"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }//if testButton
    }
}
