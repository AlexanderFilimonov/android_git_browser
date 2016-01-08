package com.afilimonov.gitbrowser;

import com.afilimonov.gitbrowser.utils.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: afilimonov
 * Date: 04.12.2015
 * Time: 16:52
 */
public class Application extends android.app.Application implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler currentHandler;

    public void onCreate() {
        currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

        super.onCreate();
    }

    public void onTerminate() {
        super.onTerminate();
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        Logger.e(throwable.getMessage(), throwable);
        currentHandler.uncaughtException(thread, throwable);
    }
}
