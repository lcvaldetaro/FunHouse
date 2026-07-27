package com.gepetto.funhouse.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.funhouse.shared.common.AppData
import club.gepetto.utils.GcBaseActivity
import club.gepetto.utils.gCtextToSpeechHandler
import com.gepetto.funhouse.ui.main.MainView

class FunHouseActivity : GcBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        AppData.ttsHandle = gCtextToSpeechHandler(getApplicationContext())

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val permissionsList = mutableListOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                permissionsList.add(android.Manifest.permission.NEARBY_WIFI_DEVICES)
            }
            // ACCESS_LOCAL_NETWORK is a runtime permission required starting in Android 17 (SDK 37)
            permissionsList.add("android.permission.ACCESS_LOCAL_NETWORK")

            val neededPermissions = permissionsList.filter {
                checkSelfPermission(it) != android.content.pm.PackageManager.PERMISSION_GRANTED
            }.toTypedArray()
            if (neededPermissions.isNotEmpty()) {
                requestPermissions(neededPermissions, 101)
            }
        }

        setContent {
            AppData.darkMode = isSystemInDarkTheme()
            MainView()
        }
    }
}
