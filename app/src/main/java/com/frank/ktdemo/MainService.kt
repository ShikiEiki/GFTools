package com.frank.ktdemo

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by FH on 2017/8/1.
 */
class MainService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

    }
}