package net.trombonium.versemem;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.Spinner;

public class NewVerseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_verse);
		
		Spinner spinner = (Spinner) findViewById(R.id.books_spinner);
		DbHelper dbhelper = new DbHelper(this);
		List<String> books = dbhelper.getAllBooks(1);  /* TODO - make this translation id come from somewhere */
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, books);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_verse, menu);
		return true;
	}

	
	
}
