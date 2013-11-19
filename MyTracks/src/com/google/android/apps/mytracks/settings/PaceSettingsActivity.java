package com.google.android.apps.mytracks.settings;

import com.google.android.apps.mytracks.util.PreferencesUtils;
import com.google.android.apps.mytracks.util.UnitConversions;
import com.google.android.maps.mytracks.R;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class PaceSettingsActivity extends AbstractSettingsActivity {
  private static final String TAG = PaceSettingsActivity.class.getSimpleName();
  private EditTextPreference targetPacePreference;
  private CheckBoxPreference usePaceSystemPreference; 
  
  
  @SuppressWarnings("deprecation")
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    addPreferencesFromResource(R.xml.pace_settings);

    targetPacePreference = (EditTextPreference) findPreference(
        getString(R.string.settings_target_pace_key));
    usePaceSystemPreference = (CheckBoxPreference) findPreference(
        getString(R.string.settings_use_pace_system_key));
  
   // configUsePaceSystem(usePaceSystemPreference, R.string.settings_use_pace_system_key,
    //    PreferencesUtils.PACE_KEEPER_USE_PACE_SYSTEM_DEFAULT);
    
    
    configTargetPacePreference(targetPacePreference, R.string.settings_target_pace_key,
        PreferencesUtils.PACE_KEEPER_PACE_DEFAULT);

    updateTargetPaceSummary(targetPacePreference, R.string.settings_target_pace_key,
        PreferencesUtils.PACE_KEEPER_PACE_DEFAULT);
    
    
  }

/*
  private void configUsePaceSystem(final Preference preference, final int key, final boolean defaultValue){
    preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
          
          @Override
          public boolean onPreferenceChange(Preference pref, Object newValue) {
            boolean val = newValue instanceof Boolean ? (Boolean) newValue : defaultValue;
            storeUsePaceSystem(key, val);
            return false;
          }
                
        });
  }
  
  private void storeUsePaceSystem(int key, boolean val) {
   PreferencesUtils.setBoolean(this, key, val);
  }
*/
  private void updateTargetPaceSummary(Preference preference,
      int key, String defaultValue) {
    boolean metricUnits = PreferencesUtils.isMetricUnits(this);
    double displayValue = getTargetPaceValue(key, defaultValue);
    preference.setSummary(getString(
        metricUnits ? R.string.value_double_minutes_kilometer : R.string.value_double_minutes_miles,
        displayValue));
    
  }
  
  private double getTargetPaceValue(int key,String defaultValue) {
    Double value = Double.parseDouble((PreferencesUtils.getString(this, key, defaultValue)));
    if (!PreferencesUtils.isMetricUnits(this)) {
      value = (value * UnitConversions.KM_TO_MI); // TODO exccise ? we're not dealing with 
    }
    return value;
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
  private void storeTargetPace(int key, String defaultValue, String val) {
      double value;
      try {
        value = Double.parseDouble(val);
        if (!PreferencesUtils.isMetricUnits(this)) {
          value = (double) (value * UnitConversions.MI_TO_KM);
        }
      } catch (NumberFormatException e) {
        Log.e(TAG, "invalid value " + val);
        value = Double.parseDouble(defaultValue);
      }

      PreferencesUtils.setString(this, key,""+value);
    
  }

}
