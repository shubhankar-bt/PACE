package com.newproject.pace.Model;

import android.content.SharedPreferences;

import com.newproject.pace.ui.DominatorApplication;


public class AppPreferences {
    private static final String PREFERENCE_NAME = "dominator";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String IS_FIRST_TIME = "isFirstTime";
    private static final int idCount = 1;

    /* Singleton Class */
    private static AppPreferences instance = new AppPreferences();

    private AppPreferences() {

    }

    public static AppPreferences getInstance() {
        return instance;
    }

    public void init() {
        boolean isFirstTime = getIsFirstTime();
        if (isFirstTime) {
            setIsFirstTime(false);
        }
    }




    private boolean getIsFirstTime() {
        boolean value = true;
        if (getPref() != null && getPref().contains(IS_FIRST_TIME)) {
            value = getPref().getBoolean(IS_FIRST_TIME, true);
        }
        return value;
    }

    private void setIsFirstTime(boolean value) {
        getEditor().putBoolean(IS_FIRST_TIME, value);
        getEditor();
    }

    public SharedPreferences getPref() {
        if (pref == null) {
            int PRIVATE_MODE = 0;
            pref = DominatorApplication.getAppContext().getSharedPreferences(PREFERENCE_NAME, PRIVATE_MODE);
        }
        return pref;
    }

    public SharedPreferences.Editor getEditor() {
        if (editor == null) {
            editor = getPref().edit();
        }
        return editor;
    }
}
