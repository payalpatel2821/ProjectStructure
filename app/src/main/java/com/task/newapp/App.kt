package com.task.newapp

import android.content.Context
import android.graphics.Typeface
import android.net.TrafficStats
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import androidx.multidex.MultiDexApplication
import com.appizona.yehiahd.fastsave.FastSave
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.task.newapp.models.SendUserDetailSocket
import com.task.newapp.models.User
import com.task.newapp.utils.Constants
import com.task.newapp.utils.GlideImageLoader
import com.task.newapp.utils.joinSocket
import com.task.newapp.utils.showLog
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider
import io.realm.Realm
import io.realm.RealmConfiguration
import lv.chi.photopicker.ChiliPhotoPicker


class App : MultiDexApplication() {

    companion object {

        @JvmField
        var appInstance: App? = null

        @JvmField
        var realm: Realm? = null

        @JvmField
        var socket: Socket? = null

        lateinit var fastSave: FastSave

        @JvmStatic
        fun getAppInstance(): App {
            return appInstance as App
        }

        @JvmStatic
        fun getRealmInstance(): Realm {
            return realm as Realm
        }

        @JvmStatic
        fun getSocketInstance(): Socket {
            return socket as Socket
        }

        fun setFont(context: Context, str: String?): Typeface? {
            return Typeface.createFromAsset(context.assets, str)
        }

    }

    val TAG = App::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        TrafficStats.setThreadStatsTag(1000)

        //FastSave pref lib initialization
        FastSave.init(this)
        fastSave = FastSave.getInstance()
        //Realm DB initialization
        Realm.init(this)
        setRealmConfig()
        EmojiManager.install(IosEmojiProvider())
        //ImagePicker lib initialization
        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "$packageName.fileprovider"
        )
        joinSocket()
        setupStrictMode()
    }



    private fun setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(ThreadPolicy.Builder().detectAll().permitDiskWrites().permitDiskReads().penaltyLog().build())
            StrictMode.setVmPolicy(VmPolicy.Builder().detectAll().penaltyLog().build())
        }
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
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