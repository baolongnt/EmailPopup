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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@nguyentrong.com)
 */
public class EmailNotification 
    extends Activity
    implements Runnable {

    private static final int MAX_SUBJECT_LENGTH = 140;

    private EmailMessage message;
    private boolean isDestroyed;
    
    private ImageView photoImageView;
    private TextView fromNameTextView;
    private TextView fromEmailTextView;
    private TextView subjectTextView;
    //private Button okButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(EmailPopup.LOG_TAG, "EmailNotification.onCreate()");

        super.onCreate(savedInstanceState);

        Window w = getWindow();
        w.requestFeature(Window.FEATURE_LEFT_ICON);

        setContentView(R.layout.notification);
        
        w.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);

        photoImageView = (ImageView)findViewById(R.id.sender_photo);
        fromNameTextView = (TextView)findViewById(R.id.from_name);
        fromEmailTextView = (TextView)findViewById(R.id.from_email);
        subjectTextView = (TextView)findViewById(R.id.subject);
        //okButton = (Button)findViewById(R.id.ok_button);

        //okButton.setOnClickListener(this);

        Intent intent = getIntent();
        message = (EmailMessage)intent.getSerializableExtra(EmailPopup.EMAIL_MESSAGE_EXTRA);
        if (message.senderName!=null
            && !"".equals(message.senderName)) {
            fromNameTextView.setText(message.senderName);
            fromEmailTextView.setText(message.senderEmail);
            fromEmailTextView.setVisibility(View.VISIBLE);
        }
        else {
            fromNameTextView.setText(message.senderEmail);
            fromEmailTextView.setVisibility(View.GONE);
        }
        String subject = message.subject;
        if (subject.length()>MAX_SUBJECT_LENGTH) {
            subject = subject.substring(0, MAX_SUBJECT_LENGTH) + "...";
        }
        subjectTextView.setText(subject);

        if (message.contactId!=-1) {
            new FetchContactPhotoTask().execute();
        }

        if (message.account!=null) {
            setTitle(message.account);
        }
        else {
            setTitle(R.string.notification_title);
        }

        isDestroyed = false;
        new Thread(this).start();
    }//onCreate

    /*
    @Override
    public void onClick(View view) {
        if (view==okButton) {
            close();
        }
    }//onClick
     * */

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
            int contactResId;
            if ((System.currentTimeMillis() % 2)==0) {
                contactResId = R.drawable.ic_contact_picture;
            }
            else {
                contactResId = R.drawable.ic_contact_picture_2;
            }
            return ContactUtils.getContactPhotoById(EmailNotification.this, message.contactId, contactResId);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Log.v(EmailPopup.LOG_TAG, "Done loading contact photo");
            if (result != null) {
                photoImageView.setImageBitmap(result);
            }
        }
    }//FetchContactPhotoTask

}//EmailNotification
