package org.gospelcoding.versemem;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.gospelcoding.versemem.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class NewVerseActivity extends Activity implements OnItemSelectedListener{

	private Book currentBook;
	private int currentChapter;
	private int currentVerse;
	private String verseBody;
	private DbHelper dbhelper;
	
	private class DownloadVerseTask extends AsyncTask<URL, Void, String> {
		@Override
		protected String doInBackground(URL... urls) {
			URL url = urls[0];
			HttpURLConnection conn;
			try {
				conn = (HttpURLConnection) url.openConnection();
				InputStream in = new BufferedInputStream(conn.getInputStream());
				verseBody = new Scanner(in, "UTF-8").useDelimiter("\\A").next();
				return verseBody;
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
		}
		
		protected void onPostExecute(String body){
			TextView previewVerse = (TextView) findViewById(R.id.text_preview_verse);
			if(body.equals("")){
				//error
				previewVerse.setText("Sorry, that verse could not be found. (Do you have an internet connection?)");
			}
			else{
				previewVerse.setText(body);
				Button addVerseButton = (Button) findViewById(R.id.button_add_verse);
				addVerseButton.setVisibility(View.VISIBLE);
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		setContentView(R.layout.activity_new_verse);

		dbhelper = new DbHelper(this);
		List<Book> books = dbhelper.getAllBooks(1);  /* TODO - make this translation id come from somewhere */
		ArrayAdapter<Book> adapter = new ArrayAdapter<Book>(this, android.R.layout.simple_spinner_item, books);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner bookSpinner = (Spinner) findViewById(R.id.books_spinner);
		bookSpinner.setOnItemSelectedListener(this);
		bookSpinner.setAdapter(adapter);
		
		Spinner chapterSpinner = (Spinner) findViewById(R.id.chapter_spinner);
		chapterSpinner.setOnItemSelectedListener(this);
		Spinner verseSpinner = (Spinner) findViewById(R.id.verse_spinner);
		verseSpinner.setOnItemSelectedListener(this);
	}
	
	public void addNewVerse(View v){
		//runs when add verse button is clicked
		String reference = currentBook.getName() + " " + currentChapter + ":" + currentVerse; //TODO this is not acceptable
		Verse newVerse = new Verse(reference, verseBody);
		newVerse.insertVerse(dbhelper);
		finish();
	}
	
	private String newVerseUrlOld(){
		String urlString = "http://gospelcoding.org/bible/";
		urlString += getTranslation() + "/";
		urlString += currentBook.getWebName() + "-" + currentChapter + "-" + currentVerse;
		return urlString;
	}
	
	private String newVerseUrl(){
		String urlString = "http://gospelcoding.org/bible/?";
		urlString += "translation=1"; //TODO fix this!
		urlString += "&book=" + currentBook.getNumber();
		urlString += "&chapter1=" + currentChapter;
		urlString += "&chapter2=" + currentChapter;
		urlString += "&verse1=" + currentVerse;
		urlString += "&verse2=" + currentVerse;
		return urlString;
	}
	
	public void getNewVerse(View v){
		//runs when new verse button is clicked
		String urlString = newVerseUrl();
		try {
			URL url = new URL(urlString);
			new DownloadVerseTask().execute(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getTranslation(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String translation = prefs.getString(SettingsActivity.PREF_TRANSLATION, SettingsActivity.DEFAULT_TRANSLATION);
		return translation;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_verse, menu);
		return true;
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
		switch(((View) view.getParent()).getId()){
		case R.id.books_spinner:
			currentBook = (Book) parent.getItemAtPosition(pos);
			updateChapterSpinner();
			break;
		case R.id.chapter_spinner:
			currentChapter = (Integer) parent.getItemAtPosition(pos);
			updateVerseSpinner();
			break;
		case R.id.verse_spinner:
			currentVerse = (Integer) parent.getItemAtPosition(pos);
		}
		//Log.e("NewVerseActivity", "View Id: "+((View) view.getParent()).getId() + "\nSpinner Id: "+R.id.books_spinner);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);	
		}
	}
	
	public void onNothingSelected(AdapterView<?> parent){
		//do nothing?
	}
	
	private void updateChapterSpinner(){
		int bookId = currentBook.getId();
		int numberOfChapters = dbhelper.getNumberOfChapters(bookId);
		List<Integer> chapterList = new ArrayList<Integer>();
		for(int i=1; i<=numberOfChapters; ++i){
			chapterList.add(i);
		}
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, chapterList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner chapterSpinner = (Spinner) findViewById(R.id.chapter_spinner);
		chapterSpinner.setAdapter(adapter);
		//Log.e("Spinners", "Updating chapter spinner for book id "+bookId);
	}
	
	private void updateVerseSpinner(){
		int bookId = currentBook.getId();
		int numberOfVerses = dbhelper.getNumberOfVerses(bookId, currentChapter);
		List<Integer> verseList = new ArrayList<Integer>();
		for(int i=1; i<=numberOfVerses; ++i){
			verseList.add(i);
		}
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, verseList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner chapterSpinner = (Spinner) findViewById(R.id.verse_spinner);
		chapterSpinner.setAdapter(adapter);
		//Log.e("Spinners", "Updating verse spinner for book id " + bookId + " chapter "+currentChapter);
	}
}
