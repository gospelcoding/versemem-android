package org.gospelcoding.versemem;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class QuizNotificationService extends IntentService {

	public QuizNotificationService() {
		super("Quiz Notification Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		notifyQuiz();
	}

	public void notifyQuiz(){
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("Verse Mem")
		.setContentText("Practice a Verse")
		.setAutoCancel(true);
		Intent notifyIntent =
				new Intent(this, QuizActivity.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent notifyPIntent =
				PendingIntent.getActivity(
						this,
						0,
						notifyIntent,
						PendingIntent.FLAG_UPDATE_CURRENT
						);

		builder.setContentIntent(notifyPIntent);
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		DbHelper dbhelper = new DbHelper(this);
		int attemptId = dbhelper.getNextAttemptId();
		mNotificationManager.notify(attemptId, builder.build());
	}
}
