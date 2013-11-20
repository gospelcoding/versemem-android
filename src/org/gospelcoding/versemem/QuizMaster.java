package org.gospelcoding.versemem;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class QuizMaster {
	private static int mId = 0;

	public static void notifyOfQuiz(Context context){
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("Verse Mem")
		.setContentText("Practice a Verse");
		// Creates an explicit intent for an Activity in your app
		PendingIntent resultIntent =  PendingIntent.getActivity(context, 0, new Intent(context, QuizActivity.class), Intent.FLAG_ACTIVITY_NEW_TASK);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		//TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		//stackBuilder.addParentStack(QuizActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		//stackBuilder.addNextIntent(resultIntent);
//		PendingIntent resultPendingIntent =
//				stackBuilder.getPendingIntent(
//						0,
//						PendingIntent.FLAG_UPDATE_CURRENT
//						);
		mBuilder.setContentIntent(resultIntent);
		NotificationManager mNotificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
		
		
		
		
		
	}
}
