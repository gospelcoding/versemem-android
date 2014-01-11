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
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class NewVerseActivity extends Activity implements OnItemSelectedListener{

	private Book currentBook;
	private int currentChapter1;
	private int currentVerse1;
	private int currentChapter2;
	private int currentVerse2;
	private String verseRef;
	private String verseBody;
	private DbHelper dbhelper;
	private boolean multiverse = false;
	private int maxVerses = 6;
	
	private boolean showingVerse = false;
	
	private class DownloadVerseTask extends AsyncTask<URL, Void, String> {
		@Override
		protected String doInBackground(URL... urls) {
			URL url = urls[0];
			HttpURLConnection conn;
			try {
				conn = (HttpURLConnection) url.openConnection();
				InputStream in = new BufferedInputStream(conn.getInputStream());
				verseBody = new Scanner(in, "UTF-8").useDelimiter("\\A").next();
				setVerseRef();
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
				previewVerse.setText(Verse.printableBody(body));
				Button addVerseButton = (Button) findViewById(R.id.button_add_verse);
				addVerseButton.setVisibility(View.VISIBLE);
				showingVerse = true;
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_verse);

		dbhelper = new DbHelper(this);
		//setupTranslationsSpinner();
		List<Book> books = dbhelper.getAllBooks(1);  /* TODO - make this translation id come from somewhere */
		ArrayAdapter<Book> adapter = new ArrayAdapter<Book>(this, android.R.layout.simple_spinner_item, books);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner bookSpinner = (Spinner) findViewById(R.id.books_spinner);
		bookSpinner.setOnItemSelectedListener(this);
		bookSpinner.setAdapter(adapter);
		
		Spinner chapter1Spinner = (Spinner) findViewById(R.id.chapter1_spinner);
		chapter1Spinner.setOnItemSelectedListener(this);
		Spinner chapter2Spinner = (Spinner) findViewById(R.id.chapter2_spinner);
		chapter2Spinner.setOnItemSelectedListener(this);
		Spinner verse1Spinner = (Spinner) findViewById(R.id.verse1_spinner);
		verse1Spinner.setOnItemSelectedListener(this);
		Spinner verse2Spinner = (Spinner) findViewById(R.id.verse2_spinner);
		verse2Spinner.setOnItemSelectedListener(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	public void addNewVerse(View v){
		//runs when add verse button is clicked
		Verse newVerse = new Verse(verseRef, verseBody);
		newVerse.insertVerse(dbhelper);
		finish();
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
		((TextView) findViewById(R.id.text_preview_verse)).setText("...");
	}
	
	public String getTranslationPref(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String translation = prefs.getString(SettingsActivity.PREF_TRANSLATION, SettingsActivity.DEFAULT_TRANSLATION);
		return translation;
	}
	
	public void multiverseCheckbox(View v){
		multiverse = ((CheckBox) v).isChecked();
		if(multiverse){
			findViewById(R.id.chapter2_spinner).setVisibility(View.VISIBLE);
			findViewById(R.id.text_verse2_colon).setVisibility(View.VISIBLE);
			findViewById(R.id.verse2_spinner).setVisibility(View.VISIBLE);
			findViewById(R.id.text_verse_dash).setVisibility(View.VISIBLE);
			findViewById(R.id.button_get_verse1).setVisibility(View.INVISIBLE);
			findViewById(R.id.button_get_verse2).setVisibility(View.VISIBLE);
		}
		else{
			findViewById(R.id.chapter2_spinner).setVisibility(View.INVISIBLE);
			findViewById(R.id.text_verse2_colon).setVisibility(View.INVISIBLE);
			findViewById(R.id.verse2_spinner).setVisibility(View.INVISIBLE);
			findViewById(R.id.text_verse_dash).setVisibility(View.INVISIBLE);
			findViewById(R.id.button_get_verse1).setVisibility(View.VISIBLE);
			findViewById(R.id.button_get_verse2).setVisibility(View.INVISIBLE);
		}
	}
	
	private String newVerseUrl(){
		String urlString = "http://gospelcoding.org/bible/?";
		urlString += "translation=" + getTranslationPref();
		urlString += "&book=" + currentBook.getNumber();
		urlString += "&chapter1=" + currentChapter1;
		urlString += "&verse1=" + currentVerse1;
		if(multiverse){
			urlString += "&chapter2=" + currentChapter2;
			urlString += "&verse2=" + currentVerse2;
		}
		else{
			urlString += "&chapter2=" + currentChapter1;
			urlString += "&verse2=" + currentVerse1;
			
		}
		return urlString;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		
		multiverseCheckbox(findViewById(R.id.checkbox_multiverse));
		if(showingVerse){
			((TextView) findViewById(R.id.text_preview_verse)).setText(Verse.printableBody(verseBody));
			findViewById(R.id.button_add_verse).setVisibility(View.VISIBLE);
		}
	}
	
//	private String newVerseUrlOld(){
//		String urlString = "http://gospelcoding.org/bible/";
//		urlString += getTranslation() + "/";
//		urlString += currentBook.getWebName() + "-" + currentChapter + "-" + currentVerse;
//		return urlString;
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_verse, menu);
		return true;
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
		switch(parent.getId()){
		case R.id.books_spinner:
			currentBook = (Book) parent.getItemAtPosition(pos);
			updateChapter1Spinner();
			break;
		case R.id.chapter1_spinner:
			currentChapter1 = (Integer) parent.getItemAtPosition(pos);
			updateVerse1Spinner();
		case R.id.chapter2_spinner:
			currentChapter2 = (Integer) parent.getItemAtPosition(pos);
			updateVerse2Spinner();
			break;
		case R.id.verse1_spinner:
			currentVerse1 = (Integer) parent.getItemAtPosition(pos);
			updateChapter2Spinner();
		case R.id.verse2_spinner:
			currentVerse2 = (Integer) parent.getItemAtPosition(pos);
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
	
//	private void setupTranslationsSpinner(){
//		DbHelper dbhelper = new DbHelper(this);
//		SQLiteDatabase db = dbhelper.getReadableDatabase();
//		Cursor c = db.query(BunchOfTranslations.TRANSLATIONS_TABLE, null, null, null, null, 
//				null, BunchOfTranslations.NAME_COLUMN+" ASC", null);
//		Spinner translationSpinner = (Spinner) findViewById(R.id.translation_spinner);
//		startManagingCursor(c);
//		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, c, new String[]{"name"}, new int[]{android.R.id.text1});
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		translationSpinner.setAdapter(adapter);
//		stopManagingCursor(c);
//		c.close();
//		db.close();
//	}
	
	private void setVerseRef(){
		if(multiverse)
			verseRef = Reference.makeReferenceString(currentBook.getName(), currentChapter1, currentChapter2, currentVerse1, currentVerse2);
		else
			verseRef = Reference.makeReferenceString(currentBook.getName(), currentChapter1, currentVerse1);
	}
	
	private void updateChapter1Spinner(){
		int bookId = currentBook.getId();
		int numberOfChapters = dbhelper.getNumberOfChapters(bookId);
		updateNumberSpinner(R.id.chapter1_spinner, 1, numberOfChapters);
	}
	
	private void updateChapter2Spinner(){
		//2 if within 6 verses of end of chapter and there is another chapter available
		
		int bookId = currentBook.getId();
		int numberOfChapters = dbhelper.getNumberOfChapters(bookId);
		int firstChapter = currentChapter1;
		int lastChapter = currentChapter1;
		
		if(numberOfChapters > currentChapter1){
			int numberOfVerses = dbhelper.getNumberOfVerses(bookId, currentChapter1);
			if(currentVerse1 + maxVerses - 1 > numberOfVerses){
				++lastChapter;
				if(currentVerse1 == numberOfVerses){
					++firstChapter;
				}
			}
		}
		updateNumberSpinner(R.id.chapter2_spinner, firstChapter, lastChapter);
	}	
	
	private void updateNumberSpinner(int spinnerResource, int start, int stop){
		List<Integer> list = new ArrayList<Integer>();
		for(int i=start; i<=stop; ++i){
			list.add(i);
		}
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner) findViewById(spinnerResource)).setAdapter(adapter);
	}
	
	private void updateVerse1Spinner(){
		int bookId = currentBook.getId();
		int numberOfChapters = dbhelper.getNumberOfChapters(bookId);
		int numberOfVerses = dbhelper.getNumberOfVerses(bookId, currentChapter1);
		if(multiverse && (currentChapter1 == numberOfChapters)){
			--numberOfVerses;
		}
		updateNumberSpinner(R.id.verse1_spinner, 1, numberOfVerses);
	}
	
	private void updateVerse2Spinner(){
		int start = 0;
		int stop = 0;
		int chapter1Verses = dbhelper.getNumberOfVerses(currentBook.getId(), currentChapter1);
		
		if(currentChapter2 == currentChapter1){
			start = currentVerse1 + 1;
			stop = start + maxVerses - 1;
			if(stop > chapter1Verses){
				stop = chapter1Verses;
			}
		}
		else{
			start = 1;
			stop = currentVerse1 + maxVerses - chapter1Verses - 1;
		}
		updateNumberSpinner(R.id.verse2_spinner, start, stop);
	}
}
