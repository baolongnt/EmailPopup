package com.blntsoft.emailpopup;

import android.test.ActivityInstrumentationTestCase;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.blntsoft.emailpopup.MainTest \
 * com.blntsoft.emailpopup.tests/android.test.InstrumentationTestRunner
 */
public class MainTest extends ActivityInstrumentationTestCase<Main> {

    public MainTest() {
        super("com.blntsoft.emailpopup", Main.class);
    }

}
