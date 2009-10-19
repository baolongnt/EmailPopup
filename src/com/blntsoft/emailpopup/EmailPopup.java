package com.blntsoft.emailpopup;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@nguyentrong.com)
 */
public class EmailPopup {

    public static final String LOG_TAG                  = "EmailPopup";

    public static final String ACTION_NOTIFICATION_VIEW = "com.blntsoft.emailpopup.intent.action.VIEW";
    public static final String ACTION_EMAIL_RECEIVED    = "com.android.email.intent.action.EMAIL_RECEIVED";

    public static final String EXTRA_ACCOUNT    = "com.android.email.intent.extra.ACCOUNT";
    public static final String EXTRA_FOLDER     = "com.android.email.intent.extra.FOLDER";
    public static final String EXTRA_SENT_DATE  = "com.android.email.intent.extra.SENT_DATE";
    public static final String EXTRA_FROM       = "com.android.email.intent.extra.FROM";
    public static final String EXTRA_TO         = "com.android.email.intent.extra.TO";
    public static final String EXTRA_CC         = "com.android.email.intent.extra.CC";
    public static final String EXTRA_BCC        = "com.android.email.intent.extra.BCC";
    public static final String EXTRA_SUBJECT    = "com.android.email.intent.extra.SUBJECT";

    public static final String EMAIL_MESSAGE_EXTRA             = "EMAIL_MESSAGE_EXTRA";

}//EmailPopup
