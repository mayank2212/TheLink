package org.thelink;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mayank on 20-07-2016.
 */
public class LoginManager_App {
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor ;
     Context context ;


    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String Preference_name = "login-check";

    private static final String IS_LOGED_IN = "islogin";

    public LoginManager_App(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Preference_name, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void SetLoginStatus(boolean isFirstTime) {
        editor.putBoolean(IS_LOGED_IN, isFirstTime);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGED_IN, true);
    }

}
