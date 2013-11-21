package org.gospelcoding.versemem;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.view.Menu;

public class SettingsActivity extends PreferenceActivity 
								implements OnSharedPreferenceChangeListener{
	public static final String PREF_NOTIFICATION_NUMBER = "pref_notification_number";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
		int numberOfNotifications = Integer.parseInt(prefs.getString(PREF_NOTIFICATION_NUMBER, "2"));
		for(int i=0; i<numberOfNotifications; ++i){
			addTimePreference();
		}
	}
	
	public void addTimePreference(){
		TimePreference timePref = new TimePreference(this, null);
		timePref.setOrder(2);

		@SuppressWarnings("deprecation")
		PreferenceScreen prefScreen = getPreferenceScreen();
		prefScreen.addPreference(timePref);
		
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if(key.equals(PREF_NOTIFICATION_NUMBER)){
			
		}
		
	}
}
