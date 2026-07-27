package com.gepetto.funhouse

import android.app.Application
import club.gepetto.gcadslib.initAnalyticsAndAds
import club.gepetto.utils.GcAppInfo
import com.funhouse.shared.common.AppData
import club.gepetto.utils.gcGetAppFolder
import club.gepetto.GcLog
import com.gepetto.funhouse.models.installAssetFiles
import com.gepetto.gamescollection.R
import java.io.File

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        GcAppInfo.initialize(this)

        AppData.applicationContext = this
        AppData.appPackage = packageName
        AppData.packageFolder = gcGetAppFolder(this)
        AppData.packageFolderFile = File(AppData.packageFolder)
        AppData.gameFolder = Constants.GAMES_FOLDER
        val gameFolderFile = File("${AppData.packageFolder}/${AppData.gameFolder}")
        AppData.gameFolderFile = gameFolderFile
        gameFolderFile.mkdir()
        AppData.appName = this.getString(R.string.app_name)

        val versionString = GcAppInfo.versionName
        val versionBuild  = GcAppInfo.versionCode ?: 0L

        AppData.version = this.getString(R.string.version_build_format, versionString, versionBuild)
        AppData.versionCode = versionBuild
        AppData.releaseVersion = GcAppInfo.releaseVersion
        AppData.secretGamesEnabled = !AppData.releaseVersion

        GcLog.plant(GcLog.DebugTree())

        installAssetFiles()

        GcLog.d("Game Folder = ${AppData.packageFolder}")
        GcLog.d("Game Dir = ${AppData.gameFolder}")

        //initOkHttp()

        initAnalyticsAndAds(this)
    }
}