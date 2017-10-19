package com.yh.yhchanneledit.Application;

import android.app.Application;
import android.view.WindowManager;

/**
 * Created by YH on 2017/10/13.
 */

public class YHApplication extends Application {
    private static YHApplication application;
    private WindowManager windowManager;

    public static YHApplication getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }
}
