package com.blntsoft.emailpopup;

import java.util.ArrayList;
import java.util.Vector;

public class EmailMessageQueue
    extends ArrayList<EmailMessage> {

    private static final EmailMessageQueue instance = new EmailMessageQueue();

    private EmailMessageQueue() {
    }//EmailMessageQueue

    public static EmailMessageQueue getInstance() {
        return instance;
    }//getInstance

}//EmailMessageQueue



