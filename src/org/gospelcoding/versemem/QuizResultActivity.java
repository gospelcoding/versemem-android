package org.gospelcoding.versemem;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class QuizResultActivity extends Activity {
	
	public static final String ATTEMPT = "org.gospelcoding.versemem.attempt";
	public static final String VERSE_BODY = "org.gospelcoding.versemem.verse_body";
	public static final String SUCCESS = "org.gospelcoding.versemem.success";
	public static final String QUIZ_STYLE = "org.gospelcoding.versemem.quiz_style";
	public static final String VERSE_ID = "org.gospelcoding.versemem.verse_id";
	public static final String REFERENCE = "org.gospelcoding.versemem.reference";
	
	private String quizStyle;
	private long verseId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quiz_result, menu);
		return true;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Intent intent = getIntent();
		quizStyle = intent.getStringExtra(QUIZ_STYLE);
		verseId = intent.getLongExtra(VERSE_ID, -1);
		showAnswer();
	}
	
	public void showAnswer(){
		if(quizStyle.equals(SettingsActivity.KEYBOARD_AUTO)){
			boolean success = getIntent().getBooleanExtra(SUCCESS, false);
			if(success) showAnswerSuccess();
			else showAnswerFailure();
			saveResult(success);
		}
		else if(quizStyle.equals(SettingsActivity.KEYBOARD_SELF)){
			showAnswerKeyboard();
		}
		else if(quizStyle.equals(SettingsActivity.MICROPHONE)){
			
		}
		else if(quizStyle.equals(SettingsActivity.NO_INPUT)){
			
		}
		else{
			Log.e("QuizResultActivity", "Unknown quiz type: "+quizStyle);
		}
	}
	
	public void showAnswerSuccess(){
		setContentView(R.layout.quiz_result_success);
		String verseBody = getIntent().getStringExtra(VERSE_BODY);
		TextView resultText = (TextView) findViewById(R.id.quiz_result_text);
		resultText.setText(getString(R.string.quiz_success) + "\n\n" + verseBody);
	}
	
	public void showAnswerFailure(){
		setContentView(R.layout.quiz_result_failure);
		String verseBody = getIntent().getStringExtra(VERSE_BODY);
		String attempt = getIntent().getStringExtra(ATTEMPT);
		TextView rightAnswerText = (TextView) findViewById(R.id.right_answer_text);
		rightAnswerText.setText(verseBody);
		TextView wrongAnswerText = (TextView) findViewById(R.id.wrong_answer_text);
		wrongAnswerText.setText(attempt);
	}
	
	public void showAnswerKeyboard(){
		setContentView(R.layout.quiz_result_keyboard);
		String verseBody = getIntent().getStringExtra(VERSE_BODY);
		String attempt = getIntent().getStringExtra(ATTEMPT);
		String reference = getIntent().getStringExtra(REFERENCE);
		TextView rightAnswerText = (TextView) findViewById(R.id.right_answer_text);
		rightAnswerText.setText(reference + "\n\n" + verseBody);
		TextView userAnswerText = (TextView) findViewById(R.id.user_answer_text);
		userAnswerText.setText(getString(R.string.user_answer) + "\n\n" + attempt);
	}
	
	public void showResult(View v){
		boolean success = false;
		if(v.getId()==R.id.button_right) success = true;
		
		saveResult(success);
		findViewById(R.id.button_go_to_list).setVisibility(View.VISIBLE);
		findViewById(R.id.button_right).setVisibility(View.INVISIBLE);
		findViewById(R.id.button_wrong).setVisibility(View.INVISIBLE);
		
		if(success){
			((TextView) findViewById(R.id.quiz_result_text)).setText(R.string.quiz_success);
			findViewById(R.id.button_new_quiz).setVisibility(View.VISIBLE);
		}
		else{
			((TextView) findViewById(R.id.quiz_result_text)).setText(R.string.quiz_failure_manual);
			findViewById(R.id.button_requiz).setVisibility(View.VISIBLE);
		}
	}
	
	public void saveResult(boolean success){
		DbHelper dbhelper = new DbHelper(this);
		Verse.saveQuizResult(dbhelper, verseId, success);
	}
	
	public void requiz(View v){
		Intent intent = new Intent(this, QuizActivity.class);
		intent.putExtra(VERSE_ID, verseId);
		startActivity(intent);
		finish();
	}
	
	public void newQuiz(View v){
		Intent intent = new Intent(this, QuizActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void goToList(View v){
		Intent intent = new Intent(this, VerseListActivity.class);
		startActivity(intent);
		finish();
	}

}
