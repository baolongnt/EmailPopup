package com.blntsoft.emailpopup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@nguyentrong.com)
 */
public class EmailNotification 
    extends Activity
    implements OnClickListener, Runnable {

    private EmailMessage message;
    private Bitmap contactPhoto;
    private boolean isDestroyed;
    
    private ImageView photoImageView;
    private TextView fromTextView;
    private TextView subjectTextView;
    private Button okButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(EmailPopup.LOG_TAG, "EmailNotification.onCreate()");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification);

        photoImageView = (ImageView)findViewById(R.id.sender_photo);
        fromTextView = (TextView)findViewById(R.id.from);
        subjectTextView = (TextView)findViewById(R.id.subject);
        okButton = (Button)findViewById(R.id.ok_button);

        okButton.setOnClickListener(this);

        Intent intent = getIntent();
        message = (EmailMessage)intent.getSerializableExtra(EmailPopup.EMAIL_MESSAGE_EXTRA);
        if (message.senderName!=null) {
            fromTextView.setText(message.senderName + " <" + message.senderEmail + ">");
        }
        else {
            fromTextView.setText(message.senderEmail);
        }
        subjectTextView.setText(message.subject);

        if (message.contactId!=-1) {
            new FetchContactPhotoTask().execute();
        }

        setTitle(getString(R.string.notification_title, message.account));

        isDestroyed = false;
        new Thread(this).start();
    }//onCreate

    @Override
    public void onClick(View view) {
        if (view==okButton) {
            close();
        }
    }//onClick

    @Override
    public void onDestroy() {
        Log.e(EmailPopup.LOG_TAG, "EmailNotification.onDestroy()");
        isDestroyed = true;
        super.onDestroy();
    }

    private void close() {
        Log.e(EmailPopup.LOG_TAG, "EmailNotification.close()");
        finish();
    }//close

    @Override
    public void run() {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EmailNotification.this);
            int displayTime = Integer.parseInt(preferences.getString(Preferences.TIME_DISPLAY_PREF_KEY, getString(R.string.time_display_preference_default)));
            Thread.sleep((int)(displayTime * 1000 * 1.1));
            if (!isDestroyed) {
                close();
            }
        }
        catch (Exception e) {
            Log.e(EmailPopup.LOG_TAG, null, e);
        }
    }//run

    private class FetchContactPhotoTask extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Log.v(EmailPopup.LOG_TAG, "Loading contact photo in background... " + message.contactId);
            return ContactUtils.getContactPhotoById(EmailNotification.this, message.contactId);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Log.v(EmailPopup.LOG_TAG, "Done loading contact photo");
            contactPhoto = result;
            if (result != null) {
                photoImageView.setImageBitmap(contactPhoto);
            }
            else {
                photoImageView.setImageDrawable(EmailNotification.this.getResources().getDrawable(android.R.drawable.ic_dialog_info));
            }
        }
    }//FetchContactPhotoTask

}//EmailNotification
