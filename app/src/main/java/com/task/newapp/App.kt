package com.task.newapp

import androidx.multidex.MultiDexApplication
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.utils.GlideImageLoader
import io.realm.Realm
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

        //FastSave pref lib initialization
        FastSave.init(this)

        //Realm DB initialization
        Realm.init(this)

        //ImagePicker lib initialization
        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "com.task.newapp.fileprovider"
        )
    }

}