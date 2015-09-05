package org.victor.tankcontroller;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity {

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener
            = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null
                );

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), "")
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        addPreferencesFromResource(R.menu.settings);
        setup("ip", "192.168.150.1", new IpChangeListener());
        setup("port", "8888", new PortChangeListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return false;
    }

    private void setup(String key,
                       String defaultValue,
                       Preference.OnPreferenceChangeListener listener) {
        EditTextPreference preference = (EditTextPreference) findPreference(key);
        if (isBlank(preference.getText())) {
            preference.setText(defaultValue);
        }
        preference.setSummary(preference.getText());
        preference.setOnPreferenceChangeListener(listener);
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().equals("");
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private boolean updateSummaryIfValid(Preference preference, Object newValue, Validator validator) {
        if (validator.isValid(newValue)) {
            preference.setSummary(newValue.toString());
            return true;
        }
        return false;
    }

    // Inner classes

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //addPreferencesFromResource(R.xml.pref_notification);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // addPreferencesFromResource(R.xml.pref_data_sync);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }
    }

    private class IpChangeListener implements Preference.OnPreferenceChangeListener {

        public static final int IP_LENGTH = 4;
        public static final int MAX_VALUE_OF_IP_PART = 255;

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return updateSummaryIfValid(preference, newValue, new Validator() {
                @Override
                public boolean isValid(Object o) {
                    if (!(o instanceof String)) {
                        return false;
                    }
                    String s = (String) o;
                    String[] parts = s.split("\\.");
                    if (parts.length != IP_LENGTH) {
                        return false;
                    }
                    for (String part : parts) {
                        try {
                            int iPart = Integer.parseInt(part);
                            if (iPart < 0 || iPart > MAX_VALUE_OF_IP_PART) {
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                    return true;
                }
            });
        }
    }

    private class PortChangeListener implements Preference.OnPreferenceChangeListener {

        public static final int MIN_PORT = 1;
        public static final int MAX_PORT = 65535;

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return updateSummaryIfValid(preference, newValue, new Validator() {
                @Override
                public boolean isValid(Object o) {
                    if (!(o instanceof String)) {
                        return false;
                    }
                    String s = (String) o;
                    try {
                        int i = Integer.parseInt(s);
                        return i >= MIN_PORT && i <= MAX_PORT;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            });
        }
    }
}
