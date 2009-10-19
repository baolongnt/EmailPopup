package com.blntsoft.emailpopup;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@nguyentrong.com)
 */
public class EmailMessage
    implements Serializable {

    String account;
    String uriString;
    String subject;
    String senderName;
    String senderEmail;
    long contactId;

}//EmailMessage
