package com.madtechcorp.timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.graphics.drawable.AnimationDrawable;
import android.widget.TextView;


import com.madtechcorp.timer.util.Constants;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TimerDoneActivity extends Activity {

    private Vibrator v;
    private long[] vibratePattern = {50, 200, 50, 200, 50, 200, 500};
    private Timer countdownTimer;
    private TimerTask countdownTimerTask;

    @Override
    public void onCreate(Bundle savedInstanceState){

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // hide the status bar and other OS-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.PARTIAL_WAKE_LOCK, Constants.TIMER_WAKE_LOCK);
        wl.acquire(5000);

        setContentView(R.layout.timerdone_activity);

        TextView currentTime = (TextView) findViewById(R.id.current_time);

        Calendar cal = Calendar.getInstance();
        int hours = cal.get(Calendar.HOUR);
        int mins = cal.get(Calendar.MINUTE);

        currentTime.setText(String.format("%02d",hours)+":"+String.format("%02d",mins));

        ImageView ivLoader = (ImageView) findViewById(R.id.alarm_animation);
        ivLoader.setBackgroundResource(R.drawable.alarm_animation);

        final AnimationDrawable frameAnimation = (AnimationDrawable) ivLoader.getBackground();
        frameAnimation.setOneShot(Boolean.FALSE);
        frameAnimation.start();

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(vibratePattern, 0);

        setupAutoDismiss();

        findViewById(R.id.restart_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismissTimer(Constants.OPEN_TIMER, Boolean.TRUE);
            }

        });

        findViewById(R.id.open_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismissTimer(Constants.OPEN_TIMER, Boolean.FALSE);
            }

        });

        findViewById(R.id.dimiss_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismissTimer(Constants.DISMISS_TIMER, Boolean.FALSE);
            }
        });
    }

    private void dismissTimer(int timerDoneResult, boolean restartTimer) {
        v.cancel();
        countdownTimerTask.cancel();
        countdownTimer.cancel();
        countdownTimer.purge();
        Intent timerActivity = new Intent(getApplicationContext(), TimerActivity.class);
        timerActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        timerActivity.putExtra(Constants.TIMER_DONE_RESULT, timerDoneResult);
        TimerActivity.restartTimer = restartTimer;
        getApplicationContext().startActivity(timerActivity);
        finish();
    }

    private void setupAutoDismiss() {

        countdownTimer = new Timer();

        countdownTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissTimer(Constants.DISMISS_TIMER, Boolean.FALSE);
                    }
                });
            }
        };

        countdownTimer.schedule(countdownTimerTask, Constants.AUTO_DISMISS_DURATION);
    }
}