package com.hzrobot.aoputv.global;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shijiwei on 2017/12/18.
 *
 * @VERSION 1.0
 */


public class GlobalApplication extends Application {

    private static final String TAG = "GlobalApplication";

    public static List<Activity> actStack = new ArrayList<>();
    private static GlobalApplication application = new GlobalApplication();


    @Override
    public void onCreate() {
        super.onCreate();
        createAopuDir(new String[]{
                Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.DIR_gallery,
                Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.DIR_video
        });
    }

    private void createAopuDir(String[] dirs) {

        for (String dirPath :
                dirs) {
            File dir = new File(dirPath);
            if (!dir.exists()) dir.mkdirs();
        }
    }

    /**
     * Get a singleton for your application
     *
     * @return
     */
    public static GlobalApplication getApplication() {
        if (application == null) application = new GlobalApplication();
        return application;
    }

    /**
     * Add activities to stack
     *
     * @param act
     */
    public void addAct2Stack(Activity act) {
        if (!actStack.contains(act))
            actStack.add(act);
    }

    /**
     * Remove activities from stack
     *
     * @param act
     */
    public void removeActFromStack(Activity act) {
        if (actStack.contains(act)) {
            actStack.remove(act);
        }
    }


    /**
     * Exit the application
     */
    public void exit() {
        for (Activity act : actStack) if (act != null && !act.isFinishing()) act.finish();
    }

}
