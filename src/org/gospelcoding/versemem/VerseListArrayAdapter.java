package org.gospelcoding.versemem;

import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

public class VerseListArrayAdapter extends ArrayAdapter<Verse> {
	
	private Context context;
	private List<Verse> verses;
	private int selectedItem;
	//private boolean editingSelected = false;
	//private String editingText = null;
	
	public VerseListArrayAdapter(Context context, List<Verse> verses){
		super(context, R.layout.verse_list_item, verses);
		this.context = context;
		this.verses = verses;
		this.selectedItem = -1; //nothing is selected yet
	}
	
//	public void cancelEdit(){
//		editingSelected = false;
//		notifyDataSetChanged();
//	}
	
	public void deleteSelectedItem(){
		verses.get(selectedItem).delete(new DbHelper(context));
		verses.remove(selectedItem);
		selectedItem = -1;
		notifyDataSetChanged();
	}
	
//	public void editSelectedItem(){
//		editingSelected = true;
//		notifyDataSetChanged();
//	}
	
	private View fillOutNormalPart(View rowView, Verse v, ViewGroup parent){
		TextView verseRef = (TextView) rowView.findViewById(R.id.text_verse_ref);
		TextView verseStatus = (TextView) rowView.findViewById(R.id.text_verse_status);
		TextView verseProgress = (TextView) rowView.findViewById(R.id.text_progress_bar);
		verseRef.setText(v.getReference());
		verseStatus.setText(v.getStatusString() + '\n' + v.getProgressText());
		int progressWidth = (int) (parent.getWidth() * v.getProgress());
		verseProgress.getLayoutParams().width = progressWidth;
		verseProgress.setBackgroundColor(Color.parseColor(v.getProgressColor()));
		return rowView;
	}
	
//	public View getEditSelectedView(int position, ViewGroup parent){
//		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View rowView = inflater.inflate(R.layout.verse_list_edit_selected_item, parent, false);
//		Verse v = verses.get(position);
//		rowView = fillOutNormalPart(rowView, v, parent);
//		EditText verseBody = (EditText) rowView.findViewById(R.id.edit_text_verse_body);
//		if(editingText != null){
//			verseBody.setText(editingText);
//			editingText = null;
//		}
//		else{
//			verseBody.setText(v.getBody());
//		}
//		verseBody.requestFocus();
//		return rowView;
//	}
	
	public View getNormalView(int position, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.verse_list_item, parent, false);
		Verse v = verses.get(position);
		return fillOutNormalPart(rowView, v, parent);	
	}
	
	public Verse getSelectedVerse(){
		if(selectedItem >= 0){
			return verses.get(selectedItem);
		}
		else{
			return null;
		}
	}
	
	public View getSelectedView(int position, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.verse_list_selected_item, parent, false);
		Verse v = verses.get(position);
		rowView = fillOutNormalPart(rowView, v, parent);
		TextView verseBody = (TextView) rowView.findViewById(R.id.text_verse_body);
		verseBody.setText(v.getBody());
		if(v.isMergeable(new DbHelper(context))){
			View mergeButton = rowView.findViewById(R.id.button_merge_verse);
			mergeButton.setVisibility(View.VISIBLE);
			//mergeButton.setLayoutParams(new TableLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
		}
		return rowView;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		if(position == selectedItem){
			return getSelectedView(position, parent);
		}
		else{
			return getNormalView(position, parent);
		}
	}
	
	public void orientationChanged(){
		notifyDataSetChanged();
	}
	
	public void saveEdit(String newBody){
		verses.get(selectedItem).editBody(newBody, new DbHelper(context));
//		editingSelected = false;
		notifyDataSetChanged();
	}
	
	public void setSelectedItem(int position){
//		editingSelected = false;
		if(position == selectedItem){
			selectedItem = -1;
		}
		else{
			selectedItem = position;
			notifyDataSetChanged();
		}
	}
}
