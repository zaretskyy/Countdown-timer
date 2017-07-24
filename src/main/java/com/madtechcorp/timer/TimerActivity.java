package com.madtechcorp.timer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import com.madtechcorp.timer.util.TimerFormat;
import com.madtechcorp.timer.util.Constants;



public class TimerActivity extends Activity {

    public static int DEFAULT_HOURS = 0;
    public static int DEFAULT_MINUTES = 5;
    public static boolean restartTimer = false;

    private Timer CountdownTimer, blinkTimer;
    private TimerTask countdownTimerTask, blinkTimerTask;
    private int hour, min;
    private long remainingDuration, startTime, timerDuration;


    private boolean timerRunning = false;
    private boolean timerPaused = false;
    private boolean timerVisible = true;
    private boolean setManually= false;

    private Button startButton, stopButton, resetButton,continueButton;
    private TextView timeView,timeViewMilli;
    private ImageView timerBackground;
    private View timerMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // hide the status bar and other OS-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeractivity);

        timeView = (TextView) findViewById(R.id.stopwatch_laptime);
        timeViewMilli = (TextView) findViewById(R.id.stopwatch_laptime_milli);
        startButton = (Button) findViewById(R.id.start_button);
        resetButton = (Button) findViewById(R.id.reset_button);
        stopButton = (Button) findViewById(R.id.stop_button);
        continueButton = (Button)findViewById(R.id.continue_button);
        timerBackground = (ImageView) findViewById(R.id.timer_bg);
        timerMainLayout = findViewById(R.id.timer_main_layout);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), Constants.FONT);
        timeView.setTypeface(custom_font, Typeface.BOLD);
        timeViewMilli.setTypeface(custom_font, Typeface.BOLD);
        timeView.setTextSize(getResources().getDimension(R.dimen.timer_mins_size));
        timeViewMilli.setTextSize(getResources().getDimension(R.dimen.timer_milli_size));

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startTimer();
            }

        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startTimer();
            }

        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stopTimer();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                resetTimer();
            }
        });

        timeView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                selectTimerDuration();
            }
        });

        hour = getIntent().getIntExtra(Constants.TIMER_HOURS, DEFAULT_HOURS);
        min = getIntent().getIntExtra(Constants.TIMER_MINUTES, DEFAULT_MINUTES);

        timerMainLayout.setOnTouchListener(new OnSwipeTouchListener(TimerActivity.this){

            public void onSwipeLeft() {
                selectTimerDuration();
            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_FIRST_USER && resultCode == Activity.RESULT_OK) {
            DEFAULT_HOURS = hour = data.getIntExtra(Constants.TIMER_HOURS, DEFAULT_HOURS);
            DEFAULT_MINUTES = min = data.getIntExtra(Constants.TIMER_MINUTES, DEFAULT_MINUTES);
            setManually = true;
            initializeTimerDuration();
            setupView(timerDuration);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putInt(Constants.TIMER_HOURS, hour);
        outState.putInt(Constants.TIMER_MINUTES, min);
        outState.putLong(Constants.TIMER_START_TIME, startTime);
        outState.putLong(Constants.TIMER_DURATION, timerDuration);
        outState.putLong(Constants.TIMER_REMAINING_DURATION, remainingDuration);
        outState.putBoolean(Constants.TIMER_RUNNING,timerRunning);
        outState.putBoolean(Constants.TIMER_PAUSED, timerPaused);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle inState){
        hour = inState.getInt(Constants.TIMER_HOURS);
        min = inState.getInt(Constants.TIMER_MINUTES);
        startTime = inState.getLong(Constants.TIMER_START_TIME);
        remainingDuration = inState.getLong(Constants.TIMER_REMAINING_DURATION);
        timerDuration = inState.getLong(Constants.TIMER_DURATION);
        timerRunning = inState.getBoolean(Constants.TIMER_RUNNING);
        timerPaused = inState.getBoolean(Constants.TIMER_PAUSED);
        super.onRestoreInstanceState(inState);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (timerRunning){
            stopCountdownTimer();
        }

        if (timerPaused){
            stopBlinkingTimer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int timerDoneResult = getIntent().getIntExtra(Constants.TIMER_DONE_RESULT, 0);

        if (timerDoneResult == Constants.OPEN_TIMER) {
            if (restartTimer) {
                restartTimer = false;
                timerRunning = false;
                timerPaused = false;
                initializeTimerDuration();
                startTimer();
            } else {
                setupViewOnResume();
            }
        } else if (timerDoneResult == Constants.DISMISS_TIMER) {
            finish();
        }
        else {
            setupViewOnResume();
        }
    }

    private void setupViewOnResume() {
        if (timerRunning) {
            if (!timerPaused) {
                remainingDuration = timerDuration - (System.currentTimeMillis() - startTime);
                if (remainingDuration > 0) {
                    setupCountDownTimer();
                    timerRunning = true;
                    timerPaused = false;
                    CountdownTimer.scheduleAtFixedRate(countdownTimerTask, 0, Constants.TIMER_INTERVAL);
                    setupView(remainingDuration);
                }
                else {
                    timerRunning = false;
                    initializeTimerDuration();
                    setupView(timerDuration);
                }
            }
        } else {
            timerPaused = false;
            initializeTimerDuration();
            setupView(timerDuration);
        }
    }

    private void startTimer() {
        setupCountDownTimer();

        registerAlarm(timerDuration);

        timerRunning = true;
        timerPaused = false;

        remainingDuration = timerDuration;
        CountdownTimer.scheduleAtFixedRate(countdownTimerTask, 0, Constants.TIMER_INTERVAL);

        setupView(timerDuration);
    }

    private void setupCountDownTimer() {

        CountdownTimer = new Timer();

        countdownTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (timerRunning) {
                            if (remainingDuration > 0) {
                                remainingDuration = remainingDuration - Constants.TIMER_INTERVAL;
                                displayRemainingDuration(remainingDuration);
                            } else {
                                timerRunning = false;
                                stopCountdownTimer();
                                initializeTimerDuration();
                                setupView(timerDuration);
                            }
                        }
                    }
                });
            }
        };
    }

    private void registerAlarm(long duration) {
        // Get the alarm manager.
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Create intent that gets fired when CountdownTimer expires.
        Intent intent = new Intent(Constants.ACTION_SHOW_TIMER, null, this,
                TimerNotificationService.class);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Set the CountdownTimer start time
        startTime = System.currentTimeMillis();

        // Calculate the time when it expires.
        long wakeupTime =  startTime + duration;

        // Schedule an alarm.
        alarm.setExact(AlarmManager.RTC_WAKEUP, wakeupTime, pendingIntent);
    }

    private void stopTimer() {
        stopCountdownTimer();

        timerDuration = remainingDuration;

        cancelAlarm();

        timerPaused = true;
        setupView(remainingDuration);
    }

    private void resetTimer() {
        if (timerPaused)
            stopBlinkingTimer();

        stopCountdownTimer();

        cancelAlarm();

        initializeTimerDuration();

        timerRunning = false;
        timerPaused = false;
        setupView(timerDuration);
    }

    private void stopCountdownTimer() {
        countdownTimerTask.cancel();
        CountdownTimer.cancel();
        CountdownTimer.purge();
    }

    private void cancelAlarm() {

        // Get the alarm manager.
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Create intent that gets fired when CountdownTimer expires.
        Intent intent = new Intent(Constants.ACTION_SHOW_TIMER, null, this,
                TimerNotificationService.class);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Cancel the existing alarm.
        alarm.cancel(pendingIntent);

        //Reset the CountdownTimer start time
        startTime = 0;
    }

    private void selectTimerDuration() {
        if (!timerRunning) {
            Intent timerPicker = new Intent(TimerActivity.this, PickerActivity.class);
            timerPicker.putExtra(Constants.TIMER_HOURS, hour);
            timerPicker.putExtra(Constants.TIMER_MINUTES, min);
            startActivityForResult(timerPicker, Activity.RESULT_FIRST_USER);
        }
    }

    private void initializeTimerDuration() {
        timerDuration = (hour * 60 * 60 * 1000) + (min * 60 * 1000);
        if (Constants.TESTING)
            if (!setManually)
                timerDuration = Constants.TEST_TIMER_DURATION;
    }

    private void setupView(long duration){
        if (timerRunning){
            timerBackground.setBackground(getDrawable(R.drawable.timer_time_bg_on));
            startButton.setVisibility(View.INVISIBLE);
            resetButton.setVisibility(View.VISIBLE);
            if (timerPaused) {
                startBlinkingTimer();
                continueButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
            } else {
                stopBlinkingTimer();
                continueButton.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.VISIBLE);
            }
        } else {
            timerBackground.setBackground(getDrawable(R.drawable.timer_time_bg_off));
            startButton.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.INVISIBLE);
            stopButton.setVisibility(View.INVISIBLE);
            continueButton.setVisibility(View.INVISIBLE);
        }
        displayRemainingDuration(duration);
    }

    private void startBlinkingTimer() {
        blinkTimer = new Timer();
        blinkTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (timerPaused) {
                            if (timerVisible) {
                                timerVisible = false;
                                timeView.setVisibility(View.INVISIBLE);
                                timeViewMilli.setVisibility(View.INVISIBLE);
                            } else {
                                timerVisible = true;
                                timeView.setVisibility(View.VISIBLE);
                                timeViewMilli.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

            }

        };

        timerPaused = true;
        blinkTimer.scheduleAtFixedRate(blinkTimerTask, 0, Constants.BLINK_INTERVAL);
    }

    private void stopBlinkingTimer() {
        if (blinkTimer != null) {
            blinkTimerTask.cancel();
            blinkTimer.cancel();
            blinkTimer.purge();

            timerVisible = true;
            timeView.setVisibility(View.VISIBLE);
            timeViewMilli.setVisibility(View.VISIBLE);
        }
    }

    private void displayRemainingDuration(long duration) {
        timeView.setText(TimerFormat.getTimeString(duration));

        if (Constants.DISPLAY_MILLI) {
            timeViewMilli.setText(" " + String.format("%02d", (int) (duration % 1000)).substring(0, 1));
        }
        else
            timeViewMilli.setText("");

    }
}
