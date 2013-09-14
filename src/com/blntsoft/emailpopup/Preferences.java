package com.blntsoft.emailpopup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.crashlytics.android.Crashlytics;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@nguyentrong.com)
 */
public class Preferences
    extends PreferenceActivity
    implements OnSharedPreferenceChangeListener {

    public static final String ON_OFF_SWITCH_PREF_KEY      = "onOffSwitch";
    public static final String CONTACT_FILTERING_PREF_KEY  = "contactFiltering";
    public static final String KEYGUARD_FILTERING_PREF_KEY = "keyguardFiltering";
    public static final String TIME_DISPLAY_PREF_KEY       = "timeDisplay";
    public static final String ABOUT_PREF_KEY              = "about";

    public static final String ALL_FILTERING_PREF_VALUE                     = "All";
    public static final String CONTACTS_FILTERING_PREF_VALUE                = "ContactsOnly";
    public static final String STARRED_CONTACT_FILTERING_PREF_VALUE         = "StarredContactsOnly";

    private final static String[][] VALUE_SUMMARY_ID_MAP = new String[][] {
        { ALL_FILTERING_PREF_VALUE, String.valueOf(R.string.all_filtering_preference) },
        { CONTACTS_FILTERING_PREF_VALUE, String.valueOf(R.string.contacts_only_filtering_preference) },
        { STARRED_CONTACT_FILTERING_PREF_VALUE, String.valueOf(R.string.starred_contacts_only_filtering_preference) }
    };

    private CheckBoxPreference onOffSwitchPreference;
    private ListPreference contactFilteringPreference;
    private CheckBoxPreference keyguardPreference;
    private ListPreference timeDisplayPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        addPreferencesFromResource(R.xml.preferences);

        onOffSwitchPreference = (CheckBoxPreference)getPreferenceScreen().findPreference(ON_OFF_SWITCH_PREF_KEY);
        contactFilteringPreference = (ListPreference)getPreferenceScreen().findPreference(CONTACT_FILTERING_PREF_KEY);
        keyguardPreference = (CheckBoxPreference)getPreferenceScreen().findPreference(KEYGUARD_FILTERING_PREF_KEY);
        timeDisplayPreference = (ListPreference)getPreferenceScreen().findPreference(TIME_DISPLAY_PREF_KEY);

        String version;
        PackageManager pm = this.getPackageManager();
        try {
            version = " v" + pm.getPackageInfo(Preferences.class.getPackage().getName(), 0).versionName;
        } catch (NameNotFoundException e) {
            version = "";
        }
        DialogPreference aboutPreference = (DialogPreference)getPreferenceScreen().findPreference(ABOUT_PREF_KEY);
        aboutPreference.setDialogTitle(getString(R.string.app_name) + version);
        aboutPreference.setDialogLayoutResource(R.layout.about);
    }//onCreate
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(ON_OFF_SWITCH_PREF_KEY)) {
            onOffSwitchPreference.setSummary(
                sharedPreferences.getBoolean(key, false) ?
                    getString(R.string.on_switch_preference)
                    : getString(R.string.off_switch_preference)
            );
        }
        else if (key.equals(CONTACT_FILTERING_PREF_KEY)) {
            contactFilteringPreference.setSummary(getContactFilteringPreferenceSummaryByValue(sharedPreferences.getString(key, "")));
        }
        else if (key.equals(KEYGUARD_FILTERING_PREF_KEY)) {
            keyguardPreference.setSummary(
                sharedPreferences.getBoolean(key, false) ?
                    getString(R.string.on_keyguard_preference)
                    : getString(R.string.off_keyguard_preference)
            );
        }
        else if (key.equals(TIME_DISPLAY_PREF_KEY)) {
            timeDisplayPreference.setSummary(
                getString(R.string.time_display_preference_summary, sharedPreferences.getString(key, ""))
            );
        }
    }//onSharedPreferenceChanged

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        onOffSwitchPreference.setSummary(
            sharedPreferences.getBoolean(ON_OFF_SWITCH_PREF_KEY, false) ?
                getString(R.string.on_switch_preference)
                : getString(R.string.off_switch_preference)
        );
        contactFilteringPreference.setSummary(getContactFilteringPreferenceSummaryByValue(sharedPreferences.getString(CONTACT_FILTERING_PREF_KEY, "")));
        keyguardPreference.setSummary(
            sharedPreferences.getBoolean(KEYGUARD_FILTERING_PREF_KEY, false) ?
                getString(R.string.on_keyguard_preference)
                : getString(R.string.off_keyguard_preference)
        );
        timeDisplayPreference.setSummary(
            getString(R.string.time_display_preference_summary, sharedPreferences.getString(TIME_DISPLAY_PREF_KEY, ""))
        );

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }//onResume

    @Override
    protected void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }//onPause

    private String getContactFilteringPreferenceSummaryByValue(String value) {
        for (String[] entry : VALUE_SUMMARY_ID_MAP) {
            if (entry[0].equals(value)) {
                return getString(Integer.parseInt(entry[1]));
            }
        }
        return "";
    }//setContactFilteringPreferenceSummary

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.preference_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.test_menu:
                Intent intent = new Intent(this, Main.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    */

}//Preferences

