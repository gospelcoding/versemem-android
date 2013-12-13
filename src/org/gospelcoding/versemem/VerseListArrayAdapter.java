package org.gospelcoding.versemem;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VerseListArrayAdapter extends ArrayAdapter<Verse> {
	
	private Context context;
	private List<Verse> verses;
	
	public VerseListArrayAdapter(Context context, List<Verse> verses){
		super(context, R.layout.verse_list_item, verses);
		this.context = context;
		this.verses = verses;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.verse_list_item, parent, false);
		TextView verseRef = (TextView) rowView.findViewById(R.id.text_verse_ref);
		TextView verseStatus = (TextView) rowView.findViewById(R.id.text_verse_status);
		TextView verseProgress = (TextView) rowView.findViewById(R.id.text_progress_bar);
		Verse v = verses.get(position);
		verseRef.setText(v.getReference());
		verseStatus.setText(v.getStatusString() + '\n' + v.getProgressText());
		int progressWidth = (int) (parent.getWidth() * v.getProgress());
		verseProgress.getLayoutParams().width = progressWidth;
		verseProgress.setBackgroundColor(Color.parseColor(v.getProgressColor()));
		return rowView;
	}
}
