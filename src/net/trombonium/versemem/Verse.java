package net.trombonium.versemem;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Verse {
	public static final String TAG = "Verse";
	
	public static final int STATUS_LEARNING = 0;
	public static final int STATUS_MASTERED = 1;
	public static final int STATUS_REFRESHING = 2;
	public static final int STREAK_TYPE_RIGHT = 0;
	public static final int STREAK_TYPE_WRONG = 1;
	
	public static final String VERSES_TABLE = "verses";
	public static final String ID_COLUMN = "_id";
	public static final String REFERENCE_COLUMN = "reference";
	public static final String BODY_COLUMN = "body";
	public static final String STATUS_COLUMN = "status";
	public static final String RIGHT_COLUMN = "right";
	public static final String ATTEMPTS_COLUMN = "attempts";
	public static final String STREAK_COLUMN = "streak";
	public static final String STREAK_TYPE_COLUMN = "streak_type";
	public static final String LAST_ATTEMPT_COLUMN = "last_attempt";
	public static final String WEIGHT_COLUMN = "weight";

	//private variables
	//DbHelper dbhelper;
	long id;
	String reference;
	String body;
	int status;
	int right;
	int attempts;
	int streak;
	int streakType;
	LocalDate lastAttempt;
	float weight;
	
	
	public Verse(String new_ref, String new_body){
		//dbhelper = new_dbhelper;
		reference = new_ref;
		body = new_body;
		right = 0;
		attempts = 0;
		streak = 0;
		streakType = STREAK_TYPE_WRONG; //arbitrary
		lastAttempt = LocalDate.now();  //technically there isn't a last attempt
		weight = 1; //new verse blitz - 100% chance of newest verse until 3 attempts
		
		//id = dbhelper.insertVerse(this);  //this should not happen here, let the creator of verse insert into db
	}
	
	public Verse(Cursor cursor){
		id = DbHelper.getCursorLong(cursor, Verse.ID_COLUMN);
		reference = DbHelper.getCursorString(cursor, Verse.REFERENCE_COLUMN);
		body = DbHelper.getCursorString(cursor, Verse.BODY_COLUMN);
		status = DbHelper.getCursorInt(cursor, Verse.STATUS_COLUMN);
		right = DbHelper.getCursorInt(cursor, Verse.RIGHT_COLUMN);
		attempts = DbHelper.getCursorInt(cursor, Verse.ATTEMPTS_COLUMN);
		streak = DbHelper.getCursorInt(cursor, Verse.STREAK_COLUMN);
		streakType = DbHelper.getCursorInt(cursor, Verse.STREAK_TYPE_COLUMN);
		weight = DbHelper.getCursorFloat(cursor, Verse.WEIGHT_COLUMN);
		lastAttempt = lastAttemptFromString(DbHelper.getCursorString(cursor, Verse.LAST_ATTEMPT_COLUMN));
	}
	
//	public Verse(long newId, String newReference, String newBody, int newStatus, int newRight, 
//			int newAttempts, int newStreak, int newStreakType, String newDate, float newWeight){
//		id = newId;
//		reference = newReference;
//		body = newBody;
//		status = newStatus;
//		right = newRight;
//		attempts = newAttempts;
//		streak = newStreak;
//		streakType = newStreakType;
//		weight = newWeight;
//		lastAttempt = lastAttemptFromString(newDate);	
//	}
	
	public long insertVerse(DbHelper dbhelper){
		ContentValues values = getInsertValues();
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		long verseId = db.insert(Verse.VERSES_TABLE, null, values);
		setBlitzWeights(verseId, dbhelper);
		return verseId;
	}
	
	public static Verse getQuizVerse(DbHelper dbhelper){
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		String[] columns = new String[] {ID_COLUMN, WEIGHT_COLUMN};
		Cursor cursor = db.query("verses", columns, null, null, null, null, ID_COLUMN, null);
		cursor.moveToFirst();
		double r = Math.random();
		double sum = DbHelper.getCursorDouble(cursor, WEIGHT_COLUMN);
		while(sum <= r && !cursor.isLast()){
			cursor.moveToNext();
			double w = DbHelper.getCursorDouble(cursor, WEIGHT_COLUMN);
			sum += w;
		}
		int id = DbHelper.getCursorInt(cursor, ID_COLUMN);
		Cursor cursor2 = db.query(VERSES_TABLE, null, getIdWhereClause(id), null, null, null, null, null);
		cursor2.moveToFirst();
		return new Verse(cursor2);
	}
	
	public boolean checkAttempt(String attempt){
		String[] bodyArray = body.split("\\s+");  //regex should greedily grab whitespace
		String[] attemptArray = attempt.split("\\s+");
		int bodyIndex = 0;
		int attemptIndex = 0;
		
		while(bodyIndex < bodyArray.length && attemptIndex < attemptArray.length){
			String bWord = bodyArray[bodyIndex].toLowerCase(Locale.ENGLISH);
			String aWord = attemptArray[attemptIndex].toLowerCase(Locale.ENGLISH);
			bWord = bWord.replaceAll("[^a-z]", "");  //regex matches anything that's not a letter and removes it from consideration
			aWord = aWord.replaceAll("[^a-z]", ""); 
			
			if(bWord.length()==0)  //skip this word and move on
				++bodyIndex; 
			else if(aWord.length()==0)   //skip this word and move on
				++attemptIndex; 
			else if(bWord.equals(aWord)){  //match
				++bodyIndex;
				++attemptIndex;
			} 
			else{  //mismatch!
				return false;
			}
		}
		if(bodyIndex < bodyArray.length)  //attempt didn't get us to the end :(
			return false;
		
		return true;
	}
	
	public void saveQuizResult(boolean success, DbHelper dbhelper){
		++attempts;
		lastAttempt = LocalDate.now();
		if(success){
			++right;
			
			switch(streakType){
			case STREAK_TYPE_RIGHT:
				++streak;
				break;
			case STREAK_TYPE_WRONG:
				streak = 1;
				streakType = STREAK_TYPE_RIGHT;
			}
			
			switch(status){
			case STATUS_LEARNING:
				if(streak >= 7) status = STATUS_MASTERED;
				break;
			case STATUS_REFRESHING:
				if(streak >= 3) status = STATUS_MASTERED;
			}
		}
		
		else{		//failure
			switch(streakType){
			case STREAK_TYPE_WRONG:
				++streak;
				break;
			case STREAK_TYPE_RIGHT: 
				streak = 1;
				streakType = STREAK_TYPE_WRONG;
			}
			
			switch(status){
			case STATUS_REFRESHING:
				if(streak >= 3) status = STATUS_LEARNING;
				break;
			case STATUS_MASTERED:
				status = STATUS_REFRESHING;
			}
		}
		ContentValues values = makeContentValues();
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		db.update(VERSES_TABLE, values, getIdWhereClause(id), null);
		
		Verse.weighterMax(dbhelper);
	}
	
	public static void setBlitzWeights(long blitzId, DbHelper dbhelper){
		//Log.e("Verse", "blitz");
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		ContentValues blitzWeight = new ContentValues();
		blitzWeight.put(WEIGHT_COLUMN, 1);
		ContentValues zeroWeights = new ContentValues();
		zeroWeights.put(WEIGHT_COLUMN, 0);
		
		db.update(VERSES_TABLE, blitzWeight, getIdWhereClause(blitzId), null);
		db.update(VERSES_TABLE, zeroWeights, ID_COLUMN + " <> " + blitzId, null);	
	}
	
	public static void weighterMax(DbHelper dbhelper){
		//Log.e("Verse", "weighterMax");
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		Cursor vCursor = db.query(VERSES_TABLE, null, null, null, null, null, ID_COLUMN, null);
		
		//NEW VERSE BLITZ! Take the latest verse with <3 attempts (if there is one), set it to one and quit
		long blitzId = -1;
		float wSum = 0;  //overall sum of unnormalized weights
		float lSum = 0;  //sum of unnormalized learning/refreshing verse weights
		HashMap<Long, Float> newWeights = new HashMap<Long, Float>();

		vCursor.moveToLast();
		while(!vCursor.isBeforeFirst() && blitzId < 0){
			long vId = DbHelper.getCursorLong(vCursor, ID_COLUMN);
			//Log.e("Verse", "reading verse: "+vId);
			int vAttempts = DbHelper.getCursorInt(vCursor, ATTEMPTS_COLUMN);
			LocalDate vLastAttempt = lastAttemptFromString(DbHelper.getCursorString(vCursor, LAST_ATTEMPT_COLUMN));
			int vStreakType = DbHelper.getCursorInt(vCursor, STREAK_TYPE_COLUMN);
			int vStatus = DbHelper.getCursorInt(vCursor, STATUS_COLUMN);
			
			if(vAttempts < 3){
				blitzId = vId;
			}
			else{
				float w = (float) Days.daysBetween(vLastAttempt, LocalDate.now()).getDays();
				w += 1.0;  //prevent weights of 0 for verses done today
				if(vStreakType == STREAK_TYPE_WRONG) w = w*2;  //double weight for verses with a wrong streak
				newWeights.put(vId, w);
				wSum += w;
				if(vStatus != STATUS_MASTERED) lSum += w;
			}
			vCursor.moveToPrevious();
		}
		
		if(blitzId > 0){
			setBlitzWeights(blitzId, dbhelper);
		}
		else{  //Normal case
			//normalize weights and save
			float mSum = wSum - lSum;  //sum of unnormalized mastered verse weights
			ContentValues value = new ContentValues();
			vCursor.moveToFirst();
			while(!vCursor.isAfterLast()){
				long vId = DbHelper.getCursorLong(vCursor, ID_COLUMN);
				int vStatus = DbHelper.getCursorInt(vCursor, STATUS_COLUMN);
				float w = newWeights.get(vId);
				if(mSum < lSum){
					w = w / wSum;  //normalize all weights to sum=1 as long as learning/refreshing is >= half
				}
				else if(vStatus == STATUS_MASTERED){  //otherwise normalize mastered verse weights to 0.5 and learning/refreshing verse weights to 0.5
					w = w / (2 * mSum);
				}
				else{  //STATUS_LEARNING or STATUS_REFRESHING
					w = w / (2 * lSum);
				}
				value.put(WEIGHT_COLUMN, w);
				db.update(VERSES_TABLE, value, getIdWhereClause(vId), null);
				vCursor.moveToNext();
			}
		}
	}
	
 	public static LocalDate lastAttemptFromString(String s){
		String[] params = s.split("-");
		return new LocalDate(Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]));
	}
	
	private ContentValues makeContentValues(){
		ContentValues values = new ContentValues();
		values.put(REFERENCE_COLUMN, reference);
		values.put(BODY_COLUMN, body);
		values.put(STATUS_COLUMN, status);
		values.put(RIGHT_COLUMN, right);
		values.put(ATTEMPTS_COLUMN, attempts);
		values.put(STREAK_COLUMN, streak);
		values.put(STREAK_TYPE_COLUMN, streakType);
		values.put(LAST_ATTEMPT_COLUMN, getLastAttemptString());
		values.put(WEIGHT_COLUMN, weight);
		return values;
	}
	
	//all the getters
	public long getId(){ return id; }
	public static String getIdWhereClause(long id){
		return ID_COLUMN + " = " + id;
	}
	public String getReference(){ return reference; }
	public String getBody(){ return body; }
	public int getStatus(){ return status; }
	public String getStatusString(){
		switch(status){
		case STATUS_LEARNING: return "Learning";
		case STATUS_REFRESHING: return "Refreshing";
		case STATUS_MASTERED: return "Mastered";
		}
		return "";
	}
	public int getRight(){ return right; }
	public int getAttempts(){ return attempts; }
	public int getStreak(){ return streak; }
	public int getStreakType(){ return streakType; }
	public LocalDate getLastAttempt(){ return lastAttempt; }
	public String getLastAttemptString(){
		return lastAttempt.getYear() + "-" + lastAttempt.getMonthOfYear() + "-" + lastAttempt.getDayOfMonth();
	}
	public float getWeight(){ return weight; }
	
	public ContentValues getInsertValues(){
		return makeContentValues();
	}
	
	//Will be used by ArrayAdapter in ListView
	@Override
	public String toString(){  
		String s = reference + " - " + getStatusString() + " " + right + "/" + attempts;
		if(streakType == STREAK_TYPE_RIGHT)
			s += " " + streak + " in a row";
		return s;
	}
}
