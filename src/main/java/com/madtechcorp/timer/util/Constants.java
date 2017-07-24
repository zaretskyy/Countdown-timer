

package com.madtechcorp.timer.util;

//import android.net.Uri;

/** Used to hold constants. */
public final class Constants {


    public static final int TIMER_INTERVAL = 100;
    public static final int BLINK_INTERVAL = 500;
    public static final boolean TESTING = Boolean.FALSE;
    public static final int TEST_TIMER_DURATION = 5000;
    public static final boolean DISPLAY_MILLI = true;
    public static final String FONT = "fonts/huamifont.ttf";

    public static final String TIMER_HOURS = "Hours";
    public static final String TIMER_MINUTES = "Mins";
    public static final String TIMER_REMAINING_DURATION = "Remaining_Duration";
    public static final String TIMER_DURATION = "Timer_Duration";
    public static final String TIMER_START_TIME = "Start_Time";
    public static final String TIMER_RUNNING = "Running";
    public static final String TIMER_PAUSED = "Paused";
    public static final String TIMER_DONE_RESULT = "Timer_Done";
    public static final int OPEN_TIMER = 1;
    public static final int DISMISS_TIMER = 2;
    public static final long AUTO_DISMISS_DURATION = 30000;

//    public static final String DATA_ITEM_PATH = "/timer";
//    public static final Uri URI_PATTERN_DATA_ITEMS =
//            Uri.fromParts("wear", DATA_ITEM_PATH, null);

    public static final String ACTION_SHOW_TIMER = "com.madtechcorp.timer.ACTION_SHOW_TIMER";
    public static final String TIMER_WAKE_LOCK = "com.madtechcorp.timer.TIMER_WAKE_LOCK";

    private Constants() {
    }

}
