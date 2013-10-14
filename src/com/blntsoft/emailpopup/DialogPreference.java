package com.blntsoft.emailpopup;

import android.content.Context;
import android.util.AttributeSet;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@blntsoft.com)
 */
public class DialogPreference
    extends android.preference.DialogPreference {

    public DialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
}//DialogPreference
