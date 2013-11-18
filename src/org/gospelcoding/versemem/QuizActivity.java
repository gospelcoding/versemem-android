package org.gospelcoding.versemem;

import org.gospelcoding.versemem.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class QuizActivity extends Activity {
	
	private Verse quizVerse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);
		quizVerse = Verse.getQuizVerse(new DbHelper(this));
		displayQuiz(quizVerse.getReference());
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quiz, menu);
		return true;
	}

	
	private void displayQuiz(String reference){
		TextView quizText = (TextView) findViewById(R.id.quiz_text);
		quizText.setText(reference);
	}
	
	public void showResult(boolean result){
		setContentView(R.layout.activity_quiz_result);
		
		TextView resultText = (TextView) findViewById(R.id.quiz_result_text);
		if(result){
			resultText.setText("Good");
		}
		else{
			resultText.setText("Sorry :(");
		}

		DbHelper dbhelper = new DbHelper(this);
		quizVerse.saveQuizResult(result, dbhelper);
		int attemptId = dbhelper.getNextAttemptId() - 1;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(attemptId);
	}
	
	public void submitQuiz(View view){
		EditText quizAttempt = (EditText) findViewById(R.id.quiz_attempt);
		String attempt = quizAttempt.getText().toString();
		boolean result = quizVerse.checkAttempt(attempt);
		showResult(result);
		
	}
}
