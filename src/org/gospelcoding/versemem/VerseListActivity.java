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
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;


public class VerseListActivity extends ListActivity {
	public static final String TAG = "VerseListActivity";
	
	private VerseListArrayAdapter adapter;
			
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
		
		DbHelper dbhelper = new DbHelper(this);
		List<Verse> verses = dbhelper.getAllVerses();
		if(verses.size()==0) getNewVerse();
		adapter = new VerseListArrayAdapter(this, verses);
		setListAdapter(adapter);
	}
	
//	public void cancelEdit(View v){
//		adapter.cancelEdit();
//	}
	
	private void deleteVerse(){
		adapter.deleteSelectedItem();
	}
	
	public void deleteVerseButton(View v){
		//Display "Are you sure?" dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.delete_dialog).setTitle(adapter.getSelectedVerse().getReference());
		builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id){
				deleteVerse();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				//cancel deletion - do nothing
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public void editVerse(String newBody){
		adapter.saveEdit(newBody);
	}
	
	public void editVerseButton(View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(adapter.getSelectedVerse().getReference());
		final EditText editVerseBody = new EditText(this);
		editVerseBody.setText(adapter.getSelectedVerse().getBody());
		builder.setView(editVerseBody);
		builder.setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id){
				editVerse(editVerseBody.getText().toString());
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				//cancel edit - do nothing
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}
	
	public void getNewVerse(){
		Intent intent = new Intent(this, NewVerseActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
//		EditText editVerseBody = (EditText) findViewById(R.id.edit_text_verse_body);
//		adapter.prepareOrientationChange(editVerseBody);
		super.onConfigurationChanged(newConfig);
		adapter.orientationChanged();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onListItemClick(ListView listview, View v, int position, long id){
		adapter.setSelectedItem(position);
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
	
	public void saveVerse(View v){
		String newBody = ((EditText) findViewById(R.id.edit_text_verse_body)).getText().toString();
		adapter.saveEdit(newBody);
	}

	private void startQuiz(){
		Intent intent = new Intent(this, QuizActivity.class);
		startActivity(intent);
	}

}
