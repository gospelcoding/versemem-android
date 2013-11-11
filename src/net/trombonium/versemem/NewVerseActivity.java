package net.trombonium.versemem;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.Spinner;

public class NewVerseActivity extends Activity implements OnItemSelectedListener{

	private Book currentBook;
	private int currentChapter;
	private int currentVerse;
	private DbHelper dbhelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		Log.e("Spinners", "Updating chapter spinner for book id "+bookId);
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
		Log.e("Spinners", "Updating verse spinner for book id " + bookId + " chapter "+currentChapter);
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
	
	public void onNothingSelected(AdapterView<?> parent){
		//do nothing?
	}
	
}
