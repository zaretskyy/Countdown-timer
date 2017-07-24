package com.madtechcorp.timer;

import android.app.IntentService;
import android.content.Intent;

import com.madtechcorp.timer.util.Constants;

/**
 * Service class that manages notifications of the timer.
 */
public class TimerNotificationService extends IntentService {

    public static final String TAG = "TimerNotificationSvc";

    public TimerNotificationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String action = intent.getAction();
        if (Constants.ACTION_SHOW_TIMER.equals(action)) {
            showTimerDoneNotification();
        } else {
            throw new IllegalStateException("Undefined constant used: " + action);
        }
    }


    private void showTimerDoneNotification() {

        Intent timerDone = new Intent(getApplicationContext(), TimerDoneActivity.class);
        timerDone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(timerDone);
    }
}