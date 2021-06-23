package com.task.newapp

import androidx.multidex.MultiDexApplication
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.utils.GlideImageLoader
import lv.chi.photopicker.ChiliPhotoPicker


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
        FastSave.init(this)
        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "com.task.newapp.fileprovider"
        )
    }

}