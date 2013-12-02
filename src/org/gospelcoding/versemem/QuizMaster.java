package org.gospelcoding.versemem;

import org.joda.time.DateTime;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.BaseAdapter;

public abstract class QuizMaster {
	
	public static DateTime getNextAlarm(SharedPreferences prefs){
		int numAlarms = Integer.parseInt(prefs.getString(SettingsActivity.PREF_NOTIFICATION_NUMBER, "1"));
		DateTime now = DateTime.now();
		DateTime nextAlarm = now.plusDays(2); //well in the future
		
		for(int i=0; i<numAlarms; ++i){
			String alarmTime = prefs.getString(SettingsActivity.PREF_NOTIFICATION_TIME+i, "12:00");
			DateTime possAlarm = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 
					TimePreference.getHour(alarmTime), TimePreference.getMinute(alarmTime));
			if(possAlarm.compareTo(now)<0){
				possAlarm = possAlarm.plusDays(1);
			}
			if(possAlarm.compareTo(nextAlarm) < 0){
				nextAlarm = possAlarm;
			}
		}	
		return nextAlarm;
	}

	public static void setNextAlarm(Context context){
		Log.e("QuizMaster", "Set Next alarm");
		AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		DateTime nextAlarm = getNextAlarm(prefs);
		Intent notifyIntent = new Intent(context, QuizNotificationService.class);
		notifyIntent.putExtra("Index", 60 * nextAlarm.getHourOfDay() + nextAlarm.getMinuteOfHour());
		PendingIntent notifyPendingIntent = PendingIntent.getService(context, 0, notifyIntent, 0);
		int alarmType = AlarmManager.RTC;
		if(prefs.getBoolean(SettingsActivity.PREF_NOTIFICATION_VIBRATE, false) || 
				prefs.getBoolean(SettingsActivity.PREF_NOTIFICATION_LED, false)){
			alarmType = AlarmManager.RTC_WAKEUP;
			Log.e("VerseMem", "Set alarmCode to RTC_WAKEUP");
		}
		mAlarmManager.cancel(notifyPendingIntent);
		mAlarmManager.set(alarmType, nextAlarm.getMillis(), notifyPendingIntent);
		Log.e("QuizMaster", "Alrm: "+nextAlarm.getHourOfDay() + ":" + nextAlarm.getMinuteOfHour());
	}
}
