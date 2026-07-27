package com.gepetto.funhouse.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import jni.GengameKotlin
import club.gepetto.GcLog

/**
 * Created by luiz on 4/29/2017.
 */
class GameService : Service() {
    private val TAG = "GameService"
    private val mBinder: IBinder = LocalBinder()
    override fun onCreate() {
        super.onCreate()
        GcLog.d( "onCreate Called")
        startThisService()
    }

    @SuppressLint("NewApi")
    private fun startThisService() {
        val thrL = Thread(null, ServiceWorker(), "Service Worker")
        thrL.start()
    }

    private inner class ServiceWorker : Runnable {
        override fun run() {
            GengameKotlin().start()
            GcLog.d( "ServiceWorker finished")
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        GcLog.d( "onStartCommand Called")
        return START_STICKY
    }

    inner class LocalBinder : Binder() {
        val service: GameService
            get() {
                GcLog.d( "LocalBinder called")
                return this@GameService
            }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStart(intent: Intent, startId: Int) {
        super.onStart(intent, startId)
        GcLog.d( "onStart Called")
    }

    override fun onBind(intent: Intent): IBinder? {
        GcLog.d( "onBind Called")
        return mBinder
    }
}