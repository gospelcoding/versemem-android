package org.gospelcoding.versemem;

import org.gospelcoding.versemem.R;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QuizActivity extends Activity{
	
	private Verse quizVerse;
	private String quizStyle;
	private int quizId;
	private boolean recordingNow = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		quizId = (new DbHelper(this)).getQuizId();
		displayQuiz();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quiz, menu);
		return true;
	}

	
	private void displayQuiz(){
		long requizId = getIntent().getLongExtra(QuizResultActivity.VERSE_ID, -1);
		if(requizId > 0){
			quizVerse = Verse.getQuizVerse(new DbHelper(this), requizId);
		}
		else{
			quizVerse = Verse.getQuizVerse(new DbHelper(this));
		}
		quizStyle = PreferenceManager.getDefaultSharedPreferences(this)
				.getString(SettingsActivity.PREF_QUIZ_STYLE, SettingsActivity.DEFAULT_QUIZ_STYLE);
		
		if(quizStyle.equals(SettingsActivity.KEYBOARD_AUTO) || quizStyle.equals(SettingsActivity.KEYBOARD_SELF)){
			displayQuizKeyboard();
		}
		else if(quizStyle.equals(SettingsActivity.MICROPHONE)){
			
		}
		else if(quizStyle.equals(SettingsActivity.NO_INPUT)){
			submitQuizNoInput();
		}
		else{
			Log.e("QuizActivity", "Unknown quiz type: "+quizStyle);
		}
	}
	
	public void displayQuizKeyboard(){
		setContentView(R.layout.quiz_keyboard);
		String reference = quizVerse.getReference();
		TextView quizText = (TextView) findViewById(R.id.quiz_text);
		quizText.setText(reference);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	
	public void submitQuizNoInput(){
		Intent intent = new Intent(this, QuizResultActivity.class);
		intent.putExtra(QuizResultActivity.REFERENCE, quizVerse.getReference());
		intent.putExtra(QuizResultActivity.VERSE_BODY, quizVerse.getBody());
		intent.putExtra(QuizResultActivity.QUIZ_STYLE, quizStyle);
		intent.putExtra(QuizResultActivity.VERSE_ID, quizVerse.getId());
		intent.putExtra(QuizResultActivity.QUIZ_ID, quizId);
		startActivity(intent);
		finish();
	}
	
	public void submitQuizKeyboard(View view){
		EditText quizAttempt = (EditText) findViewById(R.id.quiz_attempt);
		String attempt = quizAttempt.getText().toString();
		Intent intent = new Intent(this, QuizResultActivity.class);
		intent.putExtra(QuizResultActivity.ATTEMPT, attempt);
		intent.putExtra(QuizResultActivity.REFERENCE, quizVerse.getReference());
		intent.putExtra(QuizResultActivity.VERSE_BODY, quizVerse.getBody());
		intent.putExtra(QuizResultActivity.QUIZ_STYLE, quizStyle);
		intent.putExtra(QuizResultActivity.VERSE_ID, quizVerse.getId());
		intent.putExtra(QuizResultActivity.QUIZ_ID, quizId);
		if(quizStyle.equals(SettingsActivity.KEYBOARD_AUTO)){
			boolean result = quizVerse.checkAttempt(attempt);
			intent.putExtra(QuizResultActivity.SUCCESS, result);
		}
		startActivity(intent);
		finish();
	}
}
