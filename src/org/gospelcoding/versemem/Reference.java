package org.gospelcoding.versemem;

public class Reference{
	public String book;
	public int chapter1;
	public int chapter2;
	public int verse1;
	public int verse2;
	
	public Reference(){
		book = "";
		chapter1 = chapter2 = verse1 = verse2 = 0;
	}
	
	public Reference(String ref){
		int spaceIndex = ref.indexOf(' ');
		int colon1Index = ref.indexOf(':');
		int dashIndex = ref.indexOf('-');
		int colon2Index = ref.indexOf(':', colon1Index+1);
		
		book = ref.substring(0, spaceIndex);
		chapter1 = Integer.parseInt(ref.substring(spaceIndex+1, colon1Index));
		if(dashIndex > 0){  //multiverse
			verse1 = Integer.parseInt(ref.substring(colon1Index+1, dashIndex));
			if(colon2Index > 0){  //multichapter
				chapter2 = Integer.parseInt(ref.substring(dashIndex+1, colon2Index));
				verse2 = Integer.parseInt(ref.substring(colon2Index+1));
			}
			else{
				chapter2 = chapter1;
				verse2 = Integer.parseInt(ref.substring(dashIndex+1));
			}
		}
		else{
			verse1 = Integer.parseInt(ref.substring(colon1Index+1));
			chapter2 = chapter1;
			verse2 = verse1;
		}
	}
	
	public boolean isFirstVerseOf(Reference ref){
		if(book.equals(ref.book) &&
				chapter1 == ref.chapter1 &&
				verse1 == ref.verse1)
			return true;
		return false;
	}
	
	public boolean isLastVerseOf(Reference ref){
		if(book.equals(ref.book) &&
				chapter1 == ref.chapter2 &&
				verse1 == ref.verse2)
			return true;
		return false;
	}
	
	public static String makeReferenceString(String book, int chapter1, int verse1){
		return makeReferenceString(book, chapter1, chapter1, verse1, verse1);
	}
	
	public static String makeReferenceString(String book, int chapter1, int verse1, int verse2){
		return makeReferenceString(book, chapter1, chapter1, verse1, verse2);
	}
	
	public static String makeReferenceString(String book, int chapter1, int chapter2, int verse1, int verse2){
		String verseRef = book + " " + chapter1 + ":" + verse1;
		if(chapter2 != chapter1 || verse2 != verse1){
			verseRef += "-";
			if(chapter2 != chapter1){
				verseRef += chapter2 + ":";
			}
			verseRef += verse2;
		}
		return verseRef;
	}
	
	public static String mergeRefs(String refString1, String refString2){
		Reference ref1 = new Reference(refString1);
		Reference ref2 = new Reference(refString2);
		ref1.chapter2 = ref2.chapter2;
		ref1.verse2 = ref2.chapter2;
		return makeReferenceString(ref1.book, ref1.chapter1, ref1.chapter2, ref1.verse1, ref1.verse2);
	}
	
	public Reference nextVerse(DbHelper dbhelper){
		Reference nextRef = new Reference();
		nextRef.book = book;
		int lastVerse = dbhelper.getNumberOfVerses(book, chapter2);
		if(verse2 == lastVerse){
			int lastChapter = dbhelper.getNumberOfChapters(book);
			if(lastChapter == chapter2)
				return null;
			nextRef.chapter1 = chapter2 + 1;
			nextRef.verse1 = 1;
		}
		else{
			nextRef.chapter1 = chapter2;
			nextRef.verse1 = verse2 + 1;
		}
		return nextRef;
	}
	
	public Reference previousVerse(DbHelper dbhelper){
		if(chapter1 == 1 && verse1 == 1)
			return null;
		Reference previousRef = new Reference();
		previousRef.book = book;
		if(verse1 == 1){
			previousRef.chapter1 = chapter1 - 1;
			previousRef.verse1 = dbhelper.getNumberOfVerses(book, chapter1-1);
		}
		else{
			previousRef.chapter1 = chapter1;
			previousRef.verse1 = verse1 - 1;
		}
		return previousRef;
	}
}
