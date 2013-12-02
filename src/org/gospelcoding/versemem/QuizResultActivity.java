package org.gospelcoding.versemem;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QuizResultActivity extends Activity {
	
	public static final String ATTEMPT = "org.gospelcoding.versemem.attempt";
	public static final String VERSE_BODY = "org.gospelcoding.versemem.verse_body";
	public static final String SUCCESS = "org.gospelcoding.versemem.success";
	public static final String QUIZ_STYLE = "org.gospelcoding.versemem.quiz_style";
	public static final String VERSE_ID = "org.gospelcoding.versemem.verse_id";
	public static final String REFERENCE = "org.gospelcoding.versemem.reference";
	public static final String QUIZ_ID = "org.gospelcoding.versemem.quiz_id";
	
	private String quizStyle;
	private long verseId;
	private int quizId;
	private int resultStatus = -1;
	private boolean success;
	private final int STATUS_SHOWING_REF = 0;
	private final int STATUS_NO_INPUT_SHOWING_ANSWER = 1;
	private final int STATUS_SHOWING_RESULTS = 2;
	private final int STATUS_KEYBOARD_SHOWING_ANSWER = 3;
	private final int STATUS_MIC_SHOWING_ANSWER = 4;
	
	private MediaPlayer mPlayer = null;
	
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
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);	
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Intent intent = getIntent();
		//TODO revisit this idea
		if(quizId != intent.getIntExtra(QUIZ_ID, -1)){
			quizId = intent.getIntExtra(QUIZ_ID, -1);
			quizStyle = intent.getStringExtra(QUIZ_STYLE);
			verseId = intent.getLongExtra(VERSE_ID, -1);
			showAnswer();
		}
		else{
			if(quizStyle.equals(SettingsActivity.MICROPHONE)){
				initMediaPlayer();
			}
		}
	}
	
	@Override
	public void onStop(){
		super.onStop();
		if(mPlayer != null){
			mPlayer.release();
			mPlayer = null;
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		if(resultStatus == STATUS_SHOWING_RESULTS && quizStyle.equals(SettingsActivity.NO_INPUT)){
			resultStatus = STATUS_NO_INPUT_SHOWING_ANSWER;
			adjustView();
			resultStatus = STATUS_SHOWING_RESULTS;
		}
		adjustView();
	}
	
	public void showAnswer(){
		if(quizStyle.equals(SettingsActivity.KEYBOARD_AUTO)){
			success = getIntent().getBooleanExtra(SUCCESS, false);
			if(success) showAnswerSuccess();
			else showAnswerFailure();
			saveResult(success);
		}
		else if(quizStyle.equals(SettingsActivity.KEYBOARD_SELF)){
			showAnswerKeyboard();
		}
		else if(quizStyle.equals(SettingsActivity.MICROPHONE)){
			showAnswerMicrophone();
		}
		else if(quizStyle.equals(SettingsActivity.NO_INPUT)){
			showQuizNoInput();
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
		resultStatus = STATUS_KEYBOARD_SHOWING_ANSWER;
		adjustView();
	}
	
	public void showAnswerMicrophone(){
		setContentView(R.layout.quiz_result_microphone);
		resultStatus = STATUS_MIC_SHOWING_ANSWER;
		adjustView();
		initMediaPlayer();
		playPause((Button) findViewById(R.id.button_play));
	}
	
	public void playPause(View v){
		Button playButton = (Button) v;
		try{
			if(mPlayer.isPlaying()){
				mPlayer.pause();
				playButton.setText(R.string.play_button);
			}
			else{
				mPlayer.start();
				playButton.setText(R.string.pause_button);
			}
		} catch(IllegalStateException e){
			e.printStackTrace();
		}
	}
	
	private void initMediaPlayer(){
		mPlayer = new MediaPlayer();
		OnCompletionListener doneListener = new OnCompletionListener(){
			@Override
			public void onCompletion(MediaPlayer mPlayer) {
				((Button) findViewById(R.id.button_play)).setText(R.string.play_button);
			}
		};
		try{
			mPlayer.setDataSource(QuizActivity.getRecordingFilename());
			mPlayer.setOnCompletionListener(doneListener);
			mPlayer.prepare();
		} catch (IOException e){
			//TODO more here
			e.printStackTrace();
		}
	}
	
	
	public void showQuizNoInput(){
		setContentView(R.layout.quiz_result_no_input);
		resultStatus = STATUS_SHOWING_REF;
		String reference = getIntent().getStringExtra(REFERENCE);
		((TextView) findViewById(R.id.quiz_text)).setText(reference);
	}
	
	public void showAnswerNoInput(View showAnswerButton){
		resultStatus = STATUS_NO_INPUT_SHOWING_ANSWER;
		adjustView();
//		String[] verseArray = verseBody.split("\\s+");
//		String currentAnswer = verseArray[0];
//		answerView.setText(currentAnswer);
//		for(int i=1; i<verseArray.length; ++i){
//			try {
//				Thread.sleep(300);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			currentAnswer += " " + verseArray[i];
//			answerView.setText(currentAnswer);
//		}
	}
	
	public void showResult(View v){
		resultStatus = STATUS_SHOWING_RESULTS;
		success = false;
		if(v.getId()==R.id.button_right) success = true;
		saveResult(success);
		adjustView();
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
	
	public void adjustView(){
		switch(resultStatus){
		case STATUS_NO_INPUT_SHOWING_ANSWER:
            TextView rightAnswerText = (TextView) findViewById(R.id.right_answer_text);
            String verseBody = getIntent().getStringExtra(VERSE_BODY);
            rightAnswerText.setText(verseBody);
            findViewById(R.id.button_show_answer).setVisibility(View.INVISIBLE);
            findViewById(R.id.button_right).setVisibility(View.VISIBLE);
            findViewById(R.id.button_wrong).setVisibility(View.VISIBLE);
            break;
		case STATUS_KEYBOARD_SHOWING_ANSWER:
			verseBody = getIntent().getStringExtra(VERSE_BODY);
			String attempt = getIntent().getStringExtra(ATTEMPT);
			String reference = getIntent().getStringExtra(REFERENCE);
			rightAnswerText = (TextView) findViewById(R.id.right_answer_text);
			rightAnswerText.setText(reference + "\n\n" + verseBody);
			TextView userAnswerText = (TextView) findViewById(R.id.user_answer_text);
			userAnswerText.setText(getString(R.string.user_answer) + "\n\n" + attempt);
			break;
		case STATUS_MIC_SHOWING_ANSWER:
            rightAnswerText = (TextView) findViewById(R.id.right_answer_text);
            verseBody = getIntent().getStringExtra(VERSE_BODY);
            rightAnswerText.setText(verseBody);
			break;
		case STATUS_SHOWING_RESULTS:
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
	}

}
