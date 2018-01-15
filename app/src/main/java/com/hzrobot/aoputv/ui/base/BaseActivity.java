package com.hzrobot.aoputv.ui.base;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.hzrobot.aoputv.global.GlobalApplication;
import com.hzrobot.aoputv.model.event.RobotCommand;
import com.hzrobot.aoputv.ui.act.GalleryActivity;
import com.hzrobot.aoputv.ui.act.VideoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by shijiwei on 2018/1/5.
 *
 * @VERSION 1.0
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        GlobalApplication.getApplication().addAct2Stack(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initialData(savedInstanceState);
        initialView();
        initialEvn();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        GlobalApplication.getApplication().removeActFromStack(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        onBackKeyPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRobotCommand(RobotCommand cmd) {

        if (cmd == null) return;

        if (cmd.getCommand().equals("退出应用") || cmd.getCommand().equals("退出程序")) {
            GlobalApplication.getApplication().exit();
            return;
        } else if (cmd.getCommand().equals("打开相册") || cmd.getCommand().equals("返回相册")) {
            startActivity(GalleryActivity.class);
            return;
        } else if (cmd.getCommand().equals("播放视频")) {
            startActivity(VideoActivity.class);
            return;
        }

        if (isActivityAtStackTop())
            onReceiveRobotCommand(cmd);
    }

    /**
     * Gets the resource id of the layout file for the current page
     * invoked in the onCreate () of the life cycle
     *
     * @return
     */
    public abstract int getLayoutResId();

    /**
     * Initialize data and declare data collections,invoked in the onCreate () of the life cycle
     */
    public abstract void initialData(Bundle savedInstanceState);

    /**
     * Initialize the view widget,invoked in the onCreate () of the life cycle
     */
    public abstract void initialView();

    /**
     * Initializes the view widget response callback event,invoked in the onCreate () of the life cycle
     */
    public abstract void initialEvn();

    /**
     * Interceptor system returns a key event
     */
    public abstract void onBackKeyPressed();

    /**
     * Monitor the network status of the device
     */
    public abstract void onNetworkStateChanged(int type, boolean isAvailable);


    public abstract void onReceiveRobotCommand(RobotCommand cmd);


    /**
     * 程序是否在前台运行
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public boolean isAppOnForeground(Context ctx) {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) ctx.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = ctx.getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    /**
     * 当前activity是否处于栈顶
     *
     * @return
     */
    public boolean isActivityAtStackTop() {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(getClass().getName());
    }

    public void startActivity(Class cls) {
        startActivity(new Intent(this, cls));
    }

}
