package org.gospelcoding.versemem;

import java.io.File;
import java.io.IOException;

import org.gospelcoding.versemem.R;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
	//private int quizId;
	
	//for microphone quiz
	private boolean recordingNow = false;
	private MediaRecorder recorder = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//quizId = (new DbHelper(this)).getQuizId();
		displayQuiz();
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		if(recordingNow){
			stopRecording();
			displayQuizMicrophone();
			recordingNow = false;
			Log.e("QuizActivity", "onStop!");
		}
	}
	
	private void displayQuiz(){
		long requizId = getIntent().getLongExtra(QuizResultActivity.VERSE_ID, -1);
		if(requizId > 0){
			quizVerse = Verse.getQuizVerse(new DbHelper(this), requizId);
		}
		else{
			quizVerse = Verse.getQuizVerse(new DbHelper(this));
		}
		setTitle(quizVerse.getReference());
		quizStyle = PreferenceManager.getDefaultSharedPreferences(this)
				.getString(SettingsActivity.PREF_QUIZ_STYLE, SettingsActivity.DEFAULT_QUIZ_STYLE);
		
		if(quizStyle.equals(SettingsActivity.KEYBOARD_AUTO) || quizStyle.equals(SettingsActivity.KEYBOARD_SELF)){
			displayQuizKeyboard();
		}
		else if(quizStyle.equals(SettingsActivity.MICROPHONE)){
			displayQuizMicrophone();
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
		((TextView) findViewById(R.id.quiz_text)).setText(quizVerse.getReference());
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	
	public void displayQuizMicrophone(){
		setContentView(R.layout.quiz_microphone);
		((TextView) findViewById(R.id.quiz_text)).setText(quizVerse.getReference());
	}
	
	private String getRecordingFile(){
		String path = getRecordingFilepath();
		(new File(path)).mkdirs();
		return getRecordingFilename();
	}
	
	public static String getRecordingFilename(){
		return getRecordingFilepath() + "/recorded_verse.3gp";
	}
	
	public static String getRecordingFilepath(){
		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/versemem";
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quiz, menu);
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
	
	public void recordAttempt(View v){
		if(recordingNow){
			stopRecording();
			setContentView(R.layout.quiz_microphone_recorded);
			((TextView) findViewById(R.id.quiz_text)).setText(quizVerse.getReference());
		}
		else{
			startRecording();
			((Button) findViewById(R.id.button_record)).setText(R.string.stop_recording);
		}
		recordingNow = !recordingNow;
	}
	
	public void rerecord(View v){
		displayQuizMicrophone();
	}
	
	public void startRecording(){
		recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //TODO handle case where there is no sd card
        recorder.setOutputFile(getRecordingFile());
        

            try {
				recorder.prepare();
	            recorder.start();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void stopRecording(){
        recorder.stop();
        recorder.release();
        recorder = null;
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
		//intent.putExtra(QuizResultActivity.QUIZ_ID, quizId);
		if(quizStyle.equals(SettingsActivity.KEYBOARD_AUTO)){
			boolean result = quizVerse.checkAttempt(attempt);
			intent.putExtra(QuizResultActivity.SUCCESS, result);
		}
		startActivity(intent);
		finish();
	}
	
	public void submitQuizMicrophone(View v){
		Intent intent = new Intent(this, QuizResultActivity.class);
		intent.putExtra(QuizResultActivity.REFERENCE, quizVerse.getReference());
		intent.putExtra(QuizResultActivity.VERSE_BODY, quizVerse.getBody());
		intent.putExtra(QuizResultActivity.QUIZ_STYLE, quizStyle);
		intent.putExtra(QuizResultActivity.VERSE_ID, quizVerse.getId());
		//intent.putExtra(QuizResultActivity.QUIZ_ID, quizId);
		startActivity(intent);
		finish();
	}
	
	public void submitQuizNoInput(){
		Intent intent = new Intent(this, QuizResultActivity.class);
		intent.putExtra(QuizResultActivity.REFERENCE, quizVerse.getReference());
		intent.putExtra(QuizResultActivity.VERSE_BODY, quizVerse.getBody());
		intent.putExtra(QuizResultActivity.QUIZ_STYLE, quizStyle);
		intent.putExtra(QuizResultActivity.VERSE_ID, quizVerse.getId());
		//intent.putExtra(QuizResultActivity.QUIZ_ID, quizId);
		startActivity(intent);
		finish();
	}
}
