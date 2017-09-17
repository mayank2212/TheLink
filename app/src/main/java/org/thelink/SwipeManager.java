package org.thelink;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mayank on 20-09-2016.
 */
public class SwipeManager {

    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor ;
    Context context ;


    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String Preference_swipe = "swipe-check";

    private static final String IS_SWIPE = "isswipe";


    public SwipeManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Preference_swipe, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void SetSwipeStatus(boolean isFirstTime) {
        editor.putBoolean(IS_SWIPE, isFirstTime);
        editor.commit();
    }

    public boolean getSwipeStatus() {
        return sharedPreferences.getBoolean(IS_SWIPE, true);
    }
}
