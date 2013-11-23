package org.gospelcoding.versemem;

import java.util.ArrayList;
import java.util.List;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteAssetHelper {
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "versemem.db";
	public static final String TAG = "DbHelper";
	
	public static final String CHAPTER_NUMS_TABLE = "chapternums";
	public static final String BOOK_ID_COLUMN = "book_id";
	public static final String NUMBER_OF_CHAPTERS_COLUMN = "number_of_chapters";
	public static final String VERSE_NUMS_TABLE = "versenums";
	public static final String CHAPTER_COLUMN = "chapter";
	public static final String NUMBER_OF_VERSES_COLUMN = "number_of_verses";
	
	public DbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public Cursor getVersesCursor(){
		SQLiteDatabase db = getReadableDatabase();
		return db.query(Verse.VERSES_TABLE, null, null, null, null, null, Verse.ID_COLUMN, null);
	}
	
	public static String getCursorString(Cursor c, String s){
		return c.getString(c.getColumnIndex(s));
	}
	
	public static int getCursorInt(Cursor c, String s){
		return c.getInt(c.getColumnIndex(s));
	}
	
	public static long getCursorLong(Cursor c, String s){
		return c.getLong(c.getColumnIndex(s));
	}
	
	public static float getCursorFloat(Cursor c, String s){
		return c.getFloat(c.getColumnIndex(s));
	}
	
	public static double getCursorDouble(Cursor c, String s){
		return c.getDouble(c.getColumnIndex(s));
	}
	
//	public Verse verseFromCursor(Cursor c){
//		long id = getCursorLong(c, Verse.ID_COLUMN);
//		String reference = getCursorString(c, Verse.REFERENCE_COLUMN);
//		String body = getCursorString(c, Verse.BODY_COLUMN);
//		int status = getCursorInt(c, Verse.STATUS_COLUMN);
//		int right = getCursorInt(c, Verse.RIGHT_COLUMN);
//		int attempts = getCursorInt(c, Verse.ATTEMPTS_COLUMN);
//		int streak = getCursorInt(c, Verse.STREAK_COLUMN);
//		int streakType = getCursorInt(c, Verse.STREAK_TYPE_COLUMN);
//		float weight = getCursorFloat(c, Verse.WEIGHT_COLUMN);
//		String lastAttempt = getCursorString(c, Verse.LAST_ATTEMPT_COLUMN);
//		
//		return new Verse(id, reference, body, status, right, attempts, streak, streakType, lastAttempt, weight);
//	}
	
	public List<Verse> getAllVerses(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(Verse.VERSES_TABLE, null, null, null, null, null, Verse.ID_COLUMN, null);
		List<Verse> verses = new ArrayList<Verse>();
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Verse v = new Verse(cursor);
			verses.add(v);
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return verses;
	}
	
	public List<Book> getAllBooks(int translation_id){
		SQLiteDatabase db = getReadableDatabase();
		List<Book> books = new ArrayList<Book>();
		Cursor c = db.query(Book.BOOKS_TABLE, new String[]{Book.ID_COLUMN, Book.NAME_COLUMN}, Book.TRANSLATION_ID_COLUMN+"="+translation_id, null, null, null, Book.ID_COLUMN, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			int id = c.getInt(0);
			String name = c.getString(1);
			books.add(new Book(id, name));
			c.moveToNext();
		}
		c.close();
		db.close();
		return books;
	}
	
	public int getNumberOfChapters(int bookId){
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(CHAPTER_NUMS_TABLE, new String[]{NUMBER_OF_CHAPTERS_COLUMN}, BOOK_ID_COLUMN+"="+bookId, null, null, null, null, "1");
		c.moveToFirst();
		int chapters = c.getInt(0);
		c.close();
		db.close();
		return chapters;
	}
	
	public int getNumberOfVerses(int bookId, int chapter){
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(VERSE_NUMS_TABLE, new String[]{NUMBER_OF_VERSES_COLUMN}, BOOK_ID_COLUMN+"="+bookId+" and "+CHAPTER_COLUMN+"="+chapter, null, null, null, null, "1");
		c.moveToFirst();
		int verses = c.getInt(0);
		c.close();
		db.close();
		return verses;
	}
	
	public int getNextAttemptId(){
		//attempt id's start at 0 and increment for each attempt on any verse
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(Verse.VERSES_TABLE, new String[]{"SUM("+Verse.ATTEMPTS_COLUMN+")"}, null, null, null, null, null, null);
		c.moveToFirst();
		int attemptId = c.getInt(0);
		c.close();
		return attemptId;
	}

	/*
	@Override
	public void onCreate(SQLiteDatabase db) {
		//String version_table_sql = "CREATE TABLE version(version INTEGER)";
		//String version_insert_sql = "INSERT INTO version VALUES(" + VERSION + ")";	
		String verses_table_sql = "CREATE TABLE " + Verse.VERSES_TABLE + "(" +
				Verse.ID_COLUMN + " INTEGER PRIMARY KEY," +
				Verse.REFERENCE_COLUMN + " TEXT," +
				Verse.BODY_COLUMN + " TEXT," +
				Verse.STATUS_COLUMN + " INTEGER," +
				Verse.RIGHT_COLUMN + " INTEGER," +
				Verse.ATTEMPTS_COLUMN + " INTEGER," +
				Verse.STREAK_COLUMN + " INTEGER," +
				Verse.STREAK_TYPE_COLUMN + " INTEGER," +
				Verse.LAST_ATTEMPT_COLUMN + " TEXT," +
				Verse.WEIGHT_COLUMN + " REAL)";
		
		//db.execSQL(version_table_sql);
		//db.execSQL(version_insert_sql);
		Log.e(TAG, "Creating verses table");
		db.execSQL(verses_table_sql);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e(TAG, "Upgrading...");
		switch(oldVersion){
		default:
			//what the heck just happened?
			Log.e(TAG, "Tried to upgrade from version "+String.valueOf(oldVersion)+" to version "+String.valueOf(newVersion)+" but oldVersion was not a case in the switch statement.");
			break;
		}
		
	}
	*/
}
