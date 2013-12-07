package org.gospelcoding.versemem;

public class Book {

	public static final String BOOKS_TABLE = "books";
	public static final String ID_COLUMN = "_id";
	public static final String NAME_COLUMN = "name";
	public static final String TRANSLATION_ID_COLUMN = "translation_id";
	
	private String name;
	private int id;
	
	public Book(int book_id, String book_name){
		name = book_name;
		id = book_id;
	}

	public int getId(){ return id; }
	public String getName(){ return name; }
	public int getNumber(){ return id; }
	public String getWebName(){
		return name.replace(" ", "-");
	}
	public String toString(){ return name; }
}
