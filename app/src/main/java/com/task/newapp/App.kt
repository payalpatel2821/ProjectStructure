package com.task.newapp

import androidx.multidex.MultiDexApplication
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.utils.GlideImageLoader
import io.realm.Realm
import io.realm.RealmConfiguration
import lv.chi.photopicker.ChiliPhotoPicker


class App : MultiDexApplication() {

    companion object {

        @JvmField
        var appInstance: App? = null

        @JvmField
        var realm: Realm? = null

        lateinit var fastSave: FastSave
        @JvmStatic
        fun getAppInstance(): App {
            return appInstance as App
        }

        @JvmStatic
        fun getRealmInstance(): Realm {
            return realm as Realm

        }
    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this

        //FastSave pref lib initialization
        FastSave.init(this)
        fastSave = FastSave.getInstance()
        //Realm DB initialization
        Realm.init(this)
        setRealmConfig()
        //ImagePicker lib initialization
        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "com.task.newapp.fileprovider"
        )
    }

    private fun setRealmConfig() {
        val config = RealmConfiguration.Builder()
            .name("Database.realm")
            .allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)
        realm = Realm.getDefaultInstance()
    }
}