package com.namoo.plus.jejurizmapp.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by HeungSun-AndBut on 2016. 7. 27..
 */

public class JPreference {

    private static final String J_PREFERENCE = "j_preference";
    private static final String PREFERENCE_EXPLANATION_READ_CHECK = "preference_explanation_read_check";

    private static JPreference instance;
    private Context mContext;
    private int mode = Activity.MODE_PRIVATE;

    public static JPreference getInstance(Context context) {
        if(instance == null) {
            instance = new JPreference(context);
        }
        return instance;
    }

    public JPreference(Context mContext) {
        this.mContext = mContext;
    }

    public void setReadExplanation() {
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences(J_PREFERENCE, mode);
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        editor.putBoolean(PREFERENCE_EXPLANATION_READ_CHECK, true);

        editor.commit();
    }

    public boolean getReadExplanation() {
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences(J_PREFERENCE, mode);
        return mySharedPreferences.getBoolean(PREFERENCE_EXPLANATION_READ_CHECK, false);
    }



}
