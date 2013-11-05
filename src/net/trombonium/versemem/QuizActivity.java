package net.trombonium.versemem;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class QuizActivity extends Activity {
	
	public final static String QUIZ_RESULT = "net.trombonium.versemem.quiz_result";
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

		quizVerse.saveQuizResult(result, new DbHelper(this));
	}
	
	public void submitQuiz(View view){
		EditText quizAttempt = (EditText) findViewById(R.id.quiz_attempt);
		String attempt = quizAttempt.getText().toString();
		boolean result = quizVerse.checkAttempt(attempt);
		showResult(result);
		
	}
}
