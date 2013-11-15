package com.google.android.apps.mytracks.settings;

import com.google.android.apps.mytracks.util.PreferencesUtils;
import com.google.android.apps.mytracks.util.UnitConversions;
import com.google.android.maps.mytracks.R;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class PaceSettingsActivity extends AbstractSettingsActivity {
  private static final String TAG = PaceSettingsActivity.class.getSimpleName();
  private EditTextPreference targetPacePreference;
  
  
  @SuppressWarnings("deprecation")
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    addPreferencesFromResource(R.xml.pace_settings);

    targetPacePreference = (EditTextPreference) findPreference(
        getString(R.string.settings_target_pace_key));
  
    
    
    configTargetPacePreference(targetPacePreference, R.string.settings_target_pace_key,
        PreferencesUtils.PACE_KEEPER_REMINDER_FREQUENCY_DEFAULT);

    updateTargetPaceSummary(targetPacePreference, R.string.settings_target_pace_key,
        PreferencesUtils.PACE_KEEPER_REMINDER_FREQUENCY_DEFAULT);
  }



  private void updateTargetPaceSummary(Preference preference,
      int key, int defaultValue) {
    boolean metricUnits = PreferencesUtils.isMetricUnits(this);
    int displayValue = getTargetPaceValue(key, defaultValue);
    preference.setSummary(getString(
        metricUnits ? R.string.value_integer_kilometer_hour : R.string.value_integer_mile_hour,
        displayValue));
    
  }
  
  private int getTargetPaceValue(int key, int defaultValue) {
    int value = PreferencesUtils.getInt(this, key, defaultValue);
    if (!PreferencesUtils.isMetricUnits(this)) {
      value = (int) (value * UnitConversions.KM_TO_MI);
    }
    return value;
  }


  private void configTargetPacePreference(EditTextPreference preference,
      final int key, final int defaultValue) {
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
  private void storeTargetPace(int key, int defaultValue, String val) {
      int value;
      try {
        value = Integer.parseInt(val);
        if (!PreferencesUtils.isMetricUnits(this)) {
          value = (int) (value * UnitConversions.MI_TO_KM);
        }
      } catch (NumberFormatException e) {
        Log.e(TAG, "invalid value " + val);
        value = defaultValue;
      }

      PreferencesUtils.setInt(this, key, value);
    
  }

}
