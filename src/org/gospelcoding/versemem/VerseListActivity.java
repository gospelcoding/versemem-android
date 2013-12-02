package org.gospelcoding.versemem;

import java.util.List;

import org.gospelcoding.versemem.R;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;


public class VerseListActivity extends ListActivity {
	public static final String TAG = "VerseListActivity";
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		//setContentView(R.layout.activity_list);
		//tempCode();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		showList();
	}
	
	public void getNewVerse(){
		Intent intent = new Intent(this, NewVerseActivity.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.action_quiz:
			startQuiz();
			return true;
		case R.id.action_new_verse:
			getNewVerse();
			return true;
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);	
		}
	}
	
	private void showList(){
		DbHelper dbhelper = new DbHelper(this);
		List<Verse> verses = dbhelper.getAllVerses();
		ArrayAdapter<Verse> adapter = new ArrayAdapter<Verse>(this, android.R.layout.simple_list_item_1, verses);
		setListAdapter(adapter);	
		if(verses.size() == 0){
			getNewVerse();
		}
	}

	private void startQuiz(){
		Intent intent = new Intent(this, QuizActivity.class);
		startActivity(intent);
	}

}
