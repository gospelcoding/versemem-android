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
	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "versemem";
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
	
	public List<Verse> getAllVerses(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(Verse.VERSES_TABLE, null, null, null, null, null, Verse.STATUS_COLUMN + ", " + Verse.STREAK_COLUMN, null);
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
	
	public static double getCursorDouble(Cursor c, String s){
		return c.getDouble(c.getColumnIndex(s));
	}
	
	public static float getCursorFloat(Cursor c, String s){
		return c.getFloat(c.getColumnIndex(s));
	}
	
	public static int getCursorInt(Cursor c, String s){
		return c.getInt(c.getColumnIndex(s));
	}

	public static long getCursorLong(Cursor c, String s){
		return c.getLong(c.getColumnIndex(s));
	}
	
	public static String getCursorString(Cursor c, String s){
		return c.getString(c.getColumnIndex(s));
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
	
	public int getNumberOfChapters(String bookName){
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT "+NUMBER_OF_CHAPTERS_COLUMN+" FROM "+CHAPTER_NUMS_TABLE+" INNER JOIN "+Book.BOOKS_TABLE+" ON "+
				CHAPTER_NUMS_TABLE+"."+BOOK_ID_COLUMN+"="+Book.BOOKS_TABLE+"."+Book.ID_COLUMN+" WHERE "+Book.BOOKS_TABLE+
				"."+Book.NAME_COLUMN+"=? LIMIT 1";
		Cursor c = db.rawQuery(sql, new String[]{bookName});
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
	
	public int getNumberOfVerses(String bookName, int chapter){
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT "+NUMBER_OF_VERSES_COLUMN+" FROM "+VERSE_NUMS_TABLE+" INNER JOIN "+Book.BOOKS_TABLE+" ON "+
				VERSE_NUMS_TABLE+"."+BOOK_ID_COLUMN+"="+Book.BOOKS_TABLE+"."+Book.ID_COLUMN+" WHERE "+Book.BOOKS_TABLE+
				"."+Book.NAME_COLUMN+"=? AND "+VERSE_NUMS_TABLE+"."+CHAPTER_COLUMN+"=? LIMIT 1";
		Cursor c = db.rawQuery(sql, new String[]{bookName, Integer.toString(chapter)});
		c.moveToFirst();
		int verses = c.getInt(0);
		c.close();
		db.close();
		return verses;
	}
	
	public int getQuizId(){
		//quiz id's start at 0 and increment for each attempt on any verse
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(Verse.VERSES_TABLE, new String[]{"SUM("+Verse.ATTEMPTS_COLUMN+")"}, null, null, null, null, null, null);
		c.moveToFirst();
		int attemptId = c.getInt(0);
		c.close();
		db.close();
		return attemptId;
	}
	
	public Cursor getVersesCursor(){
		SQLiteDatabase db = getReadableDatabase();
		return db.query(Verse.VERSES_TABLE, null, null, null, null, null, Verse.ID_COLUMN, null);
	}
}
