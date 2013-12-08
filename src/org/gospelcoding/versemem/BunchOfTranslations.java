package org.gospelcoding.versemem;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BunchOfTranslations {
	
	public static String TRANSLATIONS_TABLE = "translations";
	public static String ID_COLUMN = "_id";
	public static String NAME_COLUMN = "name";
	public static String ABBREVIATION_COLUMN = "abbreviation";
	
	private String[] names;
	private String[] ids;
	
	public BunchOfTranslations(DbHelper dbhelper){
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor c = db.query(TRANSLATIONS_TABLE, new String[]{ID_COLUMN, NAME_COLUMN}, null, null, null, null, NAME_COLUMN+" ASC", null);
		int numberOfTranslations = c.getCount();
		names = new String[numberOfTranslations];
		ids = new String[numberOfTranslations];
		c.moveToFirst();
		for(int i=0; i<numberOfTranslations; ++i){
			ids[i] = Integer.toString(c.getInt(0));
			names[i] = c.getString(1);
			c.moveToNext();
		}
		c.close();
		db.close();
	}
	
	public String[] getIds(){
		return ids;
	}
	
	public String[] getNames(){
		return names;
	}
}
