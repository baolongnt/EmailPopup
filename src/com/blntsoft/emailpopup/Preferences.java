package com.blntsoft.emailpopup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@nguyentrong.com)
 */
public class Preferences
    extends PreferenceActivity
    implements OnSharedPreferenceChangeListener {

    public static final String ON_OFF_SWITCH_PREF_KEY      = "onOffSwitch";
    public static final String CONTACT_FILTERING_PREF_KEY  = "contactFiltering";

    public static final String ALL_FILTERING_PREF_VALUE         = "All";
    public static final String CONTACT_FILTERING_PREF_VALUE     = "ContactOnly";
    public static final String PHOTO_FILTERING_PREF_VALUE       = "ContactWithPhotoOnly";

    private final static String[][] VALUE_SUMMARY_ID_MAP = new String[][] {
        { ALL_FILTERING_PREF_VALUE, String.valueOf(R.string.all_filtering_preference) },
        { CONTACT_FILTERING_PREF_VALUE, String.valueOf(R.string.contact_only_filtering_preference) },
        { PHOTO_FILTERING_PREF_VALUE, String.valueOf(R.string.contact_with_photo_only_filtering_preference) }
    };

    private CheckBoxPreference onOffSwitchPreference;
    private ListPreference contactFilteringPreference;
    private Menu optionsMenu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        onOffSwitchPreference = (CheckBoxPreference)getPreferenceScreen().findPreference(ON_OFF_SWITCH_PREF_KEY);
        contactFilteringPreference = (ListPreference)getPreferenceScreen().findPreference(CONTACT_FILTERING_PREF_KEY);
    }//onCreate
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Let's do something a preference value changes
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.preference_options, menu);
        optionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.test_menu:
                Intent intent = new Intent(this, Main.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}//Preferences

