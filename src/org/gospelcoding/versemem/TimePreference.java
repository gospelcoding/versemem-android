package org.gospelcoding.versemem;

/* TimePreference class by Mark Murphy: https://github.com/commonsguy */

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {
    private int lastHour=0;
    private int lastMinute=0;
    private TimePicker picker=null;

    private static final int TIME_PICKER_INTERVAL=15;
    private boolean mIgnoreEvent=false;
    
    /*Time Picker with 15 minute intervals by Mark Horgan */
    private TimePicker.OnTimeChangedListener mTimePickerListener=new TimePicker.OnTimeChangedListener(){
        public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute){
            if (mIgnoreEvent)
                return;
            if (minute%TIME_PICKER_INTERVAL!=0){
                int minuteFloor=minute-(minute%TIME_PICKER_INTERVAL);
                minute=minuteFloor + (minute==minuteFloor+1 ? TIME_PICKER_INTERVAL : 0);
                if (minute==60)
                    minute=0;
                mIgnoreEvent=true;
                timePicker.setCurrentMinute(minute);
                mIgnoreEvent=false;
            }

        }
    };
    
    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
        
        
    }

    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public int getHour(){
    	return lastHour;
    }
    
    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }
    
    public int getMinute(){
    	return lastMinute;
    }

//    public boolean isBefore(TimePreference pref2){
//    	Log.e("TP", "Comparing "+lastHour+":"+lastMinute+", "+pref2.getHour()+":"+pref2.getMinute());
//    	if(lastHour == pref2.getHour())
//    		return (lastMinute < pref2.getMinute());
//    	return (lastHour < pref2.getHour());
//    }
    
    public boolean isBefore(String time2){
    	if(lastHour == getHour(time2))
    		return (lastMinute < getMinute(time2));
    	return (lastHour < getHour(time2));
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected View onCreateDialogView() {
        picker=new TimePicker(getContext());
        picker.setOnTimeChangedListener(mTimePickerListener);

        return(picker);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastHour=picker.getCurrentHour();
            lastMinute=picker.getCurrentMinute();

            String time=String.valueOf(lastHour)+":"+String.valueOf(lastMinute);

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("00:00");
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            time=defaultValue.toString();
        }

        lastHour=getHour(time);
        lastMinute=getMinute(time);
    }
}