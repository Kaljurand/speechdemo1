package ee.ioc.phon.android.speechdemo1;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Settings activity for the recognition service.
 * This is shown by the framework in the "Assist & voice input" menu.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            Resources res = getResources();
            setSummary(sp, res, R.string.key_uri);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference pref = findPreference(key);
            if (pref instanceof EditTextPreference) {
                EditTextPreference etp = (EditTextPreference) pref;
                pref.setSummary(etp.getText());
            }
        }

        private void setSummary(SharedPreferences prefs, Resources res, int key) {
            Preference pref = findPreference(getString(key));
            if (pref != null) {
                pref.setSummary(getPrefString(prefs, res, key));
            }
        }
    }

    private static String getPrefString(SharedPreferences prefs, Resources res, int key) {
        return prefs.getString(res.getString(key), null);
    }
}