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
	
	//for microphone quiz
	private boolean recordingNow = false;
	private MediaRecorder recorder = null;
	public static final String MICROPHONE_ATTEMPT_PATH = "/versemem";
	public static final String MICROPHONE_ATTEMPT_FILE = "/recorded_verse.3gp";

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
	
	public void recordAttempt(View v){
		if(recordingNow){
			stopRecording();
			setContentView(R.layout.quiz_microphone_recorded);
		}
		else{
			startRecording();
			((Button) findViewById(R.id.button_record)).setText(R.string.stop_recording);
		}
		recordingNow = !recordingNow;
	}
	
	private String getRecordingFile(){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		path += MICROPHONE_ATTEMPT_PATH;
		(new File(path)).mkdirs();
		return path + MICROPHONE_ATTEMPT_FILE;
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
	
	public void submitQuizMicrophone(View v){
		Intent intent = new Intent(this, QuizResultActivity.class);
		intent.putExtra(QuizResultActivity.REFERENCE, quizVerse.getReference());
		intent.putExtra(QuizResultActivity.VERSE_BODY, quizVerse.getBody());
		intent.putExtra(QuizResultActivity.QUIZ_STYLE, quizStyle);
		intent.putExtra(QuizResultActivity.VERSE_ID, quizVerse.getId());
		intent.putExtra(QuizResultActivity.QUIZ_ID, quizId);
		startActivity(intent);
		finish();
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
