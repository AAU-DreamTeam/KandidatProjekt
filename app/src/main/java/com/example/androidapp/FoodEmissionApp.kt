package com.example.androidapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import com.example.androidapp.views.ScannerActivity

class FoodEmissionApp: Application(), Application.ActivityLifecycleCallbacks {
    private val activities = mutableListOf<Activity>()
    private lateinit var handler: Handler
    private val delayShort = 2000L
    private val delayLong = 60000L
    private val callback = Runnable {
        for (activity in activities) {
            activity.finish()
        }
    }

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(this)
        handler = Handler(mainLooper)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activities.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        handler.removeCallbacks(callback)
    }

    override fun onActivityResumed(activity: Activity) {
        // Do nothing
    }

    override fun onActivityPaused(activity: Activity) {
        // Do nothing
    }

    override fun onActivityStopped(activity: Activity) {
        if (ScannerActivity.takingPicture) {
            handler.postDelayed(callback, delayLong)
        } else {
            handler.postDelayed(callback, delayShort)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // Do nothing
    }

    override fun onActivityDestroyed(activity: Activity) {
        activities.remove(activity)
    }
}