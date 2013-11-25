package org.gospelcoding.versemem;

import org.joda.time.DateTime;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import android.view.Menu;
import android.widget.BaseAdapter;

public class SettingsActivity extends PreferenceActivity 
								implements OnSharedPreferenceChangeListener{
	public static final int NOTIFICATION_TIME_ORDER = 2;
	public static final String PREF_NOTIFICATION_NUMBER = "pref_notification_number";
	public static final String PREF_NOTIFICATION_TIME = "pref_notification_time_";
	public static final String PREF_NOTIFICATION_VIBRATE = "pref_notification_vibrate";
	public static final String PREF_NOTIFICATION_LED = "pref_notification_led";
	public static final String PREF_TRANSLATION = "pref_translation";
	public static final String DEFAULT_TRANSLATION = "kjv";
	public static final String PREF_QUIZ_STYLE = "pref_quiz_style";
	public static final String KEYBOARD_AUTO = "Keyboard Auto-Check";
	public static final String KEYBOARD_SELF= "Keyboard Self-Check";
	public static final String MICROPHONE = "Microphone Self-Check";
	public static final String NO_INPUT = "No Input";
	public static final String DEFAULT_QUIZ_STYLE = KEYBOARD_AUTO; 
	private int oldNumberOfNotifications;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
		prefs.registerOnSharedPreferenceChangeListener(this);
		int numberOfNotifications = Integer.parseInt(prefs.getString(PREF_NOTIFICATION_NUMBER, "2"));
		for(int i=0; i<numberOfNotifications; ++i){
			addTimePreference(i);
		}
		oldNumberOfNotifications = numberOfNotifications;
	}

	@SuppressWarnings("deprecation")
	public void addTimePreference(int index){
		TimePreference timePref = new TimePreference(this, null);
		timePref.setOrder(NOTIFICATION_TIME_ORDER);
		timePref.setKey(PREF_NOTIFICATION_TIME + index);
		timePref.setDefaultValue("12:00");
		SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
		String title = prefs.getString(PREF_NOTIFICATION_TIME + index, "12:00");
		timePref.setTitle(printableTime(title));

		PreferenceScreen prefScreen = getPreferenceScreen();
		prefScreen.addPreference(timePref);
		
	}
	
	public void dropTimePreference(){
		PreferenceScreen prefScreen = getPreferenceScreen();
		prefScreen.removePreference(prefScreen.getPreference(NOTIFICATION_TIME_ORDER));
	}

	@SuppressWarnings("deprecation")
	public void changeNotificationNumber(){
		SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
		int numberOfNotifications = Integer.parseInt(prefs.getString(PREF_NOTIFICATION_NUMBER, "2"));
		int changeInNumberOfNotifications = numberOfNotifications - oldNumberOfNotifications;
		if(changeInNumberOfNotifications > 0){
			for(int i=0; i<changeInNumberOfNotifications; ++i){
				addTimePreference(oldNumberOfNotifications + i);
			}
		}
		else if(changeInNumberOfNotifications < 0){
			changeInNumberOfNotifications *= -1;
			for(int i=0; i<changeInNumberOfNotifications; ++i){
				dropTimePreference();
			}
		}
		oldNumberOfNotifications = numberOfNotifications;
	}

	public static String printableTime(String time){
		boolean pm = false;
		int h = TimePreference.getHour(time);
		int m = TimePreference.getMinute(time);
		if(h > 12){
			pm = true;
			h += -12;
		}
		else if(h == 12){
			pm = true;
		}
		else if(h == 0){
			h += 12;
		}
		String pTime = h + ":";
		if(m < 10) pTime += "0";
		pTime += m;
		if(pm) pTime += "pm";
		return pTime;
		
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if(key.equals(PREF_NOTIFICATION_NUMBER)){
			changeNotificationNumber();
			QuizMaster.setNextAlarm(this);
		}
		else if(key.substring(0, key.length()-1).equals(PREF_NOTIFICATION_TIME)){
			PreferenceScreen prefScreen = getPreferenceScreen();
			BaseAdapter prefScreenListAdapter = (BaseAdapter) prefScreen.getRootAdapter();
			Preference pref = getPreferenceManager().findPreference(key);
			String alarmTime = prefs.getString(key, "12:00");
			pref.setTitle(printableTime(alarmTime));
			prefScreenListAdapter.notifyDataSetChanged();
			
			QuizMaster.setNextAlarm(this);
		}
		
	}
}
