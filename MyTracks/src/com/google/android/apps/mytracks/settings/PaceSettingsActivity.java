package com.google.android.apps.mytracks.settings;

import com.google.android.apps.mytracks.IntegerListPreference;
import com.google.android.apps.mytracks.util.PreferencesUtils;
import com.google.android.apps.mytracks.util.UnitConversions;
import com.google.android.maps.mytracks.R;
import com.google.common.annotations.VisibleForTesting;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class PaceSettingsActivity extends AbstractSettingsActivity {
  private static final String TAG = PaceSettingsActivity.class.getSimpleName();
  private EditTextPreference targetPacePreference;
  private IntegerListPreference paceWarningFrequencyPreference; 
  
  
  @SuppressWarnings("deprecation")
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    addPreferencesFromResource(R.xml.pace_settings);

    targetPacePreference = (EditTextPreference) findPreference(
        getString(R.string.settings_target_pace_key));
    
    paceWarningFrequencyPreference = (IntegerListPreference) findPreference(
        getString(R.string.settings_target_pace_reminder_frequency_key));
    
    configTargetPacePreference(targetPacePreference, R.string.settings_target_pace_key,
        PreferencesUtils.PACE_KEEPER_PACE_DEFAULT);

    updateTargetPaceSummary(targetPacePreference, R.string.settings_target_pace_key,
        PreferencesUtils.PACE_KEEPER_PACE_DEFAULT);
    
    configPaceWarningFrequencyPreference(paceWarningFrequencyPreference, 
        R.string.settings_target_pace_reminder_frequency_key,
        PreferencesUtils.PACE_KEEPER_REMINDER_FREQUENCY_DEFAULT);
    
  }


  private void configPaceWarningFrequencyPreference(
      IntegerListPreference preference, int key, int defaultValue) {
    int value = PreferencesUtils.getInt(this, key, defaultValue);
    String[] values = getResources().getStringArray(R.array.pace_keeper_frequency_values);
    String[] options = new String[values.length];
    String[] summary = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      int val = Integer.parseInt(values[i]);
      options[i] = getString(R.string.value_integer_second, val);
      summary[i] = getString(R.string.value_integer_second, val);
    }
    
    configureListPreference(preference, summary, options, values, String.valueOf(value), null);    
  }
  
  private void configTargetPacePreference(EditTextPreference preference,
      final int key, final String defaultValue) {
    preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
      
      @Override
      public boolean onPreferenceChange(Preference pref, Object newValue) {
        String val = (String) newValue;
        storeTargetPace(key,defaultValue,val);
        updateTargetPaceSummary(pref, key, defaultValue);
        return false;
      }
    });
  }
  
  private void updateTargetPaceSummary(Preference preference,
      int key, String defaultValue) {
    boolean metricUnits = PreferencesUtils.isMetricUnits(this);
    double displayValue = getTargetPaceForSummary(key, defaultValue);
    preference.setSummary(getString(
        metricUnits ? R.string.value_double_minutes_kilometer : R.string.value_double_minutes_miles,
        displayValue));
    
  }
  
  @VisibleForTesting
  private double getTargetPaceForSummary(int key,String defaultValue) {
    Double value = Double.parseDouble((PreferencesUtils.getString(this, key, defaultValue)));
    // Convert back from m/s to minutes per km/mile
    value = 1/(value * 3.6 / 60);
    if (!PreferencesUtils.isMetricUnits(this)) {
      value *= UnitConversions.KM_TO_MI;  
    }
    return value;
  }

  @VisibleForTesting
  private void storeTargetPace(int key, String defaultValue, String val) {
      double value;
      try {
        value = Double.parseDouble(val);
        
        // Input is in "minutes per kilometer (or mile)"
        // we derive meter per second as follows:
        // 1/speed * 60[to kmh/mph] / 3.6 [to m/s]
        value = (60/value)/3.6;  
        
        if (!PreferencesUtils.isMetricUnits(this)) {
          value /= UnitConversions.MI_TO_KM;
        }
        
      } catch (NumberFormatException e) {
        Log.w(TAG, "Could not parse target pace of val " + val + ", setting poor value");
        value = Double.parseDouble(defaultValue);
      }

      PreferencesUtils.setString(this, key,""+value);
  }
  
}
