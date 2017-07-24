package com.madtechcorp.timer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.os.Vibrator;
import android.widget.TimePicker;

import com.madtechcorp.timer.util.Constants;

public class PickerActivity extends Activity {

    private TimePicker timerPicker;
    private Vibrator v;

    @Override
    public void onCreate(Bundle savedInstanceState){

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // hide the status bar and other OS-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_picker);

        timerPicker = (TimePicker) findViewById(R.id.timePicker);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        timerPicker.setIs24HourView(Boolean.TRUE);
        timerPicker.setCurrentHour(getIntent().getIntExtra(Constants.TIMER_HOURS,TimerActivity.DEFAULT_HOURS));
        timerPicker.setCurrentMinute(getIntent().getIntExtra(Constants.TIMER_MINUTES, TimerActivity.DEFAULT_MINUTES));

        findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!(timerPicker.getCurrentHour() == 0 && timerPicker.getCurrentMinute() == 0)) {
                    Intent timerActivity = new Intent();
                    timerActivity.putExtra(Constants.TIMER_HOURS, timerPicker.getCurrentHour());
                    timerActivity.putExtra(Constants.TIMER_MINUTES, timerPicker.getCurrentMinute());
                    setResult(Activity.RESULT_OK, timerActivity);
                    finish();
                }
                else{
                    v.vibrate(500);
                }
            }

        });
    }
}