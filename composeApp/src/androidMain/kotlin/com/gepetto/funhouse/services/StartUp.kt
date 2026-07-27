package com.gepetto.funhouse.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import club.gepetto.GcLog

class StartUp : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        GcLog.d("Starting the Startup Service ")
        /*
        if (AppData.running_as_service) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction("com.gepetto.funhouse.services.GameService");
            serviceIntent.setPackage("com.gepetto.funhouse");
            context.startService(serviceIntent);
            Log.d("Startup", "Starting the Game Service");
        }
         */
    }
}