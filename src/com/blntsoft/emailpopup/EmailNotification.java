package com.blntsoft.emailpopup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.Toast;
import com.blntsoft.utils.OnSwipeTouchListener;
import com.crashlytics.android.Crashlytics;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@nguyentrong.com)
 */
public class EmailNotification 
    extends Activity
    implements Runnable, OnClickListener {

    private static final String VIEW_URI_PREFIX = "email://messages/";
    private static final String DELETE_URI_PREFIX = "content://com.fsck.k9.messageprovider/delete_message/";

    private static final int MAX_SUBJECT_LENGTH = 140;

    private EmailMessage message;
    private boolean isDestroyed;
    
    private ImageView photoImageView;
    private TextView fromNameTextView;
    private TextView fromEmailTextView;
    private TextView subjectTextView;

    private Button viewButton;
    private Button deleteButton;
    private Button closeButton;

    private OnSwipeTouchListener onSwipeTouchListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Crashlytics.start(this);

        Log.v(EmailPopup.LOG_TAG, "EmailNotification.onCreate()");

        super.onCreate(savedInstanceState);

        Window w = getWindow();
        w.requestFeature(Window.FEATURE_LEFT_ICON);

        setContentView(R.layout.notification);
        
        w.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);

        findViewById(R.id.notification_area).setOnClickListener(this);

        photoImageView = (ImageView)findViewById(R.id.sender_photo);
        fromNameTextView = (TextView)findViewById(R.id.from_name);

        fromEmailTextView = (TextView)findViewById(R.id.from_email);
        subjectTextView = (TextView)findViewById(R.id.subject);

        viewButton = (Button)findViewById(R.id.view_button);
        viewButton.setOnClickListener(this);

        deleteButton = (Button)findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(this);

        closeButton = (Button)findViewById(R.id.close_button);
        closeButton.setOnClickListener(this);

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

        View notificationArea = findViewById(R.id.notification);
        notificationArea.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
                Log.d(EmailPopup.LOG_TAG, "onSwipeRight");
                EmailNotification.this.onClick(closeButton);
            }

            @Override
            public void onSwipeLeft() {
                Log.d(EmailPopup.LOG_TAG, "onSwipeLeft");
                EmailNotification.this.onClick(closeButton);
            }

            @Override
            public void onSwipeTop() {
                Log.d(EmailPopup.LOG_TAG, "onSwipeTop");
            }

            @Override
            public void onSwipeBottom() {
                Log.d(EmailPopup.LOG_TAG, "onSwipeBottom");
            }
        });

        isDestroyed = false;
        if (message.autoClose) {
            new Thread(this).start();
        }
    }//onCreate

    @Override
    public void onClick(View view) {
        if (view==closeButton) {
        }
        else if (view==deleteButton) {
            Uri viewEmailUri = getIntent().getData();
            String viewEmailUriStr = viewEmailUri.toString();
            String delEmailUriStr = DELETE_URI_PREFIX + viewEmailUriStr.substring(VIEW_URI_PREFIX.length());
            new DeleteEmailTask().execute(delEmailUriStr);
        }
        else {
            KeyguardManager.release();

            Intent intent = new Intent(Intent.ACTION_VIEW, getIntent().getData());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }
        
        finish();
    }//onClick

    @Override
    public void onDestroy() {
        Log.e(EmailPopup.LOG_TAG, "EmailNotification.onDestroy()");
        isDestroyed = true;
        super.onDestroy();
    }

    @Override
    public void run() {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EmailNotification.this);
            int displayTime = Integer.parseInt(preferences.getString(Preferences.TIME_DISPLAY_PREF_KEY, getString(R.string.time_display_preference_default)));
            Thread.sleep((int)(displayTime * 1000 * 1.1));
            if (!isDestroyed) {
                finish();
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
            int defaultPhotoResId;
            if ((System.currentTimeMillis() % 2)==0) {
                defaultPhotoResId = R.drawable.ic_contact_picture;
            }
            else {
                defaultPhotoResId = R.drawable.ic_contact_picture_2;
            }
            Bitmap photo = ContactUtils.getContactPhotoById(EmailNotification.this, message.contactId, defaultPhotoResId);
            if (photo == null) {
                photo = BitmapFactory.decodeResource(EmailNotification.this.getResources(), defaultPhotoResId);
            }
            int photoWidth = (int)getResources().getDimension(R.dimen.sender_photo_width);
            int photoHeight = (int)getResources().getDimension(R.dimen.sender_photo_height);
            photo = Bitmap.createScaledBitmap(photo, photoWidth, photoHeight, false);
            return photo;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Log.v(EmailPopup.LOG_TAG, "Done loading contact photo");
            if (result != null) {
                photoImageView.setImageBitmap(result);
            }
        }
    }//FetchContactPhotoTask

    private class DeleteEmailTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String delUriStr = params[0];
            Log.v(EmailPopup.LOG_TAG, delUriStr);

            Uri delUri = Uri.parse(delUriStr);
            getContentResolver().delete(delUri , null, null);
            return null;
        }
    }
}//EmailNotification
