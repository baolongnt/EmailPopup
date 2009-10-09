package com.blntsoft.emailpopup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
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

    static final String EMAIL_MESSAGE_EXTRA             = "EMAIL_MESSAGE_EXTRA";

    private EmailMessage message;
    private ImageView photoImageView;
    private TextView fromTextView;
    private TextView subjectTextView;
    private Button okButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(EmailPopup.LOG_TAG, "EmailNotification.onCreate()");

        super.onCreate(savedInstanceState);

        WakeLockManager.acquirePartialWakeLock(this);

        setContentView(R.layout.notification);

        photoImageView = (ImageView)findViewById(R.id.sender_photo);
        fromTextView = (TextView)findViewById(R.id.from);
        subjectTextView = (TextView)findViewById(R.id.subject);
        okButton = (Button)findViewById(R.id.ok_button);

        okButton.setOnClickListener(this);

        Intent intent = getIntent();
        message = (EmailMessage)intent.getSerializableExtra(EMAIL_MESSAGE_EXTRA);
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

        setTitle(R.string.notification_title);

        new Thread(this).start();

        WakeLockManager.acquireFullWakeLock(this);
    }//onCreate

    @Override
    public void onClick(View view) {
        if (view==okButton) {
            close();
        }
    }//onClick

    private void close() {
        KeyguardManager.reenableKeyguard();
        WakeLockManager.releaseAllWakeLocks();
        finish();
    }//close

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
            close();
        }
        catch (InterruptedException e) {
            Log.e(EmailPopup.LOG_TAG, null, e);
        }
    }//run

    Bitmap contactPhoto;
    
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
