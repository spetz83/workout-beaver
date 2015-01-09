package me.spetz83.workoutbeaver;

import android.app.Application;

import me.spetz83.workoutbeaver.modules.EventBusModule;

/**
 * Created by Tom on 1/9/2015.
 */
public class WBApp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        // Register modules here
        Object[] modules = new Object[]{new EventBusModule()};
        DaggerInjector.init(modules);
    }
}
