package com.newproject.pace.ui;

import android.app.Application;
import android.content.Context;

import com.newproject.pace.Model.AppPreferences;


//import io.branch.referral.Branch;

public class DominatorApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        DominatorApplication.context = this;
        AppPreferences.getInstance().init();

//        Branch.enableLogging();
//
//        // Branch object initialization
//        Branch.getAutoInstance(this);
    }

    public static Context getAppContext() {
        return DominatorApplication.context;
    }
}