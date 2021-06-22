package com.task.newapp

import androidx.multidex.MultiDexApplication


class App : MultiDexApplication() {

    companion object {

        @JvmField
        var appInstance: App? = null

        @JvmStatic
        fun getAppInstance(): App {
            return appInstance as App
        }
    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

}