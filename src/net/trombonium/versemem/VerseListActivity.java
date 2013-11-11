package net.trombonium.versemem;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;


public class VerseListActivity extends ListActivity {
	public static final String TAG = "VerseListActivity";
	
	private void showList(){
		DbHelper dbhelper = new DbHelper(this);
		//Cursor vCursor = dbhelper.getVersesCursor();
		
//		ListAdapter adapter = new SimpleCursorAdapter(
//				this,
//				android.R.layout.two_line_list_item,
//				vCursor,
//				new String[] {"reference", "status"},
//				new int[] {android.R.id.text1, android.R.id.text2});
//		setListAdapter(adapter);
		List<Verse> verses = dbhelper.getAllVerses();
		ArrayAdapter<Verse> adapter = new ArrayAdapter<Verse>(this, android.R.layout.simple_list_item_1, verses);
		setListAdapter(adapter);		
	}
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_list);
		
		showList();
		//tempCode();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		showList();
	}

	private void startQuiz(){
		Intent intent = new Intent(this, QuizActivity.class);
		startActivity(intent);
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
		default:
			return super.onOptionsItemSelected(item);	
		}
	}
	
	private void tempCode(){
		DbHelper dbhelper = new DbHelper(this);
		Verse v = new Verse("John 11:35", "Jesus wept.");
		v.insertVerse(dbhelper);
	}

}
